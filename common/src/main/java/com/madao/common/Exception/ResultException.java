package com.madao.common.Exception;

import com.madao.common.enums.ErrorEnum;
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
}
