package com.whatswater.curd.project.sys.permission;


import cn.hutool.core.util.StrUtil;
import com.whatswater.curd.project.common.CrudUtils;
import com.whatswater.curd.project.sys.admin.Admin;
import com.whatswater.curd.project.sys.admin.AdminService;
import com.whatswater.curd.project.sys.employee.Employee;
import com.whatswater.curd.project.sys.menu.Menu;
import com.whatswater.curd.project.sys.menu.MenuService;
import io.vertx.core.Future;
import org.apache.commons.codec.digest.DigestUtils;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class UserTokenService {
    public static final int RANDOM_SALT_LENGTH = 20;
    private final Map<String, UserToken> userTokenMap = new ConcurrentHashMap<>();
    private MenuService menuService;

    /**
     * 获取当前token的用户信息
     * @param token
     * @return
     */
    public UserToken getUserToken(String token) {
        return userTokenMap.get(token);
    }

    public Future<List<Menu>> getAuthorizedMenuList(String token) {
        UserToken userToken = userTokenMap.get(token);
        if (userToken.isAdmin()) {
            return menuService.queryAllMenuList();
        }
        // 此处需要串行执行
        final Employee employee = userToken.getEmployee();
//        return menuService.queryAllMenuList().compose(menuList -> {
//            for (Menu menu: menuList) {
//                String permission = menu.getPermission();
//                if (StrUtil.isEmpty(permission)) {
//
//                }
//                permissionFilterService.queryByPermission(permission);
//            }
//        });
        return menuService.queryAllMenuList();
    }


    /**
     * 当前token是否拥有某功能权限
     * @param userToken 用户token
     * @param permission 功能资源
     * @return 是否拥有某功能权限
     */
    public Future<Boolean> hasPermission(UserToken userToken, String permission) {
        return Future.succeededFuture(true);
    }

    public UserToken newToken(Admin admin, Employee employee) {
        UserToken userToken = UserToken.newAdminToken(admin, employee, StrUtil.EMPTY, AdminService.ttl);
        resetToken(userToken);

        userTokenMap.put(userToken.getToken(), userToken);
        return userToken;
    }

    public UserToken newToken(Employee employee) {
        UserToken userToken = UserToken.newEmployeeToken(employee, StrUtil.EMPTY, AdminService.ttl);
        resetToken(userToken);

        // 如何更新最近时间
        userTokenMap.put(userToken.getToken(), userToken);
        return userToken;
    }


    /**
     * 根据userToken的信息，生成随机的salt，hash后得到token
     * @param userToken token对象
     * @return token值
     */
    public void resetToken(UserToken userToken) {
        String loginName = null;
        if (userToken.getAdmin() != null) {
            Admin admin = userToken.getAdmin();
            loginName = admin.getLoginName();

        } else if (userToken.getEmployee() != null) {
            Employee employee = userToken.getEmployee();
            loginName = employee.getLoginName();
        }
        if (StrUtil.isNotEmpty(loginName)) {
            String salt = CrudUtils.randomString(RANDOM_SALT_LENGTH);
            String token = DigestUtils.sha256Hex(loginName + salt + System.currentTimeMillis());
            userToken.setToken(token);
            userToken.setSalt(salt);
        }
    }

    public void setMenuService(MenuService menuService) {
        this.menuService = menuService;
    }


}
