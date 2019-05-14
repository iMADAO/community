package com.madao.api.service;

import com.madao.api.utils.ResultView;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "post", fallbackFactory = PostServiceFallbackFactory.class)
public interface PostService {
    @RequestMapping("/post/category")
    public ResultView getCategoryByParentNode(@RequestParam("parentNode") Long parentNode);

    @RequestMapping("/post/category/parent")
    public ResultView getParentCategory();
}
