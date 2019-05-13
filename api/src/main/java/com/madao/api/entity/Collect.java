package com.madao.api.entity;

public class Collect {
    private Long collectId;

    public Collect(Long collectId, Long userId, Long targetId, Byte type) {
        this.collectId = collectId;
        this.userId = userId;
        this.targetId = targetId;
        this.type = type;
    }


    private Long userId;

    private Long targetId;

    private Byte type;

    public Collect() {
    }

    public Collect(Long userId, Long targetId, Byte type) {

        this.userId = userId;
        this.targetId = targetId;
        this.type = type;
    }

    public Long getCollectId() {
        return collectId;
    }

    public void setCollectId(Long collectId) {
        this.collectId = collectId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getTargetId() {
        return targetId;
    }

    public void setTargetId(Long targetId) {
        this.targetId = targetId;
    }

    public Byte getType() {
        return type;
    }

    public void setType(Byte type) {
        this.type = type;
    }
}