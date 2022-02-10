package com.whatswater.curd.project.sys.permission;


import com.whatswater.curd.project.sys.admin.Admin;
import com.whatswater.curd.project.sys.employee.Employee;

public class UserToken {
    private Admin admin;
    private Employee employee;
    private String token;
    private long updateTime;
    private long ttl;
    private String salt;

    public boolean isExpired(long current) {
        if (ttl < 0) {
            return false;
        }
        return current - updateTime > ttl;
    }

    public boolean isExpired() {
        if (ttl < 0) {
            return false;
        }
        return isExpired(System.currentTimeMillis());
    }

    public boolean isAdmin() {
        return admin != null;
    }

    public static UserToken newEmployeeToken(Employee employee, String token, long ttl) {
        UserToken userToken = new UserToken();
        userToken.employee = employee;
        userToken.token = token;
        userToken.updateTime = System.currentTimeMillis();
        userToken.ttl = ttl;

        return userToken;
    }

    public static UserToken newAdminToken(Admin admin, Employee employee, String token, long ttl) {
        UserToken userToken = new UserToken();
        userToken.employee = employee;
        userToken.admin = admin;
        userToken.token = token;
        userToken.updateTime = System.currentTimeMillis();
        userToken.ttl = ttl;

        return userToken;
    }


    public Admin getAdmin() {
        return admin;
    }

    public void setAdmin(Admin admin) {
        this.admin = admin;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }

    public long getTtl() {
        return ttl;
    }

    public void setTtl(long ttl) {
        this.ttl = ttl;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }
}
