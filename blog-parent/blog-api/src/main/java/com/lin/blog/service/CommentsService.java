package com.lin.blog.service;

import com.lin.blog.vo.Result;
import com.lin.blog.vo.params.CommentParam;

public interface CommentsService {
    Result commentsByArticleId(Long articleId);

    Result comment(CommentParam commentParam);
}
