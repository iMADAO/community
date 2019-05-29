package com.madao.web.controller;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageInfo;
import com.madao.api.Exception.ResultException;
import com.madao.api.dto.ArticleDTO;
import com.madao.api.dto.UserDTO;
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
import java.util.concurrent.Executors;

@Controller
public class ArticleController {
    @Autowired
    private ArticleService articleService;

    @GetMapping("/toArticle")
    public String toArticle(@RequestParam(value = "pageNum",  defaultValue = "1") Integer pageNum, @RequestParam(value = "pageSize", defaultValue = "5") Integer pageSize, HttpServletRequest request){
        UserDTO user = (UserDTO) request.getSession().getAttribute("user");
        Long userId = user==null ? null : user.getUserId();
        try {
            ResultView resultView = articleService.getArticleList(userId, pageNum, pageSize);
            System.out.println("resultView======" + resultView);
            request.setAttribute("data", resultView);
        }catch (Exception e){
            e.printStackTrace();
        }
        return "article";
    }

    @GetMapping("/toArticle/{categoryId}")
    public String toArticle(HttpServletRequest request, @PathVariable("categoryId") Long categoryId, @RequestParam(value = "pageNum",  defaultValue = "1") Integer pageNum, @RequestParam(value = "pageSize", defaultValue = "5") Integer pageSize){
        UserDTO user = (UserDTO) request.getSession().getAttribute("user");
        Long userId = user==null ? null : user.getUserId();
        try {
            ResultView resultView = articleService.getArticleList(userId, pageNum, pageSize, categoryId);
            request.setAttribute("data", resultView);
            request.setAttribute("categoryId", categoryId);
        }catch (Exception e){
            e.printStackTrace();
        }
        return "article";

    }

    @ResponseBody
    @GetMapping("/toArticle2/{categoryId}")
    public ResultView toArticle2(HttpServletRequest request, @PathVariable("categoryId") Long categoryId, @RequestParam(value = "pageNum",  defaultValue = "1") Integer pageNum, @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize){
        UserDTO user = (UserDTO) request.getSession().getAttribute("user");
        Long userId = user==null ? null : user.getUserId();
        try {
            ResultView resultView = articleService.getArticleList(userId, pageNum, pageSize, categoryId);
            return resultView;
        }catch (Exception e){
            e.printStackTrace();
            return ResultUtil.returnFail();
        }
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
        try {
            UserDTO user = checkUserLogin(request);
            Long userId = user.getUserId();
            String filePath = form.getFilePath();
            form.setUserId(userId);
            //将文档转换成Html页面，保存访问地址
            System.out.println("filePath ...." + filePath);
            String accessUrl = FileConverterUtil.convert2Html(filePath);
            form.setAccessUrl(accessUrl);
            ResultView resultView = articleService.addArticle(form);
            return resultView;
        }catch (ResultException e){
            System.out.println(e.getMessage());
            return ResultUtil.returnFail(e.getMessage());
        }catch (Exception e){
            e.printStackTrace();
            return ResultUtil.returnFail();
        }
    }

    @ResponseBody
    @RequestMapping("/article/person/getList/{pageNum}/{pageSize}")
    public ResultView getArticleByePerson(@PathVariable("pageNum") Integer pageNum, @PathVariable("pageSize") Integer pageSize, HttpServletRequest request){
        try {
            UserDTO user = checkUserLogin(request);
            ResultView resultView = articleService.getArticleListByPerson(user.getUserId(), pageNum, pageSize);
           return resultView;
        }catch (ResultException e){
            System.out.println(e.getMessage());
            return ResultUtil.returnFail(e.getMessage());
        }catch (Exception e){
            e.printStackTrace();
            return ResultUtil.returnFail();
        }
    }

    @ResponseBody
    @RequestMapping("/article/person/visible/{articleId}/{operate}")
    public ResultView operateArticleByPerson(@PathVariable("articleId") Long articleId, @PathVariable("operate") Byte operate, HttpServletRequest request){
        try {
            UserDTO user = checkUserLogin(request);
            ResultView resultView = articleService.operateArticleByPerson(user.getUserId(), articleId, operate);
            return resultView;
        }catch (ResultException e){
            System.out.println(e.getMessage());
            return ResultUtil.returnFail(e.getMessage());
        }catch (Exception e){
            e.printStackTrace();
            return ResultUtil.returnFail();
        }
    }

    @ResponseBody
    @RequestMapping("/article/person/collected/{pageNum}/{pageSize}")
    public ResultView getArticleByePersonCollected(@PathVariable("pageNum") Integer pageNum, @PathVariable("pageSize") Integer pageSize, HttpServletRequest request){
        try {
            UserDTO user = checkUserLogin(request);
            ResultView resultView = articleService.getArticleListByUserCollected(user.getUserId(), pageNum, pageSize);
            return resultView;
        }catch (ResultException e){
            System.out.println(e.getMessage());
            return ResultUtil.returnFail(e.getMessage());
        }catch (Exception e){
            e.printStackTrace();
            return ResultUtil.returnFail();
        }
    }

