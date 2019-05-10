package com.madao.question.mapper;

import com.madao.api.entity.AnswerComment;
import com.madao.question.bean.AnswerCommentExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface AnswerCommentMapper {
    int countByExample(AnswerCommentExample example);

    int deleteByExample(AnswerCommentExample example);

    int deleteByPrimaryKey(Long commentId);

    int insert(AnswerComment record);

    int insertSelective(AnswerComment record);

    List<AnswerComment> selectByExample(AnswerCommentExample example);

    AnswerComment selectByPrimaryKey(Long commentId);

    int updateByExampleSelective(@Param("record") AnswerComment record, @Param("example") AnswerCommentExample example);

    int updateByExample(@Param("record") AnswerComment record, @Param("example") AnswerCommentExample example);

    int updateByPrimaryKeySelective(AnswerComment record);

    int updateByPrimaryKey(AnswerComment record);
}