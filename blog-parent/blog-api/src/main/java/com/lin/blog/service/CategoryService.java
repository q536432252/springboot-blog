package com.lin.blog.service;


import com.lin.blog.vo.CategoryVo;
import com.lin.blog.vo.Result;

public interface CategoryService {
    CategoryVo findCategoryById(Long categoryId);

    Result findAll();

    Result findAllDetail();

    Result categoriesDetailById(Long id);
}
