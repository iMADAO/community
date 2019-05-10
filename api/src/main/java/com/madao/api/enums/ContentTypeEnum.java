package com.madao.api.enums;

import lombok.Getter;

@Getter
public enum ContentTypeEnum {
    TEXT((byte)0, "文本"),
    PICTURE((byte)1, "图片");
    private Byte code;
    private String message;

    ContentTypeEnum(Byte code, String message){
        this.code = code;
        this.message = message;
    }
}
