package com.madao.article.controller;

import com.github.pagehelper.PageInfo;
import com.madao.api.Exception.ResultException;
import com.madao.api.dto.ArticleDTO;
import com.madao.api.dto.ArticlePageDTO;
import com.madao.api.entity.Article;
import com.madao.api.entity.ArticleCategory;
import com.madao.api.entity.User;
import com.madao.api.enums.ResultEnum;
import com.madao.api.form.ArticleAddForm;
import com.madao.api.service.UserService;
import com.madao.api.utils.ResultUtil;
import com.madao.api.utils.ResultView;
import com.madao.article.bean.ArticleExample;
import com.madao.article.service.ArticleCategoryService;
import com.madao.article.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
    public ResultView getArticleList(@RequestParam(value="userId", required = false) Long userId,@RequestParam("pageNum")Integer pageNum, @RequestParam("pageSize") Integer pageSize){
        try {
            ArticlePageDTO articlePageDTO = articleService.getArticleDTOVisible(pageNum, pageSize);
            if(userId!=null){
                articleService.populateCollectStateByUser(userId, articlePageDTO.getArticlePageInfo().getList());
            }
            return ResultUtil.returnSuccess(articlePageDTO);
        }catch (Exception e){
            e.printStackTrace();
            return ResultUtil.returnFail();
        }
    }

    @RequestMapping("/article/getList/byCategory")
    public ResultView getArticleList(@RequestParam(value="userId", required = false) Long userId,@RequestParam("pageNum")Integer pageNum, @RequestParam("pageSize") Integer pageSize, @RequestParam("categoryId") Long categoryId){
        try {
            ArticlePageDTO articlePageDTO = articleService.getArticleDTOByCategoryIdVisible(pageNum, pageSize, categoryId);
            if(userId!=null){
                articleService.populateCollectStateByUser(userId, articlePageDTO.getArticlePageInfo().getList());
            }
            return ResultUtil.returnSuccess(articlePageDTO);
        }catch (Exception e){
            e.printStackTrace();
            return ResultUtil.returnFail();
        }
    }

    //根据Id获取文章
    @RequestMapping("/article/get")
    public ResultView<Article> getArticleById(@RequestParam("articleId") Long articleId){
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

    @RequestMapping("/articleDTO/get")
    public ResultView<ArticleDTO> getArticleDTOById(@RequestParam("articleId") Long articleId){
        try {
            ArticleDTO articleDTO = articleService.getArticleDTOById(articleId);
            ResultView<ArticleDTO> resultView = new ResultView<>();
            resultView.setCode(ResultEnum.SUCCESS.getCode());
            resultView.setData(articleDTO);
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


    @RequestMapping("/article/person/getList")
    ResultView getArticleListByPerson(@RequestParam("userId") Long userId, @RequestParam("pageNum") Integer pageNum, @RequestParam("pageSize") Integer pageSize){
        try {
            PageInfo<ArticleDTO> pageInfo = articleService.getArticleDTOByPerson(userId, pageNum, pageSize);
            return ResultUtil.returnSuccess(pageInfo);
        }catch (Exception e){
            e.printStackTrace();
            return ResultUtil.returnFail();
        }
    }

    @RequestMapping("/article/person/operate")
    ResultView operateArticleByPerson(Long userId, Long articleId, Byte operate){
        try{
            articleService.operateArticle(userId, articleId, operate);
            return ResultUtil.returnSuccess();
        }catch (ResultException e){
            System.out.println(e.getMessage());
            return ResultUtil.returnFail(e.getMessage());
        }catch (Exception e){
            e.printStackTrace();
            return ResultUtil.returnFail();
        }
    }

    @RequestMapping("/article/person/collect")
    public ResultView getArticleListByUserCollected(@RequestParam("userId")Long userId, @RequestParam("pageNum")Integer pageNum, @RequestParam("pageSize")Integer pageSize){
        try {
            PageInfo<ArticleDTO> resultPage = articleService.getArticleDTOByUserCollected(userId, pageNum, pageSize);
            return ResultUtil.returnSuccess(resultPage);
        }catch (ResultException e){
            System.out.println(e.getMessage());
            return ResultUtil.returnFail(e.getMessage());
        }catch (Exception e){
            e.printStackTrace();
            return ResultUtil.returnFail();
        }
    }


    //用户操作收藏或者取消收藏
    @RequestMapping("/article/collect")
    public ResultView collectArticle(@RequestParam("userId")Long userId, @RequestParam("articleId")Long articleId, @RequestParam("operate") Byte operate){
        System.out.println(userId + "----" + articleId + "---" + operate);
        try{
            articleService.collectArticle(userId, articleId, operate);
            return ResultUtil.returnSuccess();
        }catch (ResultException e){
            System.out.println(e.getMessage());
            return ResultUtil.returnFail(e.getMessage());
        }catch (Exception e){
            e.printStackTrace();
            return ResultUtil.returnFail();
        }
    }

    @RequestMapping("/article/getList/getState")
    public ResultView getArticleListInAllState(@RequestParam("pageNum")Integer pageNum, @RequestParam("pageSize")Integer pageSize){
        try {
            PageInfo<ArticleDTO> pageInfo = articleService.getArticleDTOInAllState(pageNum, pageSize);
            return ResultUtil.returnSuccess(pageInfo);
        }catch(Exception e){
            e.printStackTrace();
            return ResultUtil.returnFail();
        }
    }


    //管理员操作是否禁用的接口
    @RequestMapping("/article/admin/operate")
    public ResultView operateBanArticle(@RequestParam("articleId") Long articleId, @RequestParam("operate") Byte operate){
        try {
            articleService.banArticle(articleId, operate);
            return ResultUtil.returnSuccess();
        }catch(Exception e){
            e.printStackTrace();
            return ResultUtil.returnFail();
        }
    }

    //举报文章
    @RequestMapping("/article/report")
    ResultView reportArticle(@RequestParam("userId") Long userId, @RequestParam("articleId") Long articleId, @RequestParam("reason")String reason){
        try {
            articleService.reportArticle(userId, articleId, reason);
            return ResultUtil.returnSuccess();
        }catch (ResultException e){
            System.out.println(e.getMessage());
            return ResultUtil.returnFail(e.getMessage());
        }catch (Exception e){
            e.printStackTrace();
            return ResultUtil.returnFail();
        }
    }

    //搜索文章
    @RequestMapping("/article/search")
    ResultView searchArticle(@RequestParam("searchContent") String searchContent, @RequestParam("pageNum") Integer pageNum, @RequestParam("pageSize") Integer pageSize){
        try {
            ArticlePageDTO articlePageDTO = articleService.searchArticleBytTitle(searchContent, pageNum, pageSize);
            return ResultUtil.returnSuccess(articlePageDTO);
        }catch (Exception e){
            e.printStackTrace();
            return ResultUtil.returnFail();
        }

    }
}
