package com.madao.api.enums;

import lombok.Getter;

@Getter
public enum CommentEnum {
    VISIBLE((byte)0, "可见"),
    INVISIBLE((byte)1, "不可见");
    byte code;
    String message;
    CommentEnum(byte code, String message){
        this.code = code;
        this.message = message;
    }
}
