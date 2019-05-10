package com.madao.question.mapper;

import com.madao.api.entity.AnswerContent;
import com.madao.question.bean.AnswerContentExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface AnswerContentMapper {
    int countByExample(AnswerContentExample example);

    int deleteByExample(AnswerContentExample example);

    int deleteByPrimaryKey(Long contentId);

    int insert(AnswerContent record);

    int insertSelective(AnswerContent record);

    List<AnswerContent> selectByExample(AnswerContentExample example);

    AnswerContent selectByPrimaryKey(Long contentId);

    int updateByExampleSelective(@Param("record") AnswerContent record, @Param("example") AnswerContentExample example);

    int updateByExample(@Param("record") AnswerContent record, @Param("example") AnswerContentExample example);

    int updateByPrimaryKeySelective(AnswerContent record);

    int updateByPrimaryKey(AnswerContent record);
}