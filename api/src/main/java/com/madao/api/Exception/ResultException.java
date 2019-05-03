package com.madao.api.Exception;


import com.madao.api.enums.ErrorEnum;
import com.madao.api.enums.ResultEnum;
import lombok.Data;

@Data
public class ResultException extends RuntimeException{
    private int code;
    private Object id;

    public ResultException(int code, String mess){
        super(mess);
        this.code = code;
    }

    public ResultException(int code, String mess, Object id){
        super(mess);
        this.code = code;
        this.id = id;
    }

    public ResultException(ErrorEnum e, Object id){
        super(e.getMessage());
        this.code = e.getCode();
        this.id = id;
    }

    public ResultException(ErrorEnum e){
        super(e.getMessage());
        this.code = e.getCode();
    }

    public ResultException(String message){
        super(message);
        this.code = ResultEnum.FAIL.getCode();
    }
}
