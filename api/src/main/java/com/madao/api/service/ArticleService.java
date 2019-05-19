package com.madao.api.service;

import com.madao.api.entity.Article;
import com.madao.api.form.ArticleAddForm;
import com.madao.api.utils.ResultView;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "article", fallbackFactory = ArticleServiceFallbackFactory.class)
public interface ArticleService {
    @RequestMapping("/article/category/all")
    public ResultView getAllCategoryInArticle();

    @RequestMapping("/article/get")
    public ResultView getArticleById(@RequestParam("articleId") Long articleId);

    @RequestMapping("/article/download/increase")
    void increaseArticleDownloadCount(Long articleId);

    @RequestMapping("/article/add")
    public ResultView addArticle(ArticleAddForm form);

    @RequestMapping("/article/getList")
    public ResultView getArticleList(@RequestParam("pageNum")Integer pageNum, @RequestParam("pageSize") Integer pageSize);

    @RequestMapping("/article/getList/byCategory")
    public ResultView getArticleList(@RequestParam("pageNum")Integer pageNum, @RequestParam("pageSize") Integer pageSize, @RequestParam("categoryId") Long categoryId);
}

