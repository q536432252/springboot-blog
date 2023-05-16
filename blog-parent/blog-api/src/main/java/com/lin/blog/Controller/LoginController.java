package com.lin.blog.Controller;

import com.lin.blog.service.LoginService;
import com.lin.blog.vo.Result;
import com.lin.blog.vo.params.LoginParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/login")
public class LoginController {

    @Autowired
    private LoginService loginService;

    //@RequestBody,@ResponseBody的用法 和理解 https://blog.csdn.net/zhanglf02/article/details/78470219
    //浅谈@RequestMapping @ResponseBody 和 @RequestBody 注解的用法与区别
    //https://blog.csdn.net/ff906317011/article/details/78552426?utm_medium=distribute.pc_relevant.none-task-blog-2%7Edefault%7ECTRLIST%7Edefault-2.no_search_link&depth_1-utm_source=distribute.pc_relevant.none-task-blog-2%7Edefault%7ECTRLIST%7Edefault-2.no_search_link

    //@RequestBody主要用来接收前端传递给后端的 json~~~json！！！ 字符串中的数据的(请求体中的数据的)；而最常用的使用请求体传参的无疑是POST请求了，所以使用@RequestBody接收数据时，一般都用POST方式进行提交。

    //get接收参数
    //实体类接收
    //@RequestParam 用于接收一个个参数
    //@PathVariable 用于接收url参数

    //post接收参数
    //@RequestBody

    @PostMapping
    public Result login(@RequestBody LoginParam loginParam) {
        //登陆 验证用户 访问用户表
        return loginService.login(loginParam);
    }
}
