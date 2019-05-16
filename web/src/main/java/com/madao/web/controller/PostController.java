package com.madao.web.controller;

import com.madao.api.entity.PostCategory;
import com.madao.api.form.BaseForm;
import com.madao.api.form.ContentForm;
import com.madao.api.form.PostForm;
import com.madao.api.service.PostService;
import com.madao.api.utils.ResultUtil;
import com.madao.api.utils.ResultView;
import feign.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.LinkedHashMap;
import java.util.List;

@Controller
public class PostController {
    @Autowired
    private PostService postService;

    public static final int DEFAULT_SIZE = 10;

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

    @ResponseBody
    @RequestMapping("/toPost2/{categoryId}")
    public ResultView toPostByCategory(@PathVariable("categoryId") Long categoryId){
        BaseForm baseForm = new BaseForm(categoryId, 1, DEFAULT_SIZE);
        ResultView resultView = postService.getPostListByCategoryId(baseForm);
        return resultView;
    }

    @GetMapping("/toIndex")
    public String toIndex(){
        return "index";
    }

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

    @RequestMapping("/post/info/{postId}/{pageNum}/{pageSize}")
    public String toPostInfo(@PathVariable("postId") Long postId, @PathVariable("pageNum") Integer pageNum, @PathVariable("pageSize") Integer pageSize, HttpServletRequest request){
        BaseForm form = new BaseForm(postId, pageNum, pageSize);
        try {
            ResultView resultView = postService.getPostSegmentByPostId(form);
            request.setAttribute("data", resultView);
        }catch (Exception e){
            e.printStackTrace();
        }
        return "postInfo";
    }

    @ResponseBody
    @RequestMapping("/post/info2/{postId}/{pageNum}/{pageSize}")
    public ResultView toPostInfo2(@PathVariable("postId") Long postId, @PathVariable("pageNum") Integer pageNum, @PathVariable("pageSize") Integer pageSize, HttpServletRequest request){
        BaseForm form = new BaseForm(postId, pageNum, pageSize);
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
        Object userObject =  request.getSession().getAttribute("user");
        if(userObject==null){
            return ResultUtil.returnFail("用户未登录,请登录后重试");
        }
        LinkedHashMap<String, Object> map = (LinkedHashMap<String, Object>) userObject;
        Long userId =  Long.parseLong(map.get("userId").toString());
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
        Object userObject =  request.getSession().getAttribute("user");
        if(userObject==null){
            return ResultUtil.returnFail("用户未登录,请登录后重试");
        }
        LinkedHashMap<String, Object> map = (LinkedHashMap<String, Object>) userObject;
        Long userId =  Long.parseLong(map.get("userId").toString());

        PostForm postForm = new PostForm();
        postForm.setUserId(userId);
        postForm.setAnswerContentFormList(form);
        postForm.setCategoryId(categoryId);
        return postService.addPost(postForm);
    }

    @ResponseBody
    @RequestMapping("/post/content/abstract")
    public ResultView getPostContentAbstractByPostId(@RequestBody List<Long> postIdList){
        try {
            return postService.getAbstractContentByPostId(postIdList);
        }catch (Exception e){
            e.printStackTrace();
            return ResultUtil.returnFail();
        }
    }

}
