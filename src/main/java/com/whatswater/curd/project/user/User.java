package com.whatswater.curd.project.user;

import com.whatswater.sql.table.annotation.TableId;
import com.whatswater.sql.table.annotation.TableName;

@TableName("user")
public class User {
    @TableId
    private Long id;
    private String name;

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
}
