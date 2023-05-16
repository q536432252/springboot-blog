package com.lin.blog.common.aop;

import java.lang.annotation.*;

/**
 * 自定义 @LogAnnotation 日志注解
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LogAnnotation {
    String module() default ""; //哪一个模块
    String operation() default ""; //做了什么操作
}
