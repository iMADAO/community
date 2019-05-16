package com.madao.api.dto;

import com.madao.api.entity.PostCategory;
import lombok.Data;

import java.util.List;

@Data
public class ParentCategoryDTO {
    private List<PostCategory> parentCategoryList;
    private List<PostCategory> childCategoryInFirstParentList;
}
