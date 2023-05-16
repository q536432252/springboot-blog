package com.lin.blog.config;

import com.lin.blog.handler.LoginInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMVCConfig implements WebMvcConfigurer {
    @Autowired
    private LoginInterceptor loginInterceptor;

    /**
     * 跨域问题解决
     * 1. 全局配置：继承接口，重写addCorsMappings方法
     * 2. 使用nginx反向代理服务器解决跨域问题（如果前后端分离，建议此种方案）
     * @param registry
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        //跨域配置，不可设置为*，不安全,
        //前后端分离项目，可能域名不一致
        //本博客项目前段时间8080 后端是8888
        //本地测试 端口不一致 也算跨域   域名=ip地址+端口
        //要允许8080端口访问8888端口

                // 设置允许跨域的路径
        registry.addMapping("/**")
                // 设置允许跨域请求的域名
                .allowedOrigins("http://localhost:8080");
    }

    /**
     * 拦截需要登录的页面
     * springMVC 先拦截 再 执行跨域
     * 碰到需要登录的地方才进行拦截，拦截之后进入到 handler/LoginInterceptor
     * //在config/configWebMVCConfig下添加 哪些接口需要拦截
     * //拦截之后进入handler/LoginInterceptor类
     *
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //假设拦截test接口后续实际遇到拦截的接口是时，再配置真正的拦截接口
        //.addPathPatterns("/comments/create/change") 评论的时候要登录
        registry.addInterceptor(loginInterceptor)
                .addPathPatterns("/test")
                .addPathPatterns("/comments/create/change")
                .addPathPatterns("/articles/publish");
    }
}
