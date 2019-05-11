package com.madao.api.entity;

public class Agree {
    public Agree() {
    }

    public Agree(Long userId, Long answerId, Byte type) {

        this.userId = userId;
        this.answerId = answerId;
        this.type = type;
    }

    private Long userId;

    private Long answerId;

    private Byte type;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getAnswerId() {
        return answerId;
    }

    public void setAnswerId(Long answerId) {
        this.answerId = answerId;
    }

    public Byte getType() {
        return type;
    }

    public void setType(Byte type) {
        this.type = type;
    }
}