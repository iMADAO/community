package com.madao.post.mapper;

import com.madao.api.entity.PostSegment;
import com.madao.api.entity.SegmentContent;
import com.madao.post.bean.PostSegmentExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface PostSegmentMapper {
    int countByExample(PostSegmentExample example);

    int deleteByExample(PostSegmentExample example);

    int deleteByPrimaryKey(Long segmentId);

    int insert(PostSegment record);

    int insertSelective(PostSegment record);

    List<PostSegment> selectByExample(PostSegmentExample example);

    PostSegment selectByPrimaryKey(Long segmentId);

    int updateByExampleSelective(@Param("record") PostSegment record, @Param("example") PostSegmentExample example);

    int updateByExample(@Param("record") PostSegment record, @Param("example") PostSegmentExample example);

    int updateByPrimaryKeySelective(PostSegment record);

    int updateByPrimaryKey(PostSegment record);

    Long getFirstSegmentId(Long postId);

}