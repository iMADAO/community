package com.madao.user.controller;

import com.madao.user.bean.User;
import com.madao.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
public class TestController {
    @Autowired
    private UserService userService;

    @GetMapping("/user/{id}")
    public User getUserById(@PathVariable("id") Long id){
        return userService.getUserById(id);
    }

    @GetMapping("/test")
    public List<String> test(){
        List<String> list = new ArrayList<>();
        list.add("aa");
        list.add("bb");
        return list;
    }

    @PostMapping("/test/aa")
    public User test(@RequestBody String userName){
        User user = new User();
        user.setUserName(userName);
        return user;
    }
}
