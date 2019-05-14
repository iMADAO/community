package com.madao.post.mapper;

import com.madao.api.entity.PostCategory;
import com.madao.post.bean.PostCategoryExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface PostCategoryMapper {
    int countByExample(PostCategoryExample example);

    int deleteByExample(PostCategoryExample example);

    int deleteByPrimaryKey(Long categoryId);

    int insert(PostCategory record);

    int insertSelective(PostCategory record);

    List<PostCategory> selectByExample(PostCategoryExample example);

    PostCategory selectByPrimaryKey(Long categoryId);

    int updateByExampleSelective(@Param("record") PostCategory record, @Param("example") PostCategoryExample example);

    int updateByExample(@Param("record") PostCategory record, @Param("example") PostCategoryExample example);

    int updateByPrimaryKeySelective(PostCategory record);

    int updateByPrimaryKey(PostCategory record);
}