package com.madao.web.controller;

import com.madao.api.entity.User;
import com.madao.api.utils.ResultUtil;
import com.madao.api.utils.ResultView;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class MyController {
    @GetMapping("/toTest1")
    public String toTest(){
        return "test1";
    }

    @ResponseBody
    @PostMapping("/test123")
    public ResultView test(String account, String password){
        System.out.println("####################################");
        System.out.println(account + "-------" + password);
        return ResultUtil.returnSuccess();
    }

    @ResponseBody
    @PostMapping("/test/post")
    public ResultView testPost(){

        return ResultUtil.returnSuccess();
    }

    @RequestMapping("/testAjax")
    public void testAjax(@RequestParam("data") List<Integer> data){
        data.stream().forEach(System.out::println);
    }

    @RequestMapping("/toTestAjax")
    public String testAjax(){
        return "testAjax";
    }

    //测试用
    @ResponseBody
    @RequestMapping("/testRealPath")
    public void test(HttpServletRequest request){
        String realPath = request.getServletContext().getRealPath("/");
        String path = request.getServletPath();
        System.out.println(realPath);
        System.out.println(path);
    }

    @RequestMapping("/toTest")
    public String toTest1(){
        return "test";
    }

}
