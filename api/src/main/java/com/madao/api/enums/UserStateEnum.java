package com.madao.api.enums;

import lombok.Getter;

@Getter
public enum UserStateEnum{
    INVALIDATE((byte)0, "账户未验证"),
    ACTIVE((byte)1, "账户可用"),
    INACTIVE((byte)2, "账户不可用")
    ;
    private Byte code;
    private String message;

    UserStateEnum(Byte code, String message){
        this.code = code;
        this.message = message;
    }
}