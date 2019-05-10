package com.madao.api.enums;

import lombok.Getter;

@Getter
public enum ErrorEnum {
    PARAM_ERROR(3, "参数错误"),
    PASSWORDERROR(1, "用户名或密码错误"),
    LOGINFALL(2, "登录失败"), CODEERROE(3, "验证码错误"),
    USER_NOT_LOGIN(5, "用户未登录");
    private int code;
    private String message;

    ErrorEnum(int code, String message){
        this.code = code;
        this.message = message;
    }

}
