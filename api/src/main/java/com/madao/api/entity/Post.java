package com.madao.api.entity;

import java.util.Date;

public class Post {
    private Long postId;

    private Long userId;

    private Date createTime;

    private Date updateTime;

    private Long categoryId;

    private Integer segmentCount;

    public Long getPostId() {
        return postId;
    }

    public void setPostId(Long postId) {
        this.postId = postId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public Integer getSegmentCount() {
        return segmentCount;
    }

    public void setSegmentCount(Integer segmentCount) {
        this.segmentCount = segmentCount;
    }
}