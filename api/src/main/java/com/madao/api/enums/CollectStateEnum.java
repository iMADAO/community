package com.madao.api.enums;

import lombok.Getter;

@Getter
public enum CollectStateEnum {
    UN_COLLECTED((byte)0, "未收藏"),
    COLLECTED((byte)1, "已收藏")
    ;
    byte code;
    String message;
    CollectStateEnum(byte code, String message){
        this.code = code;
        this.message = message;
    }
}
