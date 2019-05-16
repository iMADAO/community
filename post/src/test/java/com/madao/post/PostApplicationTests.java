package com.madao.post;

import com.github.pagehelper.PageInfo;
import com.madao.api.dto.PostCommentDTO;
import com.madao.api.entity.*;
import com.madao.api.enums.ContentTypeEnum;
import com.madao.api.utils.KeyUtil;
import com.madao.post.bean.SegmentContentExample;
import com.madao.post.mapper.*;
import com.madao.post.service.PostCommentService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.swing.text.Segment;
import java.security.Key;
import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PostApplicationTests {
	@Autowired
	private PostCategoryMapper postCategoryMapper;

	@Autowired
	private PostSegmentMapper postSegmentMapper;

	@Autowired
	private PostMapper postMapper;

	@Autowired
	private PostCommentMapper postCommentMapper;

	@Autowired
	private SegmentContentMapper segmentContentMapper;

	@Autowired
	private PostCommentService postCommentService;

	@Test
	public void testPostCommentService(){
		PageInfo<PostCommentDTO> page  = postCommentService.getCommentBySegmentId(1557904805079397400L, 1, 1);
		System.out.println(page);
	}

	@Test
	public void contextLoads() {
		PostCategory postCategory = new PostCategory();
		postCategory.setCategoryId(KeyUtil.genUniquKeyOnLong());
		postCategory.setCategoryName("足球");
		postCategory.setCategoryOrder(1);
		postCategory.setParentNode(1557836845623629708L);
		int n = postCategoryMapper.insertSelective(postCategory);
		org.junit.Assert.assertEquals(n, 1);
	}

	@Test
	public void testGet() {
		PostCategory postCategory = postCategoryMapper.selectByPrimaryKey(1557831588293496894L);
		System.out.println(postCategory);
		Assert.assertNotNull(postCategory);
	}

	@Test
	public void testAddPost(){
		Post post = new Post();
		post.setCategoryId(1557889452011354329L);
		post.setPostId(KeyUtil.genUniquKeyOnLong());
		post.setSegmentCount(2);
		post.setUserId(1L);
		postMapper.insertSelective(post);
	}

	@Test
	public void testAddPostSegment(){
		PostSegment postSegment = new PostSegment();
		postSegment.setCommentCount(0);
		postSegment.setPostId(1557904500181210978L);
		postSegment.setSegmentId(KeyUtil.genUniquKeyOnLong());
		postSegment.setSegOrder(1);
		postSegment.setUserId(1L);
		postSegmentMapper.insertSelective(postSegment);
	}

	@Test
	public void testAddSegmentContent(){
		SegmentContent segmentContent = new SegmentContent();
		segmentContent.setContentId(KeyUtil.genUniquKeyOnLong());
		segmentContent.setContent("哈哈");
		segmentContent.setType(ContentTypeEnum.TEXT.getCode());
		segmentContent.setContentOrder(1);
		segmentContent.setSegmentId(1557904805079397461L);
		segmentContentMapper.insertSelective(segmentContent);
	}

	@Test
	public void testAddSegmentContent2(){
		SegmentContent segmentContent = new SegmentContent();
		segmentContent.setContentId(KeyUtil.genUniquKeyOnLong());
		segmentContent.setContent("http://localhost:8080/pic/1557804678738182338.jpg");
		segmentContent.setType(ContentTypeEnum.PICTURE.getCode());
		segmentContent.setContentOrder(2);
		segmentContent.setSegmentId(1557904805079397461L);
		segmentContentMapper.insertSelective(segmentContent);
	}

	@Test
	public void test2(){
		SegmentContentExample contentExample = new SegmentContentExample();
		SegmentContentExample.Criteria contentCriteria = contentExample.createCriteria();
		contentCriteria.andSegmentIdEqualTo(1557904805079397461L);
		List<SegmentContent> segmentContentList = segmentContentMapper.selectByExampleWithBLOBs(contentExample);
		segmentContentList.stream().forEach(System.out::println);
	}

	@Test
	public void testPostComment(){
		PostComment postComment = new PostComment();
		postComment.setUserId(1L);
		postComment.setSegmentId(1557904805079397400L);
		postComment.setCommentId(KeyUtil.genUniquKeyOnLong());
		postComment.setCommentContent("哈哈");
		postCommentMapper.insertSelective(postComment);
	}

	@Test
	public  void testList(){
		List<String> list = new ArrayList<>(4);
		System.out.println(list.size());
		list.add("str");
		System.out.println(list.size());
	}

}
