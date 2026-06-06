package com.turtlesoup.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.turtlesoup.common.JwtUtils;
import com.turtlesoup.entity.User;
import com.turtlesoup.mapper.UserMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserMapper userMapper;
    private final JwtUtils jwtUtils;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UserService(UserMapper userMapper, JwtUtils jwtUtils) {
        this.userMapper = userMapper;
        this.jwtUtils = jwtUtils;
    }

    public record AuthResult(Long userId, String username, String nickname, String token) {}

    public AuthResult register(String username, String password, String nickname) {
        // 检查用户名是否已存在
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username);
        if (userMapper.selectCount(wrapper) > 0) {
            throw new RuntimeException("用户名已存在");
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setNickname(nickname != null ? nickname : username);
        userMapper.insert(user);

        String token = jwtUtils.generateToken(user.getId(), user.getUsername());
        return new AuthResult(user.getId(), user.getUsername(), user.getNickname(), token);
    }

    public AuthResult login(String username, String password) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username);
        User user = userMapper.selectOne(wrapper);

        if (user == null || !passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("用户名或密码错误");
        }

        String token = jwtUtils.generateToken(user.getId(), user.getUsername());
        return new AuthResult(user.getId(), user.getUsername(), user.getNickname(), token);
    }

    public User getById(Long id) {
        return userMapper.selectById(id);
    }

    public void updateProfile(Long id, String nickname) {
        User user = new User();
        user.setId(id);
        user.setNickname(nickname);
        userMapper.updateById(user);
    }
}
