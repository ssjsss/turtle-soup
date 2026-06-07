package com.turtlesoup.controller;

import com.turtlesoup.common.Result;
import com.turtlesoup.entity.User;
import com.turtlesoup.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/profile")
    public Result<Map<String, Object>> profile(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        User user = userService.getById(userId);

        return Result.ok(Map.of(
                "id", user.getId(),
                "username", user.getUsername(),
                "nickname", user.getNickname(),
                "createTime", user.getCreateTime().toString()
        ));
    }

    @PutMapping("/profile")
    public Result<Void> updateProfile(@RequestBody UpdateProfileRequest req,
                                       HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        userService.updateProfile(userId, req.nickname());
        return Result.ok();
    }

    public record UpdateProfileRequest(String nickname) {}
}
