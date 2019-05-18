package com.madao.api.service;

import com.madao.api.entity.PostCategory;
import com.madao.api.entity.SegmentContent;
import com.madao.api.form.BaseForm;
import com.madao.api.form.PostForm;
import com.madao.api.form.PostSegmentForm;
import com.madao.api.utils.ResultView;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(value = "post", fallbackFactory = PostServiceFallbackFactory.class)
public interface PostService {
    @RequestMapping("/post/category")
    public ResultView getCategoryByParentNode(@RequestParam("parentNode") Long parentNode);

    @RequestMapping("/post/category/parent")
    public ResultView getParentCategory();

    @RequestMapping("/post/getList")
    public ResultView getPostListByCategoryId(@RequestBody BaseForm form);

    @RequestMapping("/post/postInfo/getContent")
    public ResultView getPostSegmentByPostId(@RequestBody BaseForm form);

    @RequestMapping("/post/comment")
    public ResultView addPostComment(@RequestParam("segmentId") Long segmentId, @RequestParam("userId") Long userId, @RequestParam("commentContent") String content);

    @RequestMapping("/post/category/get")
    PostCategory getPostCategoryById(@RequestParam("categoryId") Long categoryId);

    @RequestMapping("/post/add")
    ResultView addPost(PostForm postForm);

    @RequestMapping("/post/content/abstract")
    public ResultView getAbstractContentByPostId(@RequestBody List<Long> postIdList);

    @RequestMapping("/post/getList/all")
    ResultView getPostList(@RequestParam("pageNum") Integer pageNum, @RequestParam("pageSize") Integer pageSize);

    @RequestMapping("/post/segment/add")
    ResultView addPostSegment(PostSegmentForm postForm);

    @RequestMapping("/post/postInfo/comment/get")
    ResultView getSegmentComment(@RequestParam("segmentId") Long segmentId, @RequestParam("pageNum") Integer pageNum, @RequestParam("pageSize") Integer pageSize);
}
