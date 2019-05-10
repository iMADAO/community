package com.madao.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PostController {
    @GetMapping("/toIndex")
    public String toIndex(){
        return "index";
    }
}
