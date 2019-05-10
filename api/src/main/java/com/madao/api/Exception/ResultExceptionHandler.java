package com.madao.api.Exception;




import com.fasterxml.jackson.databind.ObjectMapper;
import com.madao.api.enums.ResultEnum;
import com.madao.api.utils.ResultView;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Date: 2018/1/8
 * Author: Richard
 */

//@Component
public class ResultExceptionHandler implements HandlerExceptionResolver {

    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object object, Exception exception) {
        System.out.println("经过异常处理！");
        //如果是自定义异常，否则打印系统异常
        ResultView resultView = new ResultView();
        if (exception instanceof ResultException){
            ResultException resultException = (ResultException) exception;
            resultView.setCode(ResultEnum.FAIL.getCode());
            resultView.setHint(ResultEnum.FAIL.getMessage()+resultException.getMessage());
            resultView.setData(resultException.getId());
            System.out.println(resultException.getMessage());
        }else{
            resultView.setCode(ResultEnum.ERROR.getCode());
            resultView.setHint(ResultEnum.ERROR.getMessage());
            exception.printStackTrace();
        }
        try {
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json; charset=UTF-8");
            PrintWriter printWriter = response.getWriter();
            printWriter.print(new ObjectMapper().writeValueAsString(resultView));
            printWriter.flush();
            printWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
