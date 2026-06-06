package com.turtlesoup.controller;

import com.turtlesoup.common.Result;
import com.turtlesoup.service.UserService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public Result<Map<String, Object>> register(@RequestBody RegisterRequest request) {
        UserService.AuthResult result = userService.register(
                request.username(), request.password(), request.nickname());

        return Result.ok(Map.of(
                "userId", result.userId(),
                "username", result.username(),
                "nickname", result.nickname(),
                "token", result.token()
        ));
    }

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody LoginRequest request) {
        UserService.AuthResult result = userService.login(
                request.username(), request.password());

        return Result.ok(Map.of(
                "userId", result.userId(),
                "username", result.username(),
                "nickname", result.nickname(),
                "token", result.token()
        ));
    }

    public record RegisterRequest(
            @NotBlank(message = "用户名不能为空")
            String username,
            @NotBlank(message = "密码不能为空")
            @Size(min = 6, message = "密码长度至少为6位")
            String password,
            String nickname) {}

    public record LoginRequest(
            @NotBlank(message = "用户名不能为空")
            String username,
            @NotBlank(message = "密码不能为空")
            String password) {}
}
