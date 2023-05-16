package com.lin.blog.service.impl;

import com.alibaba.fastjson.JSON;
import com.lin.blog.pojo.SysUser;
import com.lin.blog.service.LoginService;
import com.lin.blog.service.SysUserService;
import com.lin.blog.utils.JWTUtils;
import com.lin.blog.vo.ErrorCode;
import com.lin.blog.vo.Result;
import com.lin.blog.vo.params.LoginParam;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@Transactional
public class LoginServiceImpl implements LoginService {

    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    //加密盐用于加密 加密之后的密码哪怕被解密了  不知道加密盐也无法还原原来的密码
    private static final String salt = "mszlu!@#";

    /**
     * 登录功能
     * @param loginParam
     * @return
     */
    @Override
    public Result login(LoginParam loginParam) {
        /**
         * 用户登录
         * 1/检查参数 (账号密码) 是否合法，是否为空
         * 2、查找用户名和密码去user表中查找是否存在
         * 3、不存在，登陆失败
         * 4、存在，使用jwt生成token返回给前端
         * 5/token 放入redis中，  redis   key(token) : value(用户信息) ,设置过期时间1天 和jwt 时间一致
         *
         * 6、
         * 进入到需要登录才能操作的页面时候，
         * 就通过拦截器(config/webMVCConfig)拦截下来
         * 到handler/LoginInterceptor中 判断是否 有token 、token是否伪造（去redis里面查看该token是否有value(用户)），
         * 如果都通过说明是登录成功的  在LoginInterceptor中把用户信息存放在 ThreadLocal局部线程。
         * 就可以在controller中直接获取用户信息
         *
         */
        //！1检查参数 (账号密码) 是否合法，是否为空
        String account = loginParam.getAccount();
        String password = loginParam.getPassword();
        if(StringUtils.isBlank(account) || StringUtils.isBlank(password)) {
            return Result.fail(ErrorCode.PARAMS_ERROR.getCode(),ErrorCode.PARAMS_ERROR.getMsg());
        }
        //！2、查找用户名和密码去user表中查找是否存在
        //密码加密 ： md5 (密码+盐)
        //在数据库中加密存储，因为md5是可以反向推出来，加了一个盐，不知道加密盐也无法还原原来的密码
        password = DigestUtils.md5Hex(password + salt);
        SysUser sysUser = sysUserService.findUser(account,password);
        //！3、不存在，登陆失败
        if (sysUser == null) {
            return Result.fail(ErrorCode.ACCOUNT_PWD_NOT_EXIST.getCode(),ErrorCode.ACCOUNT_PWD_NOT_EXIST.getMsg());
        }
        //！4、查找账号密码 该用户存在，使用jwt生成token返回给前端
        //账号密码存在找到该用户， 使用jwt生成一个token
        String token = JWTUtils.createToken(sysUser.getId());

        //redis存放      key(token) ： value(用户信息)
        redisTemplate.opsForValue().set("TOKEN_" + token, JSON.toJSONString(sysUser), 1, TimeUnit.DAYS);
        return Result.success(token);
    }

    /**
     * 在拦截器(handler/LoginInterceptor)中，使用该方法，查看当前是否有登陆
     * @param token
     * @return
     */
    @Override
    public SysUser checkToken(String token) {
        //token为空返回null
        if(StringUtils.isBlank(token)){
            return null;
        }
        Map<String, Object> stringObjectMap = JWTUtils.checkToken(token);
        //解析失败
        if(stringObjectMap ==null){
            return null;
        }
        //如果成功,去查看redis是否有该token
        String userJson =  redisTemplate.opsForValue().get("TOKEN_"+token);
        if (StringUtils.isBlank(userJson)) {
            return null;
        }
        //解析回sysUser对象
        SysUser sysUser = JSON.parseObject(userJson, SysUser.class);
        return sysUser;
    }

    @Override
    public Result logout(String token) {
        /**
         * 退出登录
         * 把redis对应的token删除
         */
        redisTemplate.delete("TOKEN_" + token);
        return Result.success(null);
    }

    @Override
    public Result register(LoginParam loginParam) {
        /**
         * 1. 判断参数 是否合法
         * 2. 判断账户是否存在，存在 返回账户已经被注册
         * 3. 不存在，注册用户
         * 4. 生成token
         * 5. 存入redis 并返回
         * 6. 注意 加上事务，一旦中间的任何过程出现问题，注册的用户 需要回滚
         */
        String account = loginParam.getAccount();
        String password = loginParam.getPassword();
        String nickname = loginParam.getNickname();
        if (StringUtils.isBlank(account)
                || StringUtils.isBlank(password)
                || StringUtils.isBlank(nickname)
        ){
            return Result.fail(ErrorCode.PARAMS_ERROR.getCode(),ErrorCode.PARAMS_ERROR.getMsg());
        }
        SysUser sysUser = sysUserService.findUserByAccount(account);
        if (sysUser != null){
            return Result.fail(ErrorCode.ACCOUNT_EXIST.getCode(),ErrorCode.ACCOUNT_EXIST.getMsg());
        }

        sysUser = new SysUser();
        sysUser.setNickname(nickname);
        sysUser.setAccount(account);
        sysUser.setPassword(DigestUtils.md5Hex(password+salt));
        sysUser.setCreateDate(System.currentTimeMillis());
        sysUser.setLastLogin(System.currentTimeMillis());
        sysUser.setAvatar("/static/img/logo.b3a48c0.png");
        sysUser.setAdmin(1); //1 为true
        sysUser.setDeleted(0); // 0 为false
        sysUser.setSalt("");
        sysUser.setStatus("");
        sysUser.setEmail("");
        sysUserService.save(sysUser);
        //token
        String token = JWTUtils.createToken(sysUser.getId());

        redisTemplate.opsForValue().set("TOKEN_"+token, JSON.toJSONString(sysUser),1, TimeUnit.DAYS);
        return Result.success(token);

    }
}
