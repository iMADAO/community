package com.madao.common.utils;


import com.madao.common.enums.ResultEnum;

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
}
