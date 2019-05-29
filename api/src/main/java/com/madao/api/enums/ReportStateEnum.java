package com.madao.api.enums;

import lombok.Getter;

@Getter
public enum ReportStateEnum {
    REPORTED((byte)0, "已举报"),
    FINISH((byte)1, "已通过"),
    DENY((byte)-1, "已拒绝")
    ;
    private Byte code;
    private String message;

    ReportStateEnum(Byte code, String message){
        this.code = code;
        this.message = message;
    }
}
