package com.madao.api.enums;

import lombok.Getter;

@Getter
public enum  OperateEnum {
    CANCEL((byte)0, "取消"),
    OPERATE((byte)1, "添加操作")
    ;
    byte code;
    String message;
    OperateEnum(byte code, String message){
        this.code = code;
        this.message = message;
    }
}
