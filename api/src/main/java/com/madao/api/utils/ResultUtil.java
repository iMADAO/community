package com.madao.api.utils;


import com.madao.api.Exception.ResultException;
import com.madao.api.enums.ResultEnum;

/**
 * Date: 2018/1/9
 * Author: Richard
 */

public class ResultUtil {
    public static ResultView returnSuccess(){
        ResultView resultView = new ResultView();
        resultView.setCode(ResultEnum.SUCCESS.getCode());
        resultView.setHint(ResultEnum.SUCCESS.getMessage());
        return resultView;
    }

    public static ResultView returnSuccess(Object data){
        ResultView resultView = new ResultView();
        resultView.setCode(ResultEnum.SUCCESS.getCode());
        resultView.setHint(ResultEnum.SUCCESS.getMessage());
        resultView.setData(data);
        return resultView;
    }

    public static ResultView returnException(ResultException e){
        ResultView resultView = new ResultView();
        resultView.setCode((byte)e.getCode());
        resultView.setHint(e.getMessage());
        return resultView;
    }

    public static ResultView returnFail(String hint) {
        ResultView resultView = new ResultView();
        resultView.setCode(ResultEnum.FAIL.getCode());
        resultView.setHint(hint);
        return resultView;
    }
}
