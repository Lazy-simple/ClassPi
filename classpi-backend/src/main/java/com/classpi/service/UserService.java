package com.classpi.service;

import com.classpi.common.Result;
import com.classpi.dto.LoginDTO;
import com.classpi.dto.RegisterDTO; // 新增导入

public interface UserService {
    Result login(LoginDTO loginDTO);
    Result register(RegisterDTO registerDTO); // 替换原LoginDTO为RegisterDTO
    Result getUserInfo(String token);
}