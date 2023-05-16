package com.lin.blog.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lin.blog.pojo.Comment;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentsMapper extends BaseMapper<Comment> {
}
