package com.madao.article.controller;

import com.madao.api.dto.ArticleDTO;
import com.madao.api.entity.Article;
import com.madao.api.entity.ArticleCategory;
import com.madao.api.enums.ResultEnum;
import com.madao.api.form.ArticleAddForm;
import com.madao.api.utils.ResultUtil;
import com.madao.api.utils.ResultView;
import com.madao.article.service.ArticleCategoryService;
import com.madao.article.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ArticleController {
    @Autowired
    private ArticleCategoryService articleCategoryService;

    @Autowired
    private ArticleService articleService;

    @RequestMapping("/article/category/all")
    public ResultView getAllCategoryInArticle(){
        try {
            List<ArticleCategory> categoryList = articleCategoryService.getAllCategory();
            return ResultUtil.returnSuccess(categoryList);
        }catch (Exception e){
            e.printStackTrace();
            return ResultUtil.returnFail();
        }
    }

    @RequestMapping("/article/getList")
    public ResultView getArticleList(@RequestParam("pageNum")Integer pageNum, @RequestParam("pageSize") Integer pageSize){
        try {
            ArticleDTO articleDTO = articleService.getArticleDTO(pageNum, pageSize);
            return ResultUtil.returnSuccess(articleDTO);
        }catch (Exception e){
            e.printStackTrace();
            return ResultUtil.returnFail();
        }
    }

    @RequestMapping("/article/getList/byCategory")
    public ResultView getArticleList(@RequestParam("pageNum")Integer pageNum, @RequestParam("pageSize") Integer pageSize, @RequestParam("categoryId") Long categoryId){
        try {
            ArticleDTO articleDTO = articleService.getArticleDTOByCategoryId(pageNum, pageSize, categoryId);
            return ResultUtil.returnSuccess(articleDTO);
        }catch (Exception e){
            e.printStackTrace();
            return ResultUtil.returnFail();
        }
    }

    //根据Id获取文章
    @RequestMapping("/article/get")
    public ResultView getArticleById(@RequestParam("articleId") Long articleId){
        try {
            Article article = articleService.getArticleById(articleId);
            ResultView<Article> resultView = new ResultView<>();
            resultView.setCode(ResultEnum.SUCCESS.getCode());
            resultView.setData(article);
            return resultView;
        }catch (Exception e){
            e.printStackTrace();
            return ResultUtil.returnFail();
        }
    }

    //更新下载次数
    @RequestMapping("/article/download/increase")
    public void increaseArticleDownloadCount(Long articleId){
        articleService.increaseDownloadCount(articleId);
    }

    //添加
    @RequestMapping("/article/add")
    public ResultView addArticle(@RequestBody ArticleAddForm form){
        try{
            Article article = articleService.addArticle(form);
            return ResultUtil.returnSuccess(article);
        }catch (Exception e){
            e.printStackTrace();
            return ResultUtil.returnFail();
        }
    }
}
