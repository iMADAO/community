package com.madao.common.enums;

import lombok.Getter;

@Getter
public enum ErrorEnum {
    PARAM_ERROR(3, "参数错误")
    ;
    private int code;
    private String message;

    ErrorEnum(int code, String message){
        this.code = code;
        this.message = message;
    }

}
