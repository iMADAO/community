package com.madao.api.entity;

public class PostCategory {
    private Long categoryId;

    @Override
    public String toString() {
        return "PostCategory{" +
                "categoryId=" + categoryId +
                ", categoryName='" + categoryName + '\'' +
                ", parentNode=" + parentNode +
                ", categoryOrder=" + categoryOrder +
                '}';
    }

    private String categoryName;

    private Long parentNode;

    private Integer categoryOrder;

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName == null ? null : categoryName.trim();
    }

    public Long getParentNode() {
        return parentNode;
    }

    public void setParentNode(Long parentNode) {
        this.parentNode = parentNode;
    }

    public Integer getCategoryOrder() {
        return categoryOrder;
    }

    public void setCategoryOrder(Integer categoryOrder) {
        this.categoryOrder = categoryOrder;
    }
}