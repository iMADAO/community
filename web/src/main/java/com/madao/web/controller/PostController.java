package com.madao.web.controller;

import com.madao.api.entity.PostCategory;
import com.madao.api.entity.User;
import com.madao.api.form.*;
import com.madao.api.service.PostService;
import com.madao.api.utils.ResultUtil;
import com.madao.api.utils.ResultView;
import feign.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.xml.crypto.Data;
import java.util.LinkedHashMap;
import java.util.List;

@Controller
public class PostController {
    @Autowired
    private PostService postService;

    public static final int DEFAULT_SIZE = 10;

    //根据类别分页获取帖子，并返回页面
    @RequestMapping("/toPost/{categoryId}")
    public String toPostByCategory(@PathVariable("categoryId") Long categoryId, HttpServletRequest request){
        BaseForm baseForm = new BaseForm(categoryId, 1, DEFAULT_SIZE);
        ResultView resultView = postService.getPostListByCategoryId(baseForm);
        request.setAttribute("data", resultView);

        PostCategory postCategory = postService.getPostCategoryById(categoryId);
        if(postCategory!=null) {
            request.setAttribute("category", postCategory);
        }
        return "post";
    }

    //分页获取帖子，不分类,返回页面
    @RequestMapping("/toPost")
    public String toPost(HttpServletRequest request){
        ResultView resultView = postService.getPostList(1, DEFAULT_SIZE);
        System.out.println("toPost....." + resultView);
        request.setAttribute("data", resultView);
        return "post";

    }


    //测试查看数据用
    @ResponseBody
    @RequestMapping("/toPost2/{categoryId}")
    public ResultView toPostByCategory(@PathVariable("categoryId") Long categoryId){
        BaseForm baseForm = new BaseForm(categoryId, 1, DEFAULT_SIZE);
        ResultView resultView = postService.getPostListByCategoryId(baseForm);
        return resultView;
    }

//    @GetMapping("/toIndex")
//    public String toIndex(){
//        return "index";
//    }

    @ResponseBody
    @GetMapping("/post/category/parent")
    public ResultView getParentCategory(){
       return postService.getParentCategory();
    }

    @ResponseBody
    @GetMapping("/post/category")
    public ResultView getChildCategory(Long parentNodeId){
        System.out.println(parentNodeId);
        return postService.getCategoryByParentNode(parentNodeId);
    }

    @ResponseBody
    @GetMapping("/post/{categoryId}/{pageNum}/{pageSize}")
    public ResultView getPostByCategory(@PathVariable("categoryId") Long categoryId, @PathVariable("pageNum") Integer pageNum, @PathVariable("pageSize") Integer pageSize){
        BaseForm baseForm = new BaseForm(categoryId, pageNum, pageSize);
        try {
            return postService.getPostListByCategoryId(baseForm);
        }catch (Exception e){
            e.printStackTrace();
            return ResultUtil.returnFail();
        }
    }

    @RequestMapping("/post/info/{postId}/{pageNum}/{pageSize}/{commentPageSize}")
    public String toPostInfo(@PathVariable("postId") Long postId, @PathVariable("pageNum") Integer pageNum, @PathVariable("pageSize") Integer pageSize, @PathVariable("commentPageSize") Integer commentPageSize ,HttpServletRequest request){
        PostGetForm form = new PostGetForm(postId, pageNum, pageSize, commentPageSize);
        try {
            ResultView resultView = postService.getPostSegmentByPostId(form);
            request.setAttribute("data", resultView);
        }catch (Exception e){
            e.printStackTrace();
        }
        return "postInfo";
    }

    @ResponseBody
    @RequestMapping("/post/info/test/{postId}/{pageNum}/{pageSize}/{commentPageSize}")
    public ResultView toPostInfoTest(@PathVariable("postId") Long postId, @PathVariable("pageNum") Integer pageNum, @PathVariable("pageSize") Integer pageSize, @PathVariable("commentPageSize") Integer commentPageSize, HttpServletRequest request){
        PostGetForm form = new PostGetForm(postId, pageNum, pageSize, commentPageSize);
        try {
            ResultView resultView = postService.getPostSegmentByPostId(form);
            return resultView;
        }catch (Exception e){
            e.printStackTrace();
            return ResultUtil.returnFail();
        }
    }

