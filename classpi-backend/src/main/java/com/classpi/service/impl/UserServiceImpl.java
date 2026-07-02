package com.classpi.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.classpi.common.Result;
import com.classpi.dto.LoginDTO;
import com.classpi.dto.RegisterDTO;
import com.classpi.entity.User;
import com.classpi.mapper.UserMapper;
import com.classpi.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.nio.charset.StandardCharsets;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService  {

    @Autowired
    private UserMapper userMapper;

    // ========== 抽离Java原生MD5加密工具方法 ==========
    private String encryptWithMD5(String password) {
        try {
            // 1. 获取MD5加密实例
            MessageDigest md = MessageDigest.getInstance("MD5");
            // 2. 转换为字节数组并加密
            byte[] digest = md.digest(password.getBytes(StandardCharsets.UTF_8));
            // 3. 转16进制字符串（和Spring DigestUtils结果一致）
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b)); // 补零，避免位数不对
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5加密算法不存在", e); // 异常抛出，避免静默失败
        }
    }

    // ========== 登录逻辑（改用原生加密） ==========
    @Override
    public Result login(LoginDTO loginDTO) {
        // 1. 校验入参（补充：你原代码漏了入参非空校验）
        if (loginDTO.getUsername() == null || loginDTO.getUsername().isEmpty()
                || loginDTO.getPassword() == null || loginDTO.getPassword().isEmpty()) {
            return Result.error("用户名/密码不能为空");
        }

        // 2. 查询用户
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, loginDTO.getUsername())
                .eq(User::getDeleted, 0);
        User user = userMapper.selectOne(queryWrapper);

        // 3. 校验用户是否存在
        if (user == null) {
            return Result.error("账号或密码错误"); // 统一提示，避免枚举用户名
        }

        // 4. 原生MD5加密前端明文，和数据库密文对比
        String inputMd5 = encryptWithMD5(loginDTO.getPassword());
        if (!inputMd5.equals(user.getPassword())) {
            return Result.error("账号或密码错误");
        }

        // 5. 清空密码，返回用户信息
        user.setPassword(null);
        return Result.success("登录成功", user);
    }

    // ========== 注册逻辑（改用原生加密） ==========
    @Override
    public Result register(RegisterDTO registerDTO) {
        // 1. 入参非空校验（补充：你原代码漏了）
        if (registerDTO.getUsername() == null || registerDTO.getUsername().isEmpty()
                || registerDTO.getPassword() == null || registerDTO.getPassword().isEmpty()) {
            return Result.error("用户名/密码不能为空");
        }

        // 2. 检查用户名是否已存在
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, registerDTO.getUsername())
                .eq(User::getDeleted, 0);
        User existUser = userMapper.selectOne(queryWrapper);
        if (existUser != null) {
            return Result.error("用户名已存在");
        }

        // 3. 构建用户对象（原生MD5加密密码）
        User user = new User();
        user.setUsername(registerDTO.getUsername());
        user.setPassword(encryptWithMD5(registerDTO.getPassword())); // 改用原生加密
        user.setRole(2); // 普通用户
        user.setPhone(registerDTO.getPhone());
        user.setName(registerDTO.getName());
        user.setSchool(registerDTO.getSchool());
        user.setStudentId(registerDTO.getStudentId());
        user.setIdentity(registerDTO.getIdentity());

        // 4. 插入数据库
        int insertResult = userMapper.insert(user);
        if (insertResult <= 0) {
            return Result.error("注册失败：数据库插入失败");
        }

        user.setPassword(null); // 清空密码返回
        return Result.success("注册成功", user);
    }

    // ========== 获取用户信息（逻辑无加密，仅补注释） ==========
    @Override
    public Result getUserInfo(String token) {
        if (token == null || token.isEmpty()) {
            return Result.error("token不能为空");
        }
        try {
            Integer userId = Integer.parseInt(token);
            User user = userMapper.selectById(userId);
            if (user == null || user.getDeleted() == 1) {
                return Result.error("用户不存在");
            }
            user.setPassword(null); // 避免密码泄露
            return Result.success("获取用户信息成功", user);
        } catch (NumberFormatException e) {
            return Result.error("token格式错误");
        }
    }

    @Override
    public Result resetPassword(String account, String newPassword) {
        // 1. 查询用户
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, account)
                .or()
                .eq(User::getPhone, account)
                .eq(User::getDeleted, 0);
        User user = userMapper.selectOne(wrapper);

        if (user == null) {
            return Result.error("账号不存在");
        }

        // 2. 更新密码（加密）
        user.setPassword(encryptWithMD5(newPassword));
        userMapper.updateById(user);

        return Result.success("密码重置成功");
    }

    @Override
    public Result sendResetCode(String account) {
        // 查询用户是否存在（支持用户名或手机号）
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, account)
                .or()
                .eq(User::getPhone, account)
                .eq(User::getDeleted, 0);
        User user = userMapper.selectOne(wrapper);

        if (user == null) {
            return Result.error("账号不存在");
        }

        // 验证通过，返回成功
        return Result.success("验证通过，请设置新密码");
    }
}