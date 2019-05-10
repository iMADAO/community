package com.madao.api.utils;


import com.madao.api.Exception.ResultException;
import com.madao.api.enums.ErrorEnum;
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

    public static ResultView returnFall(ErrorEnum errorEnum){
        ResultView resultView = new ResultView();
        resultView.setCode(ResultEnum.FAIL.getCode());
        resultView.setHint(errorEnum.getMessage());
        resultView.setData(errorEnum.getCode());
        return resultView;
    }

    public static ResultView returnFall(ErrorEnum errorEnum, String s) {
        ResultView resultView = new ResultView();
        resultView.setCode(ResultEnum.FAIL.getCode());
        resultView.setHint(errorEnum.getMessage());
        resultView.setData(s);
        return resultView;
    }

    public static ResultView returnFail() {
        ResultView resultView = new ResultView();
        resultView.setHint("请稍后重试");
        resultView.setCode(ResultEnum.ERROR.getCode());
        return resultView;
    }
}