    @ResponseBody
    @RequestMapping("/post/info2/{postId}/{pageNum}/{pageSize}/{commentPageSize}")
    public ResultView toPostInfo2(@PathVariable("postId") Long postId, @PathVariable("pageNum") Integer pageNum, @PathVariable("pageSize") Integer pageSize, @PathVariable("commentPageSize") Integer commentPageSize, HttpServletRequest request){
        PostGetForm form = new PostGetForm(postId, pageNum, pageSize, commentPageSize);
        try {
            ResultView resultView = postService.getPostSegmentByPostId(form);
            return resultView;
        }catch (Exception e){
            e.printStackTrace();
        }
        return ResultUtil.returnSuccess();
    }

    @ResponseBody
    @PostMapping("/post/comment/{segmentId}")
    public ResultView addPostComment(@PathVariable("segmentId") Long segmentId,@RequestParam("commentContent") String commentContent, HttpServletRequest request){
        System.out.println("commentContent:...." + commentContent);
        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            return ResultUtil.returnFail("用户未登录,请登录后重试");
        }
        Long userId = user.getUserId();
        try{
            ResultView resultView = postService.addPostComment(segmentId, userId, commentContent);
            return resultView;
        }catch (Exception e){
            e.printStackTrace();
            return ResultUtil.returnFail();
        }
    }

    @ResponseBody
    @PostMapping("/post/{categoryId}")
    public ResultView addPost(@RequestBody List<ContentForm> form, @PathVariable("categoryId") Long categoryId, HttpServletRequest request){
        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            return ResultUtil.returnFail("用户未登录,请登录后重试");
        }
        Long userId = user.getUserId();

        PostForm postForm = new PostForm();
        postForm.setUserId(userId);
        postForm.setAnswerContentFormList(form);
        postForm.setCategoryId(categoryId);
        return postService.addPost(postForm);
    }

    @ResponseBody
    @PostMapping("/post/segment/{postId}")
    public ResultView addPostSegment(@RequestBody List<ContentForm> form, @PathVariable("postId") Long postId, HttpServletRequest request){
        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            return ResultUtil.returnFail("用户未登录,请登录后重试");
        }
        Long userId = user.getUserId();
        PostSegmentForm postSegmentForm = new PostSegmentForm();
        postSegmentForm.setUserId(userId);
        postSegmentForm.setAnswerContentFormList(form);
        postSegmentForm.setPostId(postId);
        return postService.addPostSegment(postSegmentForm);
    }

    //
    @ResponseBody
    @RequestMapping("/post/content/abstract")
    public ResultView getPostContentAbstractByPostId(@RequestParam("postIdList") List<Long> postIdList){
        try {
            return postService.getAbstractContentByPostId(postIdList);
        }catch (Exception e){
            e.printStackTrace();
            return ResultUtil.returnFail();
        }
    }

    @ResponseBody
    @RequestMapping("/post/segment/comment/{segmentId}/{pageNum}/{pageSize}")
    public ResultView getPostComment(@PathVariable("segmentId") Long segmentId, @PathVariable("pageSize") Integer pageSize, @PathVariable("pageNum") Integer pageNum){
        try{
            ResultView resultView = postService.getSegmentComment(segmentId, pageNum, pageSize);
            return resultView;
        }catch (Exception e){
            e.printStackTrace();
            return ResultUtil.returnFail();
        }
    }


    @ResponseBody
    @RequestMapping("/post/collect/state/{postId}")
    public ResultView getUserPostCollectState(@PathVariable("postId") Long postId, HttpServletRequest request){
        try{
            User user = (User) request.getSession().getAttribute("user");
            if (user == null) {
                return ResultUtil.returnFail("用户未登录,请登录后重试");
            }
            Long userId = user.getUserId();
            ResultView resultView = postService.getPostCollectState(userId, postId);
            return resultView;
        }catch (Exception e){
            e.printStackTrace();
            return ResultUtil.returnFail();
        }
    }

    //用户进行帖子收藏和取消收藏操作
    @ResponseBody
    @RequestMapping("/post/collect/operate/{postId}/{operate}")
    public ResultView setPostCollectState(@PathVariable("postId") Long postId, @PathVariable("operate") Byte operate, HttpServletRequest request){
        try{
            User user = (User) request.getSession().getAttribute("user");
            if (user == null) {
                return ResultUtil.returnFail("用户未登录,请登录后重试");
            }
            Long userId = user.getUserId();
            ResultView resultView = postService.setPostCollect(userId, postId, operate);
            return resultView;
        }catch (Exception e){
            e.printStackTrace();
            return ResultUtil.returnFail();
        }

    }



}
