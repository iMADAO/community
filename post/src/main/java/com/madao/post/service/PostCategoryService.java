package com.madao.post.service;

import com.madao.api.entity.PostCategory;
import com.madao.post.mapper.PostCategoryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PostCategoryService {
    @Autowired
    private PostCategoryMapper postCategoryMapper;

    public PostCategory getCategoryById(Long categoryId){
        PostCategory postCategory = postCategoryMapper.selectByPrimaryKey(categoryId);
        return postCategory;
    }
}
