package com.classpi.controller;

import com.classpi.common.Result;
import com.classpi.dto.LoginDTO;
import com.classpi.dto.RegisterDTO;
import com.classpi.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

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

    @PostMapping("/register")
    public Result register(@RequestBody RegisterDTO registerDTO) {
        return userService.register(registerDTO);
    }

    @GetMapping("/info")
    public Result getUserInfo(@RequestHeader("Authorization") String token) {
        return userService.getUserInfo(token);
    }

    // ✅ 新增：验证账号
    @PostMapping("/sendResetCode")
    public Result sendResetCode(@RequestBody Map<String, String> params) {
        String account = params.get("account");
        if (account == null || account.isEmpty()) {
            return Result.error("账号不能为空");
        }
        return userService.sendResetCode(account);
    }

    // 重置密码
    @PostMapping("/resetPassword")
    public Result resetPassword(@RequestBody Map<String, String> params) {
        String account = params.get("account");
        String newPassword = params.get("newPassword");
        if (account == null || account.isEmpty()) {
            return Result.error("账号不能为空");
        }
        if (newPassword == null || newPassword.isEmpty()) {
            return Result.error("新密码不能为空");
        }
        if (newPassword.length() < 6) {
            return Result.error("密码长度不能少于6位");
        }
        return userService.resetPassword(account, newPassword);
    }
}