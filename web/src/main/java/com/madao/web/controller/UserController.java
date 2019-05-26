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
import com.madao.api.utils.KeyUtil;
import com.madao.api.utils.ResultUtil;
import com.madao.api.utils.ResultView;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.rmi.registry.Registry;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

@Controller
public class UserController {
    @Autowired
    private UserService userService;



    @GetMapping("/checkUserName/{userName}")
    @ResponseBody
    public ResultView testUserName(@PathVariable(value="userName") String userName){
        System.out.println(userName);
        try {
            ResultView resultView = userService.checkIfUserNameExist(userName);
            return resultView;
        }catch (Exception e){
            e.printStackTrace();
            return ResultUtil.returnFail();
        }

    }

    @PostMapping("/user/userName/change")
    @ResponseBody
    public ResultView changeUserName(@RequestParam("userName") String userName, HttpServletRequest request){
        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            return ResultUtil.returnFail("用户未登录,请登录后重试");
        }
        Long userId = user.getUserId();
        ResultView resultView =  userService.changeUserName(userId, userName);
        //更新session中的用户信息
        if(resultView.getCode().equals(ResultEnum.SUCCESS.getCode())){
            user = userService.getUserById(userId);
            if(user!=null){
                request.getSession().setAttribute("user", user);
            }

        }
        return resultView;
    }

    @GetMapping("/index")
    public String toIndex(HttpServletRequest request){
        System.out.println(request.getSession().getAttribute("user"));
        return "post";
    }

    @ResponseBody
    @GetMapping("/getSession")
    public void testSession(HttpServletRequest request){
        System.out.println(request.getSession().getAttribute("user"));
    }

    @GetMapping("/toLogin")
    public String toLogin(@RequestParam(value = "lastPage", required = false) String lastPage, HttpServletRequest request){
        if(lastPage!=null && lastPage!=""){
            request.setAttribute("lastPage", lastPage);
        }
        return "login";
    }

    @ResponseBody
    @PostMapping("/login")
    public ResultView login(@Validated  UserLoginForm form, BindingResult bindingResult, HttpServletRequest request, HttpServletResponse response){
        System.out.println("user...login.......................");
        HttpSession session = request.getSession();

        ResultView<User> resultView  = userService.login(form);
        User user = (User) resultView.getData();
        session.setAttribute("user", user);

        String token = KeyUtil.genUniquKey();
        session.setAttribute("token", token);
        Cookie cookie = new Cookie("token", token);
        response.addCookie(cookie);
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

    @ResponseBody
    @PostMapping("/user/pic/change")
    public ResultView changeUserPic(@RequestParam("picPath") String picPath, HttpServletRequest request){
        try {
            User user = (User) request.getSession().getAttribute("user");
            if (user == null) {
                return ResultUtil.returnFail("用户未登录,请登录后重试");
            }
            Long userId = user.getUserId();
            ResultView resultView = userService.changeUserPic(userId, picPath);
            if(resultView.getCode().equals(ResultEnum.SUCCESS.getCode())){
                User newUser = userService.getUserById(userId);
                if(newUser!=null){
                    request.getSession().setAttribute("user", newUser);
                }
            }
            return resultView;
        }catch (Exception e){
            e.printStackTrace();
            return ResultUtil.returnFail();
        }
    }

    @RequestMapping("/toAdmin")
    public String toAdmin(){
        return "admin";
    }

    @RequestMapping("toAdminLogin")
    public String toAdminLogin(){
        return "adminLogin";
    }

    @RequestMapping("/toTestAjax")
    public String testAjax(){
        return "testAjax";
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

    @RequestMapping("/testAjax")
    public void testAjax(@RequestParam("data") List<Integer> data){
        data.stream().forEach(System.out::println);
    }

}
