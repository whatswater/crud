package com.whatswater.sql.dialect;

import com.whatswater.sql.table.annotation.IdType;
import com.whatswater.sql.table.annotation.TableId;
import com.whatswater.sql.table.annotation.TableName;
import io.vertx.ext.sql.assist.TableColumn;

@TableName("user")
public class User {
    @TableId(type = IdType.AUTO)
    private Long id;

    @TableColumn("name")
    private String name;

    @TableColumn("code")
    private String code;

    @TableColumn("phone")
    private String phone;

    @TableColumn("gender")
    private Integer gender;

    @TableColumn("email")
    private String email;

    @TableColumn("status")
    private Integer status;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
