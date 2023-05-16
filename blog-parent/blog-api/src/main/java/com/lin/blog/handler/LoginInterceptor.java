package com.lin.blog.handler;

import com.alibaba.fastjson.JSON;
import com.lin.blog.pojo.SysUser;
import com.lin.blog.service.LoginService;
import com.lin.blog.utils.UserThreadLocal;
import com.lin.blog.vo.ErrorCode;
import com.lin.blog.vo.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
//spring mvc内容 拦截器

//在config/configWebMVCConfig下添加 哪些接口需要拦截
//拦截之后进入handler/LoginInterceptor类
@Component
@Slf4j
public class LoginInterceptor implements HandlerInterceptor {

    @Autowired
    private LoginService loginService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //在执行controller方法(Handler)之前进行执行
        /**
         * 1. 需要判断 请求的接口路径 是否为 HandlerMethod (controller方法)
         * 2. 判断 token是否为空，如果为空 未登录
         * 3. 如果token不为空，就把token和redis里面token进行对比
         * 4. 如果认证成功，把当前用户的信息放入ThreadLocal，放行
         */
        //如果不是我们的 controller方法进行,放行
        if (!(handler instanceof HandlerMethod)){
            //handler 可能是 RequestResourceHandler springboot 程序 访问静态资源 默认去classpath下的static目录去查询
            return true;
        }
        //如果是登录过的，token会放在浏览器storage中，每次请求都会携带token值
        String token = request.getHeader("Authorization");

        log.info("=================request start===========================");
        String requestURI = request.getRequestURI();
        log.info("request uri:{}",requestURI);
        log.info("request method:{}",request.getMethod());
        log.info("token:{}", token);
        log.info("=================request end===========================");


        //没有token，也就是没有登录状态
        if(StringUtils.isBlank(token)){
            Result result = Result.fail(ErrorCode.NO_LOGIN.getCode(), "未登录");
            //设置浏览器识别返回的是json
            response.setContentType("application/json;charset=utf-8");
            //https://www.cnblogs.com/qlqwjy/p/7455706.html response.getWriter().print()
            //JSON.toJSONString则是将对象转化为Json字符串
            response.getWriter().print(JSON.toJSONString(result));
            return false;
        }
        //目前传进来的有token，还要判断一下正不正确,根据token得到用户信息
        //吧token作为key 去查找redis里面 的value(用户)，如果有value(用户)说明是正常登录之后的token
        SysUser sysUser = loginService.checkToken(token);
        if (sysUser == null){
            Result result = Result.fail(ErrorCode.NO_LOGIN.getCode(), "未登录");
            response.setContentType("application/json;charset=utf-8");
            response.getWriter().print(JSON.toJSONString(result));
            return false;
        }
        //是登录状态，放行
        //登录验证成功，放行

        //我希望在controller中 直接获取用户的信息 怎么获取?
        // 使用ThreadLocal，就不用从redis中获取，不过redis获取也没什么不好，感觉是为了使用新技术而用ThreadLocal。
        //redis中可以获取用户信息，但是因为redis中的key是token，要先知道token才能拿到用户信息，但是token不是每一个类中都存在
        UserThreadLocal.put(sysUser);

        return true;
    }


    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        //如果不删除 ThreadLocal中用完的信息 会有内存泄漏的风险
        UserThreadLocal.remove();
    }
}
