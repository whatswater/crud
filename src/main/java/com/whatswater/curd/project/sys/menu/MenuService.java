package com.whatswater.curd.project.sys.menu;


import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.whatswater.curd.project.common.CrudUtils;
import com.whatswater.curd.project.common.Page;
import com.whatswater.curd.project.common.PageResult;
import com.whatswater.curd.project.module.workflow.flowInstanceTaskRelation.FlowInstanceTaskRelation;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.sql.assist.SQLExecute;
import io.vertx.ext.sql.assist.SqlAssist;
import io.vertx.mysqlclient.MySQLClient;
import io.vertx.mysqlclient.MySQLPool;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


public class MenuService {
    private final MenuSQL menuSQL;

    public MenuService(MySQLPool pool) {
        this.menuSQL = new MenuSQL(SQLExecute.createMySQL(pool));
    }

    public Future<PageResult<Menu>> search(Page page, MenuQuery query) {
        SqlAssist sqlAssist = query.toSqlAssist();
        sqlAssist.setStartRow(page.getOffset());
        sqlAssist.setRowSize(page.getLimit());

        return menuSQL.getCount(sqlAssist).compose(total -> {
            if (CrudUtils.notZero(total)) {
                return menuSQL.selectAll(sqlAssist).map(list -> PageResult.of(list.stream().map(Menu::new).collect(Collectors.toList()), page, total));
            } else {
                return Future.succeededFuture(PageResult.empty());
            }
        });
    }

    /**
     * 查询子菜单
     * @param parentId 菜单Id
     * @return 子菜单列表
     */
    public Future<List<MenuName>> queryChildrenName(long parentId) {
        SqlAssist sqlAssist = Menu.parentIdSqlAssist(parentId);
        return menuSQL.selectAll(sqlAssist).map(jsonList -> {
            if (jsonList == null || jsonList.isEmpty()) {
                return Collections.emptyList();
            }

            return jsonList.stream().map(Menu::new).map(MenuName::fromMenu).collect(Collectors.toList());
        });
    }

    public Future<Menu> getById(Long id) {
        return menuSQL.selectById(id).map(json -> {
            if (json == null) {
                return null;
            }
            return new Menu(json);
        });
    }

    public Future<Menu> getByUrl(String url) {
        SqlAssist sqlAssist = Menu.urlSqlAssist(url);
        return menuSQL.selectAll(sqlAssist).map(MenuService::mapOne);
    }

    public Future<Boolean> existsChildren(long parentId) {
        SqlAssist sqlAssist = Menu.parentIdSqlAssist(parentId);
        return menuSQL.getCount(sqlAssist).map(value -> value != null && value > 0L);
    }

    private Future<Boolean> exists(long parentId, String name) {
        SqlAssist sqlAssist = Menu.parentIdNameSqlAssist(parentId, name);
        return menuSQL.getCount(sqlAssist).map(value -> value != null && value > 0L);
    }

    public Future<Long> fillAndInsertWithCheck(final Menu menu) {
        Long parentId = menu.getParentId();
        if (CrudUtils.gtZero(parentId)) {
            return exists(parentId, menu.getName()).compose(exist -> {
                if (exist) {
                    return Future.failedFuture("系统中存在与当前菜单名称完全一致的菜单");
                }

                return getById(parentId).compose(parentMenu -> {
                    menu.setPath(parentMenu.childPath());
                    menu.setLevel(parentMenu.childLevel());
                    menu.setParentId(parentMenu.getId());

                    return insert(menu);
                });
            });
        } else {
            return exists(0L, menu.getName()).compose(exist -> {
                if (exist) {
                    return Future.failedFuture("系统中存在与当前菜单名称完全一致的菜单");
                }

                menu.setPath(StrUtil.EMPTY);
                menu.setLevel(0);
                menu.setParentId(0L);
                return insert(menu);
            });
        }
    }

    public Future<Integer> deleteWithCheck(long id) {
        return getById(id).compose(org -> {
            if (org == null) {
                return Future.failedFuture("不存在此菜单");
            }
            return existsChildren(id).compose(r -> {
                if (r) {
                    return Future.failedFuture("当前部门存在子菜单，请先删除子菜单");
                }
                return delete(id);
            }).compose(r -> this.syncLeaf(org.getParentId()).map(r));
        });
    }

    private Future<Integer> syncLeaf(long menuId) {
        if (menuId == 0L) {
            return Future.succeededFuture(0);
        }
        return existsChildren(menuId).compose(r -> {
            Menu menu = new Menu();
            menu.setId(menuId);
            menu.setLeaf(!r);
            return update(menu);
        });
    }

    public Future<Integer> updateWithCheck(MenuName menuName) {
        Menu menuUpdate = new Menu();
        menuUpdate.setId(menuName.getId());
        menuUpdate.setName(menuName.getName());
        if (StrUtil.isNotEmpty(menuName.getUrl())) {
            menuUpdate.setUrl(menuName.getUrl());
        }
        return this.update(menuUpdate);
    }


    public Future<Long> insert(Menu menu) {
        return menuSQL.insertNonEmptyGeneratedKeys(menu, MySQLClient.LAST_INSERTED_ID);
    }

    public Future<Integer> update(Menu menu) {
        return menuSQL.updateNonEmptyById(menu);
    }

    public Future<Integer> delete(long id) {
        return menuSQL.deleteById(id);
    }


    public Future<List<Menu>> queryAllMenuList() {
        SqlAssist sqlAssist = Menu.menuSqlAssist(MenuTypeEnum.MENU, MenuTypeEnum.DIRECTORY);
        return menuSQL.selectAll(sqlAssist).map(list -> list.stream().map(Menu::new).collect(Collectors.toList()));
    }

    private static Menu mapOne(List<JsonObject> jsonList) {
        if (CollectionUtil.isEmpty(jsonList)) {
            return null;
        }

        return new Menu(jsonList.get(0));
    }
}
