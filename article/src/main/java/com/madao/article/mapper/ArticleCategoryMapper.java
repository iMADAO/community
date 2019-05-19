package com.madao.article.mapper;

import com.madao.api.entity.ArticleCategory;
import com.madao.article.bean.ArticleCategoryExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface ArticleCategoryMapper {
    int countByExample(ArticleCategoryExample example);

    int deleteByExample(ArticleCategoryExample example);

    int insert(ArticleCategory record);

    int insertSelective(ArticleCategory record);

    List<ArticleCategory> selectByExample(ArticleCategoryExample example);

    int updateByExampleSelective(@Param("record") ArticleCategory record, @Param("example") ArticleCategoryExample example);

    int updateByExample(@Param("record") ArticleCategory record, @Param("example") ArticleCategoryExample example);
}