package com.classpi.controller;

import com.classpi.common.Result;
import com.classpi.dto.LoginDTO;
import com.classpi.dto.RegisterDTO; // 新增导入
import com.classpi.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@CrossOrigin
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public Result login(@RequestBody LoginDTO loginDTO) {
        return userService.login(loginDTO);
    }

    // 注册接口接收RegisterDTO
    @PostMapping("/register")
    public Result register(@RequestBody RegisterDTO registerDTO) {
        return userService.register(registerDTO);
    }

    @GetMapping("/info")
    public Result getUserInfo(@RequestHeader("Authorization") String token) {
        return userService.getUserInfo(token);
    }
}