package com.classpi.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.classpi.common.Result;
import com.classpi.dto.LoginDTO;
import com.classpi.dto.RegisterDTO;
import com.classpi.entity.User;

public interface UserService extends IService<User> {
    Result login(LoginDTO loginDTO);
    Result register(RegisterDTO registerDTO);
    Result getUserInfo(String token);
    Result resetPassword(String account, String newPassword);

    // ✅ 新增
    Result sendResetCode(String account);
}