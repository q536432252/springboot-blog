package com.lin.blog.common.cache;

import java.lang.annotation.*;
//https://www.jb51.net/article/161279.htm


//自定义一个注解 @Cache
// 声明可以使用该注解的使用范围  METHOD：方法   ；  TYPE：类，接口、枚举
@Target({ElementType.METHOD})
// 声明当前注解的生命周期，说人话就是这个注解作用会保留到什么时候
// RUNTIME : 在程序运行期间依然有效，此时就可以通过反射拿到注解的信息
@Retention(RetentionPolicy.RUNTIME)
//当被此注解所注解时，使用javadoc工具生成文档就会带有注解信息。
@Documented
//@Inherited：说明子类可以继承父类中的该注解
public @interface Cache {
    //缓存有效期时间  1分钟
    long expire() default 1 * 60 * 1000;
    //缓存标识 key的名字
    String name() default "";
}
