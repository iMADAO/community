package com.madao.web.controller;

import com.madao.api.Exception.ResultException;
import com.madao.api.dto.UserDTO;
import com.madao.api.entity.User;
import com.madao.api.enums.ErrorEnum;
import com.madao.api.enums.ResultEnum;
import com.madao.api.form.PasswordChangeForm;
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

@Controller
public class UserController {
    @Autowired
    private UserService userService;

    private String phoneCode = "phoneCode";

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
        try {
            UserDTO userDTO = checkUserLogin(request);
            Long userId = userDTO.getUserId();
            ResultView resultView = userService.changeUserName(userId, userName);
            //更新session中的用户信息
            if (resultView.getCode().equals(ResultEnum.SUCCESS.getCode())) {
                User user = userService.getUserById(userId);
                if (user != null) {
                    BeanUtils.copyProperties(user, userDTO);
                    request.getSession().setAttribute("user", userDTO);
                }

            }
            return resultView;
        }catch (ResultException e){
            System.out.println(e.getMessage());
            return ResultUtil.returnFail();
        }catch (Exception e){
            e.printStackTrace();
            return ResultUtil.returnFail();
        }
    }

    @GetMapping("/index")
    public String toIndex(HttpServletRequest request){
        return "post";
    }

    @GetMapping("/toLogin")
    public String toLogin(@RequestParam(value = "lastPage", required = false) String lastPage, HttpServletRequest request){
        if(lastPage!=null && lastPage!=""){
            request.setAttribute("lastPage", lastPage);
        }
        return "login";
    }

    //登录
    @ResponseBody
    @PostMapping("/login")
    public ResultView login(@Validated  UserLoginForm form, BindingResult bindingResult, HttpServletRequest request, HttpServletResponse response){
        try {
            HttpSession session = request.getSession();

            ResultView<UserDTO> resultView = userService.login(form);
            UserDTO userDTO = (UserDTO) resultView.getData();
            session.setAttribute("user", userDTO);

            String token = KeyUtil.genUniquKey();
            session.setAttribute("token", token);
            Cookie cookie = new Cookie("token", token);
            response.addCookie(cookie);
            return resultView;
        }catch (Exception e){
            e.printStackTrace();
            return ResultUtil.returnFail();
        }
    }

    @GetMapping("/toRetrieve")
    public String toRetrieve(){
        return "retrieve";
    }

    @GetMapping("/toRegister")
    public String toRegister(){
        return "register";
    }

    @ResponseBody
    @PostMapping("/user/register/email")
    public ResultView registerByEmail(UserRegisterForm2 form, BindingResult bindingResult){
        try {
            ResultView resultView = userService.registerByEmail(form);
            System.out.println(resultView);
            return resultView;
        }catch (Exception e){
            e.printStackTrace();
            return ResultUtil.returnFail();
        }
    }

    @ResponseBody
    @PostMapping("/user/register/phone")
    public ResultView registerByPhone(UserRegisterForm form, BindingResult bindingResult){
        try {
            return userService.registerByPhone(form);
        }catch (Exception e){
            e.printStackTrace();
            return ResultUtil.returnFail();
        }
    }

    @ResponseBody
    @PostMapping("/validateCode")
    public ResultView validateCode(String account, HttpServletRequest request){
        if(account==null || account==""){
            return ResultUtil.returnFail("参数错误");
        }
        try {
            ResultView<String> resultView =  userService.sendValidateCode(account);
            if(resultView.getCode().equals(ResultEnum.SUCCESS.getCode())){
                String code = resultView.getData();
                request.getSession().setAttribute(phoneCode, code);
                return ResultUtil.returnSuccess();
            }else{
                return resultView;
            }
        }catch (Exception e){
            e.printStackTrace();
            return ResultUtil.returnFail();
        }
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
            UserDTO user = (UserDTO) request.getSession().getAttribute("user");
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
        }catch (ResultException e){
            System.out.println(e.getMessage());
            return ResultUtil.returnFail();
        }catch (Exception e){
            e.printStackTrace();
            return ResultUtil.returnFail();
        }
    }

    @RequestMapping("/toAdmin")
    public String toAdmin(){
        return "admin";
    }

    @ResponseBody
    @RequestMapping("/user/change/password")
    public ResultView changePassword(PasswordChangeForm form, HttpServletRequest request){
        try {
            UserDTO user = checkUserLogin(request);
            ResultView resultView = userService.changeUserPassword(user.getUserId(), form.getPassword(), form.getNewPassword());
            return resultView;
        }catch (ResultException e){
            System.out.println(e.getMessage());
            return ResultUtil.returnFail();
        }catch (Exception e){
            e.printStackTrace();
            return ResultUtil.returnFail();
        }
    }

    @ResponseBody
    @RequestMapping("/user/change/password/byCode")
    public ResultView changePasswordByCode(@RequestParam("phoneNum") String phoneNum, @RequestParam("newPassword") String newPassword, @RequestParam("code") String code, HttpServletRequest request){
        String changePasswdCode = (String) request.getSession().getAttribute(phoneCode);
        if(!code.equals(changePasswdCode)){
            return ResultUtil.returnFail("验证码不正确");
        }
        try {
            UserDTO user = checkUserLogin(request);
            ResultView resultView = userService.changeUserPassword(user.getUserId(), newPassword);
            return resultView;
        }catch (ResultException e){
            System.out.println(e.getMessage());
            return ResultUtil.returnFail();
        }catch (Exception e){
            e.printStackTrace();
            return ResultUtil.returnFail();
        }
    }

    //检查用户是否登录
    private UserDTO checkUserLogin(HttpServletRequest request){
        UserDTO user = (UserDTO) request.getSession().getAttribute("user");
        if(user==null){
            throw new ResultException("用户未登录");
        }
        return user;
    }

    //检查管理员权限
    private void checkAdminAuthority(UserDTO user){
        // todo 检查管理员权限
    }
}