package com.madao.api.utils;
import lombok.Data;

@Data
public class ResultView {
    private Byte code;
    private String hint;
    private Object data;
}
