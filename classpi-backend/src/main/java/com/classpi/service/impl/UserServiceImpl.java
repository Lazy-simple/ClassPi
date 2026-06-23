package com.classpi.service.impl;

import com.classpi.common.Result;
import com.classpi.dto.LoginDTO;
import com.classpi.dto.RegisterDTO; // 新增导入
import com.classpi.entity.User;
import com.classpi.mapper.UserMapper;
import com.classpi.service.UserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils; // 密码加密用

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    // 登录逻辑（保留，若需要短信登录可后续扩展）
    @Override
    public Result login(LoginDTO loginDTO) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, loginDTO.getUsername())
                .eq(User::getDeleted, 0);

        User user = userMapper.selectOne(queryWrapper);
        if (user == null) {
            return Result.error("用户不存在");
        }

        // 密码加密比对
        String encryptedPassword = DigestUtils.md5DigestAsHex(loginDTO.getPassword().getBytes());
        if (!user.getPassword().equals(encryptedPassword)) {
            return Result.error("密码错误");
        }

        // 返回用户信息（密码不要返回）
        user.setPassword(null); // 清空密码再返回
        return Result.success("登录成功", user);
    }

    // 注册逻辑（适配新的RegisterDTO）
    @Override
    public Result register(RegisterDTO registerDTO) {
        // 1. 检查用户名/手机号是否已存在
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, registerDTO.getUsername())
                .eq(User::getDeleted, 0);
        User existUser = userMapper.selectOne(queryWrapper);
        if (existUser != null) {
            return Result.error("用户名已存在");
        }

        // 2. 构建用户对象（填充所有注册字段）
        User user = new User();
        user.setUsername(registerDTO.getUsername());
        // 建议密码加密（MD5示例，生产环境用BCrypt）
        user.setPassword(DigestUtils.md5DigestAsHex(registerDTO.getPassword().getBytes()));
        user.setRole(2); // 普通用户
        user.setPhone(registerDTO.getPhone()); // 新增手机号
        user.setName(registerDTO.getName()); // 新增姓名
        user.setSchool(registerDTO.getSchool()); // 新增学校
        user.setStudentId(registerDTO.getStudentId()); // 新增学号
        user.setIdentity(registerDTO.getIdentity()); // 新增身份（老师/学生）

        // 3. 插入数据库
        int insertResult = userMapper.insert(user);
        if (insertResult <= 0) {
            return Result.error("注册失败：数据库插入失败");
        }
        return Result.success("注册成功", user);
    }

    @Override
    public Result getUserInfo(String token) {
        // 保持原有逻辑，若需要扩展用户信息，可返回新增的phone/name等字段
        if (token == null || token.isEmpty()) {
            return Result.error("token不能为空");
        }
        try {
            Integer userId = Integer.parseInt(token);
            User user = userMapper.selectById(userId);
            if (user == null || user.getDeleted() == 1) {
                return Result.error("用户不存在");
            }
            return Result.success("获取用户信息成功", user);
        } catch (NumberFormatException e) {
            return Result.error("token格式错误");
        }
    }
}