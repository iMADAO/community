package com.madao.article;

import com.madao.api.entity.Article;
import com.madao.api.entity.ArticleCategory;
import com.madao.api.entity.User;
import com.madao.api.enums.CollectTypeEnum;
import com.madao.api.service.UserService;
import com.madao.api.utils.KeyUtil;
import com.madao.api.utils.MD5Encoder;
import com.madao.article.mapper.ArticleCategoryMapper;
import com.madao.article.mapper.ArticleMapper;
import com.madao.article.mapper.CollectMapper;
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
	private ArticleMapper articleMapper;

	@Autowired
	private UserService userService;

	@Autowired
	private ArticleCategoryMapper mapper;

	@Autowired
	private CollectMapper collectMapper;
	@Test
	public void contextLoads() {
		List<ArticleCategory> categoryList = articleCategoryService.getAllCategory();
		System.out.println(categoryList.size());
	}

	@Test
	public void test(){
		ArticleCategory articleCategory = new ArticleCategory();
		articleCategory.setCategoryId(KeyUtil.genUniquKeyOnLong());
		articleCategory.setCategoryName("教育");
		mapper.insert(articleCategory);
	}

	@Test
	public void testAdd(){
		Article article = new Article();
		article.setArticleId(KeyUtil.genUniquKeyOnLong());
		article.setDownloadCount(0);
		article.setAccessUrl("http://localhost:8080/a.html");
		article.setCategoryId(1558285095450166899L);
		article.setTitle("标题1");
		article.setUserId(1L);
		articleMapper.insertSelective(article);
	}

	@Test
	public void testUserService(){
		User user = userService.getUserById(1L);
		System.out.println(user);
	}

	@Test
	public void testCollect(){
		List<Long> list =  collectMapper.getTargetIdByUser(1L, CollectTypeEnum.ARTICLE.getCode());
		list.stream().forEach(System.out::println);
	}

	@Test
	public void test1(){
//		Article article = articleMapper.selectByPrimaryKey(1558314972549912149L);
//		System.out.println(article);
		String password = MD5Encoder.getEncryptedWithSalt("zzw123", "2");
		System.out.println(password);
	}

}
