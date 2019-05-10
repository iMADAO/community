package com.madao.web.controller;

import com.madao.api.Exception.ResultException;
import com.madao.api.entity.User;
import com.madao.api.enums.ErrorEnum;
import com.madao.api.enums.ResultEnum;
import com.madao.api.form.UserLoginForm;
import com.madao.api.form.UserRegisterForm;
import com.madao.api.form.UserRegisterForm2;
import com.madao.api.service.UserService;
import com.madao.api.utils.FormErrorUtil;
import com.madao.api.utils.ResultUtil;
import com.madao.api.utils.ResultView;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.rmi.registry.Registry;

@Controller
public class UserController {
    @Autowired
    private UserService userService;



    @GetMapping("/checkUserName/{userName}")
    @ResponseBody
    public boolean testUserName(@PathVariable(value="userName") String userName){
        return userService.checkIfUserNameExist(userName);
    }

    @GetMapping("/index")
    public String toIndex(HttpServletRequest request){
        System.out.println(request.getSession().getAttribute("user"));
        return "index";
    }

    @ResponseBody
    @GetMapping("/getSession")
    public void testSession(HttpServletRequest request){
        System.out.println(request.getSession().getAttribute("user"));
    }

    @GetMapping("/toLogin")
    public String toLogin(){
        return "login";
    }

    @ResponseBody
    @PostMapping("/login")
    public ResultView login(@Validated  UserLoginForm form, BindingResult bindingResult, HttpSession session){
        System.out.println(form);
        System.out.println("user...login.......................");
        //出现异常
        if(bindingResult.hasErrors()){
            throw new ResultException(ErrorEnum.PARAM_ERROR, FormErrorUtil.getFormErrors(bindingResult));
        }
        ResultView resultView  = userService.login(form);
        System.out.println("resultView:////" +resultView);
        if(resultView.getCode().equals(ResultEnum.SUCCESS.getCode())) {
//            User user = (User) resultView.getData();
//            BeanUtils.copyProperties(resultView.getData(), user);
//            System.out.println(user + ".....");
            session.setAttribute("user", resultView.getData());
        }
        return resultView;
    }

    @GetMapping("/toRetrieve")
    public String toRetrieve(){
        return "retrieve";
    }

    @GetMapping("/toRegister")
    public String toRegister(){
        return "register";
    }

    @GetMapping("/toTest1")
    public String toTest(){
        return "test1";
    }

    @ResponseBody
    @PostMapping("/user/register/email")
    public ResultView registerByEmail(UserRegisterForm2 form, BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            throw new ResultException(ErrorEnum.PARAM_ERROR, FormErrorUtil.getFormErrors(bindingResult));
        }
        System.out.println("email............................");
        ResultView resultView =  userService.registerByEmail(form);
        System.out.println(resultView);
        return resultView;
    }

    @ResponseBody
    @PostMapping("/user/register/phone")
    public ResultView registerByPhone(UserRegisterForm form, BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            throw new ResultException(ErrorEnum.PARAM_ERROR, FormErrorUtil.getFormErrors(bindingResult));
        }
        return userService.registerByPhone(form);
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

    @ResponseBody
    @PostMapping("/test/getUser")
    public User test(String userName){
        return userService.testUser(userName);
    }

    @ResponseBody
    @PostMapping("/validateCode")
    public ResultView validateCode(String account){
        System.out.println(account);
        if(account==null || account==""){
            throw new ResultException(ErrorEnum.PARAM_ERROR);
        }
        return userService.sendValidateCode(account);
    }

    @GetMapping(value = "/user/validate/{userId}/{path}")
    public void validate(@PathVariable("userId") Long userId, @PathVariable("path") String path, HttpServletResponse response){
        boolean result = userService.validate(userId, path);
        System.out.println(result);
        //todo 在前端通知结果
    }

    @GetMapping(value="/toHomePage")
    public String toHomePage(){
        return "homePage";
    }

    @ResponseBody
    @GetMapping(value = "/user/logout")
    public ResultView logout(HttpServletRequest request){
        request.getSession().removeAttribute("user");
        return ResultUtil.returnSuccess();
    }

//    @ResponseBody
//    @GetMapping(value="/user/logout2")
//    public ResultView logout2(HttpServletRequest request){
//        request.getSession().removeAttribute("user");
//        return ResultUtil.returnSuccess();
//    }

//    @GetMapping("/test")
//    @ResponseBody
//    public User getUser(){
//        System.out.println("调用.............");
//        return userService.getUserById(1L);
//    }

}
