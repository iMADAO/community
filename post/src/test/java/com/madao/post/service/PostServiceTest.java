package com.madao.post.service;

import com.madao.api.dto.ParentCategoryDTO;
import com.madao.api.entity.PostCategory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PostServiceTest {

    @Autowired
    private PostService postService;

    @Test
    public void test(){
        ParentCategoryDTO parentCategoryDTO = postService.getParentCategoryInOrder();
    }

    @Test
    public void test2(){
        List<PostCategory> postCategoryList = postService.getCategoryInOrderByParentId(1557831588293496894L);
        System.out.println(postCategoryList.size());
    }

}