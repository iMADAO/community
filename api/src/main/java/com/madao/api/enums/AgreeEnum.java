package com.madao.api.enums;

import lombok.Getter;

@Getter
public enum AgreeEnum {
    DEFAULT((byte)-1, "未定义"),
    AGREE((byte)0, "赞同"),
    DISAGREE((byte)1,"不赞同")
    ;
    byte code;
    String message;
    AgreeEnum(byte code, String message){
        this.code = code;
        this.message = message;
    }
}
