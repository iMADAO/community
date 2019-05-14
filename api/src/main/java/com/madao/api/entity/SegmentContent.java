package com.madao.api.entity;

public class SegmentContent {

    @Override
    public String toString() {
        return "SegmentContent{" +
                "contentId=" + contentId +
                ", segmentId=" + segmentId +
                ", type=" + type +
                ", contentOrder=" + contentOrder +
                ", content='" + content + '\'' +
                '}';
    }
    private Long contentId;

    private Long segmentId;

    private Byte type;

    private Integer contentOrder;

    private String content;

    public Long getContentId() {
        return contentId;
    }

    public void setContentId(Long contentId) {
        this.contentId = contentId;
    }

    public Long getSegmentId() {
        return segmentId;
    }

    public void setSegmentId(Long segmentId) {
        this.segmentId = segmentId;
    }

    public Byte getType() {
        return type;
    }

    public void setType(Byte type) {
        this.type = type;
    }

    public Integer getContentOrder() {
        return contentOrder;
    }

    public void setContentOrder(Integer contentOrder) {
        this.contentOrder = contentOrder;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content == null ? null : content.trim();
    }
}