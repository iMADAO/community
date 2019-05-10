package com.madao.api.service;

import com.madao.api.entity.User;
import com.madao.api.form.UserLoginForm;
import com.madao.api.form.UserRegisterForm;
import com.madao.api.form.UserRegisterForm2;
import com.madao.api.utils.ResultView;
import com.sun.org.apache.xpath.internal.operations.Bool;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;

@FeignClient(value = "user", fallbackFactory = UserServiceFallbackFactory.class)
public interface UserService {
    @GetMapping(value = "/user/{id}")
    public User getUserById(@PathVariable("id") Long userId);

    @GetMapping(value="/user/name/{userName}")
    public Boolean checkIfUserNameExist(@PathVariable(value = "userName") String userName);

    @PostMapping(value="/user/login")
    public ResultView login(UserLoginForm form);

    @PostMapping(value = "/test/aa")
    public User testUser(String userName);

    @PostMapping(value="/validateCode")
    public ResultView sendValidateCode(String account);

    @PostMapping("/user/register/email")
    public ResultView registerByEmail(UserRegisterForm2 form);

    @PostMapping("/user/register/phone")
    public ResultView registerByPhone(UserRegisterForm form);

    @GetMapping(value = "/user/validate/{userId}/{path}")
    public Boolean validate(@PathVariable("userId") Long userId, @PathVariable("path") String path);

    @RequestMapping("/user/logout")
    public ResultView logout();
}
