package com.lin.blog.service.mq;

import com.alibaba.fastjson.JSON;
import com.lin.blog.service.ArticleService;
import com.lin.blog.vo.ArticleMessage;
import com.lin.blog.vo.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Set;


// https://blog.csdn.net/u011535541/article/details/116165642
// https://www.csdn.net/tags/NtTakg1sMjgxNjgtYmxvZwO0O0OO0O0O.html
//https://blog.51cto.com/u_15310381/3201790
//https://www.jianshu.com/p/8d890bdf0ab6

@Slf4j
@Component
@RocketMQMessageListener(topic = "blog-update-article",consumerGroup = "blog-update-article-group")
//实现了RocketMQListener接口的自定义监听器就可以消费消息了. 为什么呢？
public class ArticleListener implements RocketMQListener<ArticleMessage> {
    @Autowired
    private ArticleService articleService;
    @Autowired
    private StringRedisTemplate redisTemplate;

    //当收到消息的时候，会自动调用onMessage方法
    @Override
    public void onMessage(ArticleMessage message) {
        log.info("收到的消息:{}",message);
        //做什么了，更新缓存
        //1. 编辑之后更新查看文章详情的缓存
        Long articleId = message.getArticleId();
        String params = DigestUtils.md5Hex(articleId.toString());
        //获取到原来的key
        String redisKey = "view_article::ArticleController::findArticleById::"+params;
        Result articleResult = articleService.findArticleById(articleId);
        //更改原来的缓存value
        redisTemplate.opsForValue().set(redisKey, JSON.toJSONString(articleResult), Duration.ofMillis(5 * 60 * 1000));
        log.info("更新了缓存:{}",redisKey);

        //2. 文章列表的缓存 不知道参数,解决办法 直接删除缓存
        Set<String> keys = redisTemplate.keys("list_article*");
        keys.forEach(s -> {
            redisTemplate.delete(s);
            log.info("删除了文章列表的缓存:{}",s);
        });
    }
}
