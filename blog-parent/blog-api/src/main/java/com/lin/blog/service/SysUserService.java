package com.lin.blog.service;

import com.lin.blog.pojo.SysUser;
import com.lin.blog.vo.Result;
import com.lin.blog.vo.UserVo;

public interface SysUserService {
    SysUser findUserByID(Long id);

    SysUser findUser(String account, String password);

    Result findUserByToken(String token);

    SysUser findUserByAccount(String account);

    void save(SysUser sysUser);

    UserVo findUserVoById(Long authorId);
}
