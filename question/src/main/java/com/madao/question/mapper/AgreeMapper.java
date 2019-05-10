package com.madao.question.mapper;

import com.madao.api.entity.Agree;
import com.madao.question.bean.AgreeExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface AgreeMapper {
    int countByExample(AgreeExample example);

    int deleteByExample(AgreeExample example);

    int insert(Agree record);

    int insertSelective(Agree record);

    List<Agree> selectByExample(AgreeExample example);

    int updateByExampleSelective(@Param("record") Agree record, @Param("example") AgreeExample example);

    int updateByExample(@Param("record") Agree record, @Param("example") AgreeExample example);
}