package com.ch.demo.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;

@TableName("user")
public class EntityClass {
    Integer id ;

    @TableField("username")
    String userName;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public EntityClass() {
    }

    public EntityClass(Integer id, String userName) {
        this.id = id;
        this.userName = userName;
    }
}