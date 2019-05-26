package com.madao.web.controller;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageInfo;
import com.madao.api.dto.ArticleDTO;
import com.madao.api.entity.User;
import com.madao.api.form.ArticleAddForm;
import com.madao.api.service.ArticleService;
import com.madao.api.utils.ResultUtil;
import com.madao.api.utils.ResultView;
import com.madao.web.util.FileConverterUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.xml.transform.Result;

@Controller
public class ArticleController {
    @Autowired
    private ArticleService articleService;

    @GetMapping("/toArticle")
    public String toArticle(@RequestParam(value = "pageNum",  defaultValue = "1") Integer pageNum, @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize, HttpServletRequest request){
        ResultView resultView = articleService.getArticleList(pageNum, pageSize);
        System.out.println("resultView======" + resultView);
        request.setAttribute("data", resultView);
        return "article";
    }

    @GetMapping("/toArticle/{categoryId}")
    public String toArticle(HttpServletRequest request, @PathVariable("categoryId") Long categoryId, @RequestParam(value = "pageNum",  defaultValue = "1") Integer pageNum, @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize){
        ResultView resultView = articleService.getArticleList(pageNum, pageSize, categoryId);
        request.setAttribute("data", resultView);
        request.setAttribute("categoryId", categoryId);
        return "article";
    }

    @ResponseBody
    @GetMapping("/toArticle2/{categoryId}")
    public ResultView toArticle2(HttpServletRequest request, @PathVariable("categoryId") Long categoryId, @RequestParam(value = "pageNum",  defaultValue = "1") Integer pageNum, @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize){
        ResultView resultView = articleService.getArticleList(pageNum, pageSize, categoryId);
        return resultView;
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
    @ResponseBody
    @PostMapping("/article")
    public ResultView addArticle(ArticleAddForm form, HttpServletRequest request){
        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            return ResultUtil.returnFail("用户未登录,请登录后重试");
        }
        Long userId = user.getUserId();
        String filePath = form.getFilePath();
        form.setUserId(userId);
        try {
            //将文档转换成Html页面，保存访问地址
            System.out.println("filePath ...." + filePath);
            String accessUrl = FileConverterUtil.convert2Html(filePath);
            form.setAccessUrl(accessUrl);
            ResultView resultView = articleService.addArticle(form);
            return resultView;
        }catch (Exception e){
            e.printStackTrace();
            return ResultUtil.returnFail("操作失败，请稍后再试");
        }
    }

    @ResponseBody
    @RequestMapping("/article/person/getList/{pageNum}/{pageSize}")
    public ResultView getArticleByePerson(@PathVariable("pageNum") Integer pageNum, @PathVariable("pageSize") Integer pageSize, HttpServletRequest request){
        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            return ResultUtil.returnFail("用户未登录,请登录后重试");
        }
        try {
           ResultView resultView = articleService.getArticleListByPerson(user.getUserId(), pageNum, pageSize);
           return resultView;
        }catch (Exception e){
            e.printStackTrace();
            return ResultUtil.returnFail();
        }
    }

    @ResponseBody
    @RequestMapping("/article/person/visible/{articleId}/{operate}")
    public ResultView operateArticleByPerson(@PathVariable("articleId") Long articleId, @PathVariable("operate") Byte operate, HttpServletRequest request){
        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            return ResultUtil.returnFail("用户未登录,请登录后重试");
        }
        try {
            ResultView resultView = articleService.operateArticleByPerson(user.getUserId(), articleId, operate);
            return resultView;
        }catch (Exception e){
            e.printStackTrace();
            return ResultUtil.returnFail();
        }
    }
}