    @ResponseBody
    @PutMapping("/article/person/collect/{articleId}/{operate}")
    public ResultView collectArticle(@PathVariable("articleId") Long articleId, @PathVariable("operate") Byte operate, HttpServletRequest request){
        try {
            UserDTO user = checkUserLogin(request);
            articleService.collectArticle(user.getUserId(), articleId, operate);
            return ResultUtil.returnSuccess();
        }catch (ResultException e){
            System.out.println(e.getMessage());
            return ResultUtil.returnFail(e.getMessage());
        }catch (Exception e){
            e.printStackTrace();
            return ResultUtil.returnFail();
        }
    }

    @ResponseBody
    @GetMapping("/article/admin/getAllState/{pageNum}/{pageSize}")
    public ResultView getArticleListInAllState(HttpServletRequest request, @PathVariable("pageNum") Integer pageNum, @PathVariable("pageSize")Integer pageSize){
        try {
            UserDTO user = checkUserLogin(request);
            checkAdminAuthority(user);
            ResultView resultView = articleService.getArticleListInAllState(pageNum, pageSize);
            return resultView;
        }catch (ResultException e){
            System.out.println(e.getMessage());
            return ResultUtil.returnFail(e.getMessage());
        }catch (Exception e){
            e.printStackTrace();
            return ResultUtil.returnFail();
        }
    }

    @ResponseBody
    @RequestMapping("/article/admin/ban/{articleId}/{operate}")
    public ResultView articleBanOperate(@PathVariable("articleId") Long articleId, @PathVariable("operate") Byte operate, HttpServletRequest request){
        try{
            UserDTO user = checkUserLogin(request);
            checkAdminAuthority(user);
            ResultView resultView = articleService.operateBanArticle(articleId, operate);
            return resultView;
        }catch (ResultException e){
            System.out.println(e.getMessage());
            return ResultUtil.returnFail(e.getMessage());
        }catch (Exception e){
            e.printStackTrace();
            return ResultUtil.returnFail();
        }
    }

    @ResponseBody
    @RequestMapping("/article/report/{articleId}")
    public ResultView reportArticle(@PathVariable("articleId") Long articleId, @RequestParam("reason")String reason, HttpServletRequest request){
        try {
            UserDTO user = checkUserLogin(request);
            ResultView  resultView = articleService.reportArticle(user.getUserId(), articleId, reason);
            return resultView;
        }catch (ResultException e){
            System.out.println(e.getMessage());
            return ResultUtil.returnFail(e.getMessage());
        }catch (Exception e){
            e.printStackTrace();
            return ResultUtil.returnFail();
        }
    }

    @RequestMapping("/toArticleInfo/{articleId}")
    public String toArticleInfo(@PathVariable("articleId") Long articleId, HttpServletRequest request){
        try {
            ResultView resultView = articleService.getArticleDTOById(articleId);
            request.setAttribute("data", resultView);
            System.out.println(resultView);
        }catch (Exception e){
            e.printStackTrace();
        }
        return "articleInfo";
    }

    @RequestMapping("/article/search")
    public String searchArticle(@RequestParam("searchContent") String searchContent,  @RequestParam(value="pageNum", defaultValue = "1") Integer pageNum, @RequestParam(value="pageSize", defaultValue = "5") Integer pageSize, HttpServletRequest request){
        try{
            ResultView resultView = articleService.searchArticle(searchContent, pageNum, pageSize);
            request.setAttribute("data", resultView);
            request.setAttribute("searchContent", searchContent);
        }catch (Exception e){
            e.printStackTrace();
        }
        return "article";
    }

    @ResponseBody
    @RequestMapping("/article/search1")
    public ResultView searchArticle1(@RequestParam("searchContent") String searchContent,  @RequestParam(value="pageNum", defaultValue = "1") Integer pageNum, @RequestParam(value="pageSize", defaultValue = "5") Integer pageSize, HttpServletRequest request){
        try{
            ResultView resultView = articleService.searchArticle(searchContent, pageNum, pageSize);
            request.setAttribute("data", resultView);
            request.setAttribute("searchContent", searchContent);
            return resultView;
        }catch (Exception e){
            e.printStackTrace();
            return ResultUtil.returnFail();
        }

    }

    private UserDTO checkUserLogin(HttpServletRequest request){
        UserDTO user = (UserDTO) request.getSession().getAttribute("user");
        if(user==null){
            throw new ResultException("用户未登录");
        }
        return user;
    }
    private void checkAdminAuthority(UserDTO user){
        //todo 检查管理员权限
    }
}
