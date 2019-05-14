package com.madao.post.mapper;

import com.madao.api.entity.SegmentContent;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SegmentContentMapperTest {
    @Autowired
    private SegmentContentMapper mapper;

    @Test
    public void getFirstTextContentBySegmentId() {
        SegmentContent content = mapper.getFirstTextContentBySegmentId(1L);
        System.out.println(content);
    }

    @Test
    public void getPicContentBySegmentId() {
        List<SegmentContent> contentList = mapper.getPicContentBySegmentId(1L);
        System.out.println(contentList.size());
    }
}