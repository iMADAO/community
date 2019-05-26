package com.madao.api.enums;

import lombok.Getter;

@Getter
public enum StateEnum {
    VISIBLE((byte)0, "可见"),
    INVISIBLE((byte)1, "不可见"),
    BAN((byte)-1, "禁止")
    ;
    private Byte code;
    private String message;

    StateEnum(Byte code, String message){
        this.code = code;
        this.message = message;
    }
}
