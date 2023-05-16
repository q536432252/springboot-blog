package com.lin.blog.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.util.List;

@Data
public class CommentVo  {
    //1 原来的Comment就有  0 原来无

    //防止精度损失， 把id转为string
    //@JsonSerialize(using = ToStringSerializer.class)
    private String id;

    //0
    private UserVo author;

    private String content;
    //0
    private List<CommentVo> childrens;

    private String createDate;

    private Integer level;
    //0
    private UserVo toUser;
}
