package com.lin.blog.admin.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lin.blog.admin.mapper.AdminMapper;
import com.lin.blog.admin.mapper.PermissionMapper;
import com.lin.blog.admin.pojo.Admin;
import com.lin.blog.admin.pojo.Permission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminService {
    @Autowired
    private AdminMapper adminMapper;
    @Autowired
    private PermissionMapper permissionMapper;

    public Admin findAdminByUsername(String username) {
        LambdaQueryWrapper<Admin> lambdaQueryWrapper = new LambdaQueryWrapper();
        lambdaQueryWrapper.eq(Admin::getUsername,username);
        Admin admin = adminMapper.selectOne(lambdaQueryWrapper);
        return admin;
    }

    public List<Permission> findPermissionsByAdminId(Long id) {
        return adminMapper.findPermissionsByAdminId(id);
    }
}
