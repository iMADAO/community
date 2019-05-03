package com.madao.api.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class User implements Serializable{
    private Long userId;

    private String userName;

    private Long roleId;

    private String password;

    private String userPic;

    private String email;

    private String phone;

    private Byte isActive;
}