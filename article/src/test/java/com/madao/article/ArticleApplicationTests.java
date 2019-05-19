package com.madao.article;

import com.madao.api.entity.ArticleCategory;
import com.madao.api.utils.KeyUtil;
import com.madao.article.mapper.ArticleCategoryMapper;
import com.madao.article.service.ArticleCategoryService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ArticleApplicationTests {
	@Autowired
	private ArticleCategoryService articleCategoryService;

	@Autowired
	private ArticleCategoryMapper mapper;
	@Test
	public void contextLoads() {
		List<ArticleCategory> categoryList = articleCategoryService.getAllCategory();
		System.out.println(categoryList.size());
	}

	@Test
	public void test(){
		ArticleCategory articleCategory = new ArticleCategory();
		articleCategory.setCategoryId(KeyUtil.genUniquKeyOnLong());
		articleCategory.setCategoryName("小说");
		mapper.insert(articleCategory);
	}

}
