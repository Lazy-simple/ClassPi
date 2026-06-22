package com.classpi.service.impl;

import com.classpi.common.Result;
import com.classpi.dto.LoginDTO;
import com.classpi.entity.User;
import com.classpi.mapper.UserMapper;
import com.classpi.service.UserService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public Result login(LoginDTO loginDTO) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", loginDTO.getUsername());
        User user = userMapper.selectOne(queryWrapper);
        
        if (user == null) {
            return Result.error("用户不存在");
        }
        
        if (!user.getPassword().equals(loginDTO.getPassword())) {
            return Result.error("密码错误");
        }
        
        return Result.success("登录成功", user);
    }

    @Override
    public Result register(LoginDTO registerDTO) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", registerDTO.getUsername());
        User existUser = userMapper.selectOne(queryWrapper);
        
        if (existUser != null) {
            return Result.error("用户名已存在");
        }
        
        User user = new User();
        user.setUsername(registerDTO.getUsername());
        user.setPassword(registerDTO.getPassword());
        user.setRole(2);
        userMapper.insert(user);
        
        return Result.success("注册成功", user);
    }

    @Override
    public Result getUserInfo(String token) {
        return Result.success("获取用户信息成功", null);
    }
}