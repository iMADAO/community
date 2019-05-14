package com.madao.post;

import com.madao.api.entity.PostCategory;
import com.madao.api.utils.KeyUtil;
import com.madao.post.mapper.PostCategoryMapper;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PostApplicationTests {
	@Autowired
	private PostCategoryMapper postCategoryMapper;
	@Test
	public void contextLoads() {
		PostCategory postCategory = new PostCategory();
		postCategory.setCategoryId(KeyUtil.genUniquKeyOnLong());
		postCategory.setCategoryName("体育");
		postCategory.setCategoryOrder(1);
		int n = postCategoryMapper.insertSelective(postCategory);
		org.junit.Assert.assertEquals(n, 1);
	}

	@Test
	public void testGet() {
		PostCategory postCategory = postCategoryMapper.selectByPrimaryKey(1557831588293496894L);
		System.out.println(postCategory);
		Assert.assertNotNull(postCategory);
	}

}
