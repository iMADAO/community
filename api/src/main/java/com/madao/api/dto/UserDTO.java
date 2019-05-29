package com.madao.api.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class UserDTO implements Serializable{
    @JsonSerialize(using = ToStringSerializer.class)
    private Long userId;

    private String userName;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long roleId;

    private String password;

    private String userPic;

    private String email;

    private String phone;

    private Byte state;

    List<String> authorityList;
}
