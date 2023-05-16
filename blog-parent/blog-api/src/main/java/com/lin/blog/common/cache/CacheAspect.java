package com.lin.blog.common.cache;

import com.alibaba.fastjson.JSON;
import com.lin.blog.vo.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AliasFor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.Duration;

//aop 定义了一个切面 ，切面定义了切点和通知的关系
//切点为 ：  标注了 @annotation（Cache自定义注解） 的方法

@Aspect //切面 定义了通知和切点的关系
@Component
@Slf4j
public class CacheAspect {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    //切点，注解加在哪里 哪里就是切点
    @Pointcut("@annotation(com.lin.blog.common.cache.Cache)")
    public void pt(){}

    //Around：环绕通知
    @Around("pt()")
    public Object around(ProceedingJoinPoint pjp){
        try {
            Signature signature = pjp.getSignature();
            //类名
            String className = pjp.getTarget().getClass().getSimpleName();
            //调用的方法名
            String methodName = signature.getName();

            //参数
            Class[] parameterTypes = new Class[pjp.getArgs().length];
            Object[] args = pjp.getArgs();
            //参数字符串变量
            String params = "";
            //判断参数是否有值
            for(int i=0; i<args.length; i++) {
                if(args[i] != null) {
                    //将参数转变成json，有可能参数是类
                    params += JSON.toJSONString(args[i]);
                    parameterTypes[i] = args[i].getClass();
                }else {
                    parameterTypes[i] = null;
                }
            }
            if (StringUtils.isNotEmpty(params)) {
                //对参数 进行md5加密 以防出现key过长以及字符转义获取不到的情况
                params = DigestUtils.md5Hex(params);
            }
            //拿到 运行的方法
            Method method = pjp.getSignature().getDeclaringType().getMethod(methodName, parameterTypes);
            //根据运行的方法 获取Cache注解
            Cache annotation = method.getAnnotation(Cache.class);
            //缓存过期时间 注解里面的expire参数
            long expire = annotation.expire();
            //缓存名称  注解里面的name参数
            String name = annotation.name();
            //key的组成  注解里面的name参数 :: 类名 :: 方法名 :: 参数转json再md5加密形式
            String redisKey = name + "::" + className+"::"+methodName+"::"+params;
            //先从redis查看是否有该key
            String redisValue = redisTemplate.opsForValue().get(redisKey);
            //如果redis里面有，取缓存
            if (StringUtils.isNotEmpty(redisValue)){
                log.info("走了缓存~~~,{},{}",className,methodName);
                Result result = JSON.parseObject(redisValue, Result.class);
                return result;
            }

            //如果redis里面没有，调用切点方法================
            //环绕通知 手动执行方法
            Object proceed = pjp.proceed();
            //设置缓存
            redisTemplate.opsForValue().set(redisKey,JSON.toJSONString(proceed), Duration.ofMillis(expire));
            log.info("存入缓存~~~ {},{}",className,methodName);
            return proceed;
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        //错误情况
        return Result.fail(-999,"系统错误");
    }

}
