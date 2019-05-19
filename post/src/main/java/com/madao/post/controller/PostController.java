package com.madao.post.controller;

import com.github.pagehelper.PageInfo;
import com.madao.api.dto.*;
import com.madao.api.entity.Post;
import com.madao.api.entity.PostCategory;
import com.madao.api.entity.PostSegment;
import com.madao.api.entity.SegmentContent;
import com.madao.api.form.BaseForm;
import com.madao.api.form.PostForm;
import com.madao.api.form.PostGetForm;
import com.madao.api.form.PostSegmentForm;
import com.madao.api.utils.ResultUtil;
import com.madao.api.utils.ResultView;
import com.madao.post.service.PostCategoryService;
import com.madao.post.service.PostCommentService;
import com.madao.post.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class PostController {
    @Autowired
    private PostService postService;

    @Autowired
    private PostCommentService postCommentService;

    @Autowired
    private PostCategoryService postCategoryService;

    //获取所有一级分类
    @RequestMapping("/post/category/parent")
    public ResultView getParentCategory(){
        try {
            ParentCategoryDTO parentCategoryDTO = postService.getParentCategoryInOrder();
            return ResultUtil.returnSuccess(parentCategoryDTO);
        }catch (Exception e){
            e.printStackTrace();
            return ResultUtil.returnFail("请稍后重试");
        }

    }

    //根据一级分类获取二级分类
    @RequestMapping("/post/category")
    public ResultView getCategoryByParentNode(@RequestParam("parentNode") Long parentNode){
        System.out.println(parentNode);
        try {
            List<PostCategory> result = postService.getCategoryInOrderByParentId(parentNode);
            System.out.println(result.size());
            return ResultUtil.returnSuccess(result);
        }catch (Exception e){
            e.printStackTrace();
            return ResultUtil.returnFail();
        }
    }

    //分页获取某类型的帖子
    @RequestMapping("/post/getList")
    public ResultView getPostListByCategoryId(@RequestBody BaseForm form){
        try {
            PageInfo<PostDTO> pageInfo = postService.getPostListByCategoryId(form);
            return ResultUtil.returnSuccess(pageInfo);
        }catch (Exception e){
            e.printStackTrace();
            return ResultUtil.returnFail();
        }
    }

    //分页获取所有类型的帖子
    @RequestMapping("/post/getList/all")
    ResultView getPostList(@RequestParam("pageNum") Integer pageNum, @RequestParam("pageSize") Integer pageSize){
        try {
            PageInfo<PostDTO> pageInfo = postService.getPostList(pageNum, pageSize);
            return ResultUtil.returnSuccess(pageInfo);
        }catch (Exception e){
            e.printStackTrace();
            return ResultUtil.returnFail();
        }
    }

    //分页获取帖子每层的信息
    @RequestMapping("/post/postInfo/getContent")
    public ResultView getPostSegmentByPostId(@RequestBody PostGetForm form){
        try {
            PageInfo<PostSegmentDTO> resultPageInfo = postService.getPostSegmentByPostId(form.getPostId(), form.getPageNum(), form.getPageSize(), form.getCommentPageSize());
            return ResultUtil.returnSuccess(resultPageInfo);
        }catch (Exception e){
            e.printStackTrace();
            return ResultUtil.returnFail();
        }
    }

    //获取评论分页
    @RequestMapping("/post/postInfo/comment/get")
    ResultView getSegmentComment(@RequestParam("segmentId") Long segmentId, @RequestParam("pageNum") Integer pageNum, @RequestParam("pageSize") Integer pageSize){
        try{
            PageInfo<PostCommentDTO> resultPage = postCommentService.getCommentBySegmentId(segmentId, pageNum, pageSize);
            return ResultUtil.returnSuccess(resultPage);
        }catch (Exception e){
            e.printStackTrace();
            return ResultUtil.returnFail();
        }
    }

    //添加评论
    @RequestMapping("/post/comment")
    public ResultView addPostComment(@RequestParam("segmentId") Long segmentId, @RequestParam("userId") Long userId, @RequestParam("commentContent") String commentContent){
        System.out.println(commentContent);
        try{
            postCommentService.addComment(segmentId, userId, commentContent);
            return ResultUtil.returnSuccess();
        }catch (Exception e){
            e.printStackTrace();
            return ResultUtil.returnFail();
        }
    }

    @ResponseBody
    @RequestMapping("/post/category/get")
    PostCategory getPostCategoryById(@RequestParam("categoryId") Long categoryId){
        return postCategoryService.getCategoryById(categoryId);
    }

    //添加帖子
    @ResponseBody
    @RequestMapping("/post/add")
    ResultView addPost(@RequestBody PostForm postForm){
        try {
            Post post  = postService.insertPost(postForm);
            return ResultUtil.returnSuccess(post);
        }catch (Exception e){
            e.printStackTrace();
            return ResultUtil.returnFail();
        }
    }

    @ResponseBody
    @RequestMapping("/post/segment/add")
    ResultView addPostSegment(@RequestBody PostSegmentForm postForm){
        try {
            PostSegment postSegment  = postService.insertPostSegment(postForm);
            return ResultUtil.returnSuccess(postSegment);
        }catch (Exception e){
            e.printStackTrace();
            return ResultUtil.returnFail();
        }
    }


    @ResponseBody
    @RequestMapping("/post/content/abstract")
    public ResultView getAbstractContentByPostId(@RequestBody List<Long> postIdList){
        try {
            List<List<String>> postContent = postService.getContentByPostIdList(postIdList);
            return ResultUtil.returnSuccess(postContent);
        }catch (Exception e){
            e.printStackTrace();
            return ResultUtil.returnFail();
        }
    }

    //获取用户收藏的帖子
    @ResponseBody
    @RequestMapping("/post/user/collect")
    ResultView getPostCollect(@RequestParam("userId") Long userId, @RequestParam("pageNum") Integer pageNum, @RequestParam("pageSize") Integer pageSize){
        try {
            PageInfo<CollectDTO<PostDTO>> resultPage = postService.getCollectPost(userId, pageNum, pageSize);
            return ResultUtil.returnSuccess(resultPage);
        }catch (Exception e){
            e.printStackTrace();
            return ResultUtil.returnFail();
        }
    }

    @ResponseBody
    @RequestMapping("/post/collect/state")
    ResultView getPostCollectState(@RequestParam("userId") Long userId, @RequestParam("postId") Long postId){
        try {
            boolean result = postService.getUserCollectPostState(userId, postId);
            return ResultUtil.returnSuccess(result);
        }catch (Exception e){
            e.printStackTrace();
            return ResultUtil.returnFail();
        }
    }

    @ResponseBody
    @RequestMapping("/post/collect/update")
    ResultView setPostCollect(@RequestParam("userId") Long userId, @RequestParam("postId") Long postId, @RequestParam("operate") Byte operate){
        try {
            postService.updatePostCollectState(userId, postId, operate);
            return ResultUtil.returnSuccess();
        }catch (Exception e){
            e.printStackTrace();
            return ResultUtil.returnFail();
        }

    }

}
