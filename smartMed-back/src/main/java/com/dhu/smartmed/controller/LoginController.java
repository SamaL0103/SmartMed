package com.dhu.smartmed.controller;

import com.dhu.smartmed.dto.RespResult;
import com.dhu.smartmed.entity.User;
import com.dhu.smartmed.enums.Gender;
import com.dhu.smartmed.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/login")
public class LoginController {

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private UserService userService;

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public RespResult login(
            @RequestParam("userAccount") String username,
            @RequestParam("userPwd") String password,
            HttpSession session) {
        
        logger.info("登录请求接收: userAccount={}, userPwd={}", username, "******");
        
        try {
            if (userService.login(username, password)) {
                User user = userService.getUserByName(username);
                logger.info("登录成功: username={}, userId={}", username, user.getUserId());
                // 将用户信息存入session
                session.setAttribute("loginUser", user);
                // 直接返回用户信息，避免前端再次请求
                return RespResult.success("登录成功", user);
            } else {
                logger.warn("登录失败: 用户名或密码错误, username={}", username);
                return RespResult.fail("用户名或密码错误");
            }
        } catch (Exception e) {
            logger.error("登录过程发生异常: {}", e.getMessage(), e);
            return RespResult.fail("登录过程发生错误: " + e.getMessage());
        }
    }

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public RespResult register(
            @RequestParam("userName") String name,
            @RequestParam("userPwd") String password,
            @RequestParam("userTel") String phone,
            @RequestParam("userAge") Integer age,
            @RequestParam("userSex") String genderStr) {

        // 检查昵称是否已存在
        if (userService.getUserByName(name) != null) {
            return RespResult.fail("昵称已存在");
        }
        Gender gender = "男".equals(genderStr) ? Gender.Male : Gender.Female;
        User user = User.builder()
                .username(name)
                .password(password)
                .phone(phone)
                .age(age)
                .gender(gender)
                .isAdmin(false)
                .build();
        if (userService.register(user)) {
            return RespResult.success("注册成功");
        } else {
            return RespResult.fail("注册失败");
        }
    }

    /**
     * 用户登出
     */
    @PostMapping("/logout")
    public RespResult logout(HttpSession session) {
        session.invalidate(); // 清除所有session信息
        return RespResult.success("已退出登录");
    }
}