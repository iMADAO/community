package com.madao.post.service;

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
        List<PostCategory> postCategoryList = postService.getParentCategoryInOrder();
        for(PostCategory postCategory: postCategoryList){
            System.out.println(postCategory);
        }
    }

}