package com.classpi.service;

import com.classpi.common.Result;
import com.classpi.dto.LoginDTO;

public interface UserService {
    Result login(LoginDTO loginDTO);
    Result register(LoginDTO registerDTO);
    Result getUserInfo(String token);
}