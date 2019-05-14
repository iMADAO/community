package com.madao.post.controller;

import com.github.pagehelper.PageInfo;
import com.madao.api.dto.PostDTO;
import com.madao.api.dto.PostSegmentDTO;
import com.madao.api.entity.Post;
import com.madao.api.entity.PostCategory;
import com.madao.api.form.PostGetForm;
import com.madao.api.utils.ResultUtil;
import com.madao.api.utils.ResultView;
import com.madao.post.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class PostController {
    @Autowired
    private PostService postService;

    @RequestMapping("/post/category/parent")
    public ResultView getParentCategory(){
        try {
            List<PostCategory> postCategoryList = postService.getParentCategoryInOrder();
            return ResultUtil.returnSuccess(postCategoryList);
        }catch (Exception e){
            e.printStackTrace();
            return ResultUtil.returnFail("请稍后重试");
        }

    }

    @RequestMapping("/post/category")
    public ResultView getCategoryByParentNode(@RequestParam("parentNode") Long parentNode){
        try {
            List<PostCategory> result = postService.getCategoryInOrderByParentId(parentNode);
            return ResultUtil.returnSuccess(result);
        }catch (Exception e){
            e.printStackTrace();
            return ResultUtil.returnFail();
        }
    }

    @RequestMapping("/post/getList")
    public ResultView getPostListByCategoryId(@RequestBody PostGetForm form){
        try {
            PageInfo<PostDTO> pageInfo = postService.getPostListByCategoryId(form);
            return ResultUtil.returnSuccess(pageInfo);
        }catch (Exception e){
            e.printStackTrace();
            return ResultUtil.returnFail();
        }
    }

    //分页获取帖子每层的信息
    @RequestMapping("/post/postInfo/getContent")
    public ResultView getPostContentByPostId(Long postId, Integer pageNum, Integer pageSize){
        try {
            PageInfo<PostSegmentDTO> resultPageInfo = postService.getPostSegmentByPostId(postId, pageNum, pageSize);
            return ResultUtil.returnSuccess(resultPageInfo);
        }catch (Exception e){
            e.printStackTrace();
            return ResultUtil.returnFail();
        }
    }

    //获取评论分页 todo
    @RequestMapping("/post/postInfo/comment/get")
    public ResultView getSegmentComment(Long segmentId, Integer pageNum, Integer pageSize){
        try{
            postService.getCommentBySegmentId(segmentId, pageNum, pageSize);
        }catch (Exception e){
            e.printStackTrace();
            return ResultUtil.returnFail();
        }
        return null;
    }
}
