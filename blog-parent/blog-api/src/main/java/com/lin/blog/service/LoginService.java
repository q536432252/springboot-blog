package com.lin.blog.service;

import com.lin.blog.pojo.SysUser;
import com.lin.blog.vo.Result;
import com.lin.blog.vo.params.LoginParam;

public interface LoginService {
    /**
     * 登录功能
     * @param loginParam
     * @return
     */
    Result login(LoginParam loginParam);

    SysUser checkToken(String token);

    Result logout(String token);

    Result register(LoginParam loginParam);
}
