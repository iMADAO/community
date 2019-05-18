package com.madao.user.controller;

import com.madao.api.Exception.ResultException;
import com.madao.api.entity.User;
import com.madao.api.enums.ResultEnum;
import com.madao.api.form.UserLoginForm;
import com.madao.api.form.UserRegisterForm;
import com.madao.api.form.UserRegisterForm2;
import com.madao.api.utils.KeyUtil;
import com.madao.api.utils.ResultUtil;
import com.madao.api.utils.ResultView;
import com.madao.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.transform.Result;
import java.util.List;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

//    用户登录
    @ResponseBody
    @PostMapping("/user/login")
    public ResultView<User> login(@RequestBody  UserLoginForm form, HttpSession session, HttpServletResponse response){
        User user = null;
        try {
            user = userService.loginValidate(form);
            ResultView<User> resultView = new ResultView<>();
            resultView.setCode(ResultEnum.SUCCESS.getCode());
            resultView.setData(user);
            return resultView;
//            return ResultUtil.returnSuccess(user);
        }catch (ResultException e){
            System.out.println("catch");
            ResultView resultView = ResultUtil.returnException(e);
            return  resultView;
        }

    }

    @ResponseBody
    @RequestMapping("/user/logout")
    public ResultView logout(HttpSession session){
        session.removeAttribute("user");
        return ResultUtil.returnSuccess();
    }

//    用户注册-手机号码
    @ResponseBody
    @PostMapping("/user/register/phone")
    public ResultView registerByPhone(@RequestBody UserRegisterForm form){
        User user =  null;
        try {
            user = userService.registerByPhone(form);
        }catch (ResultException e){
            e.printStackTrace();
            System.out.println(e.getMessage());
            return ResultUtil.returnException(e);
        }
        return ResultUtil.returnSuccess(user);
    }

    //用户注册-邮箱
    @Async
    @ResponseBody
    @PostMapping("/user/register/email")
    public ResultView registerByEmail(@RequestBody UserRegisterForm2 form){
        User user = null;
        try {
            user = userService.registerByEmail(form);
            userService.sendEmail(user.getUserId(), form.getEmail());
        }catch (ResultException e){
            e.printStackTrace();
            System.out.println(e.getMessage());
            return ResultUtil.returnException(e);
        }
        System.out.println("success");
        ResultView resultView = ResultUtil.returnSuccess(user);
        System.out.println(resultView);
        return resultView;
    }

    @ResponseBody
    @RequestMapping(value="/user/name/check")
    public ResultView checkIfUserNameExist(@RequestParam(value = "userName") String userName){
        try{
            Boolean result = userService.checkUserName(userName);
            return ResultUtil.returnSuccess(result);
        }catch (Exception e){
            e.printStackTrace();
            return ResultUtil.returnFail();
        }
    }

    @ResponseBody
    @RequestMapping("/user/name/change")
    public ResultView changeUserName(@RequestParam("usreId") Long userId, @RequestParam("userName") String userName){
        try {
            userService.setNewUserName(userId, userName);
            return ResultUtil.returnSuccess();
        }catch (ResultException e){
            e.printStackTrace();
            return ResultUtil.returnFail(e.getMessage());
        }catch (Exception e){
            e.printStackTrace();
            return ResultUtil.returnFail();
        }
    }

    @ResponseBody
    @RequestMapping("/user/pic/change")
    ResultView changeUserPic(@RequestParam("userId") Long userId, @RequestParam("picPath") String picPath){
        try {
            userService.setNewUserPic(userId, picPath);
            return ResultUtil.returnSuccess();
        }catch (ResultException e){
            e.printStackTrace();
            return ResultUtil.returnFail(e.getMessage());
        }catch (Exception e){
            e.printStackTrace();
            return ResultUtil.returnFail();
        }
    }

    //邮箱验证的链接,用户点击链接完成验证
    @ResponseBody
    @GetMapping(value = "/user/validate/{userId}/{path}")
    public Boolean validate(@PathVariable("userId") Long userId, @PathVariable("path") String path){
        System.out.println("validate......................");
        try {
            boolean result = userService.userRegisterValidate(userId, path);
            System.out.println("result: " +result);
            return result;
        }catch (ResultException e){
            System.out.println(e.getMessage());
            return false;
        }

    }

    //发送验证码到手机
    @ResponseBody
    @PostMapping(value="/validateCode")
    public ResultView sendValidateCode(@RequestBody String account){
        try{
            userService.sendPhoneCode(account);
        }catch (ResultException e){
            System.out.println(e.getMessage());
            return ResultUtil.returnException(e);
        }

        return ResultUtil.returnSuccess();
    }
}
