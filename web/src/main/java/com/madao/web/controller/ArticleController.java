package com.madao.web.controller;

import com.madao.api.entity.User;
import com.madao.api.form.ArticleAddForm;
import com.madao.api.service.ArticleService;
import com.madao.api.utils.ResultUtil;
import com.madao.api.utils.ResultView;
import com.madao.web.util.FileConverterUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

@Controller
public class ArticleController {
    @Autowired
    private ArticleService articleService;

    @GetMapping("/toArticle")
    public String toArticle(HttpServletRequest request){
        ResultView resultView = articleService.getAllCategoryInArticle();
        request.setAttribute("data", resultView);
        return "article";
    }

    @GetMapping("/toArticle")
    public String toArticle(HttpServletRequest request, @RequestParam("categoryId") Long categoryId){
        ResultView resultView = articleService.getAllCategoryInArticle();
        request.setAttribute("data", resultView);
        return "article";
    }

    @RequestMapping("/article/category/all")
    public ResultView getAllArticleCategory() {
        try {
            ResultView resultView = articleService.getAllCategoryInArticle();
            return resultView;
        }catch (Exception e){
            e.printStackTrace();
            return ResultUtil.returnFail();
        }
    }

    //添加文件
    @PostMapping("/article")
    public ResultView addArticle(ArticleAddForm form, HttpServletRequest request){
        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            return ResultUtil.returnFail("用户未登录,请登录后重试");
        }
        Long userId = user.getUserId();
        String filePath = form.getFilePath();
        try {
            //将文档转换成Html页面，保存访问地址
            String accessUrl = FileConverterUtil.convert2Html(filePath);
            form.setAccessUrl(accessUrl);
            ResultView resultView = articleService.addArticle(form);
            return resultView;
        }catch (Exception e){
            e.printStackTrace();
            return ResultUtil.returnFail("操作失败，请稍后再试");
        }
    }
}
