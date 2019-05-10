package com.madao.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ArticleController {
    @GetMapping("/toArticle")
    public String toArticle(){
        return "article";
    }
}
