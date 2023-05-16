package com.lin.blog.admin.service;

import com.lin.blog.admin.pojo.Admin;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
@Slf4j
public class SecurityUserService implements UserDetailsService {
    /**
     * 登陆的时候，会把username传递到这里
     * 通过username查询admin表， 如果admin存在，将密码告诉spring security
     * 如果不存在 返回null 认证失败
     * @param s
     * @return
     * @throws UsernameNotFoundException
     */

    @Autowired
    private AdminService adminService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("username:{}",username);
        Admin adminUser = adminService.findAdminByUsername(username);
        if (adminUser == null){
            //throw new UsernameNotFoundException("用户名不存在");
            return  null;
        }
        ArrayList<GrantedAuthority> authorities = new ArrayList<>();
        UserDetails userDetails = new User(username,adminUser.getPassword(), authorities);
        return userDetails;
    }
}
