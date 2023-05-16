package com.lin.blog.Controller;

import com.lin.blog.common.aop.LogAnnotation;
import com.lin.blog.common.cache.Cache;
import com.lin.blog.service.ArticleService;
import com.lin.blog.vo.ArticleParam;
import com.lin.blog.vo.Result;
import com.lin.blog.vo.params.PageParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

//json 数据进行交互
@RestController
@RequestMapping("articles")
public class ArticleController {

    @Autowired
    private ArticleService articleService;
    /**
     * 首页 文章列表
     * @param pageParams
     * @return
     */
    @PostMapping
    //加上此注解，代表要对此接口记录日志
    @LogAnnotation(module = "文章",operation = "获取文章列表")
    @Cache(expire = 1*60*1000,name = "list_article")
    public Result listArticle(@RequestBody PageParams pageParams){
        Result result = articleService.listArticle(pageParams);
        return result;
    }

    /**
     * 首页 最热文章
     * @return
     */
    @PostMapping("/hot")
    @Cache(expire = 1*60*1000,name = "hot_article")
    public Result hotArticle(){
        int limit = 5;
        return articleService.hotArticle(limit);
    }

    /**
     * 最新文章
     * @return
     */
    @PostMapping("/new")
    @Cache(expire = 1*60*1000,name = "new_article")
    public Result newArticle(){
        int limit = 5;
        return articleService.newArticle(limit);
    }

    /**
     * 首页 文章归档
     * @return
     */
    @PostMapping("/listArchives")
    public Result listArchives(){
        return articleService.listArchives();
    }

    /**
     * 文章详情
     * @param articleId
     * @return
     */
    @PostMapping("view/{id}")
    public Result findArticleById(@PathVariable("id") Long articleId){
        return articleService.findArticleById(articleId);
    }

    /**
     * 发布文章
     * @param articleParam
     * @return
     */
    @PostMapping("publish")
    public Result publish(@RequestBody ArticleParam articleParam){
        return articleService.publish(articleParam);
    }

    /**
     * 编辑
     * @param articleId
     * @return
     */
    @PostMapping("{id}")
    public Result articleById(@PathVariable("id") Long articleId){
        return articleService.findArticleById(articleId);
    }
}
