package com.madao.article.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.madao.api.entity.Article;
import com.madao.api.entity.ArticleCategory;
import com.madao.article.bean.ArticleCategoryExample;
import com.madao.article.bean.ArticleExample;
import com.madao.article.mapper.ArticleCategoryMapper;
import com.madao.article.mapper.ArticleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ArticleCategoryService {
    //获取所有类别的文章
    @Autowired
    private ArticleCategoryMapper articleCategoryMapper;

    @Autowired
    private ArticleMapper articleMapper;

    public List<ArticleCategory> getAllCategory() {
        ArticleCategoryExample example = new ArticleCategoryExample();
        List<ArticleCategory> articleCategoryList = articleCategoryMapper.selectByExample(example);
        return articleCategoryList;
    }

    private PageInfo<Article> getArticleListByCategoryId(Long categoryId, int pageNum, int pageSize){
        PageHelper.startPage(pageNum, pageSize);
        ArticleExample example = new ArticleExample();
        ArticleExample.Criteria criteria = example.createCriteria();
        criteria.andCategoryIdEqualTo(categoryId);
        List<Article> articleList = articleMapper.selectByExample(example);
        PageInfo<Article> pageInfo = new PageInfo<>(articleList);
        return pageInfo;
    }
}
