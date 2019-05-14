package com.madao.post.mapper;

import com.madao.api.entity.SegmentContent;
import com.madao.post.bean.SegmentContentExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface SegmentContentMapper {
    int countByExample(SegmentContentExample example);

    int deleteByExample(SegmentContentExample example);

    int insert(SegmentContent record);

    int insertSelective(SegmentContent record);

    List<SegmentContent> selectByExampleWithBLOBs(SegmentContentExample example);

    List<SegmentContent> selectByExample(SegmentContentExample example);

    int updateByExampleSelective(@Param("record") SegmentContent record, @Param("example") SegmentContentExample example);

    int updateByExampleWithBLOBs(@Param("record") SegmentContent record, @Param("example") SegmentContentExample example);

    int updateByExample(@Param("record") SegmentContent record, @Param("example") SegmentContentExample example);

    SegmentContent getFirstTextContentBySegmentId(Long firstSegmentId);

    List<SegmentContent> getPicContentBySegmentId(Long firstSegmentId);
}