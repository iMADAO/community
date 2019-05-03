package com.madao.demo.controller;

import com.madao.api.entity.User;
import com.madao.api.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @Autowired
    private UserService userService;

    @GetMapping("/getUser")
    public User getUser(){
        return userService.getUserById(1L);
    }
}
