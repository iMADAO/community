package com.madao.api.enums;

import lombok.Getter;

@Getter
public enum CollectTypeEnum {
    POST((byte)0, "贴吧"),
    ANSWER((byte)1, "问答"),
    QUESTION((byte)2, "问题"),
    ARTICLE((byte)3, "文章")
    ;
    byte code;
    String message;
    CollectTypeEnum(byte code, String message){
        this.code = code;
        this.message = message;
    }
}
