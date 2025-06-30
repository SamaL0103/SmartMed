package com.dhu.smartmed.controller;

import com.dhu.smartmed.dto.RespResult;
import com.dhu.smartmed.entity.User;
import com.dhu.smartmed.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;
import java.util.List;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    // 管理员：获取所有用户（分页可选）
    @GetMapping("/list")
    public RespResult listUsers(HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null || !userService.isAdmin(loginUser.getUserId())) {
            return RespResult.fail("无权限");
        }
        List<User> users = userService.findAll();
        return RespResult.success("获取成功", users);
    }

    // 管理员：根据ID获取用户
    @GetMapping("/get")
    public RespResult getUserById(@RequestParam("userId") Integer userId, HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null || !userService.isAdmin(loginUser.getUserId())) {
            return RespResult.fail("无权限");
        }
        User user = userService.getUserById(userId);
        if (user == null) {
            return RespResult.fail("用户不存在");
        }
        return RespResult.success("获取成功", user);
    }

    // 管理员：新增用户
    @PostMapping("/add")
    public RespResult addUser(@RequestBody User user, HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null || !userService.isAdmin(loginUser.getUserId())) {
            return RespResult.fail("无权限");
        }
        if (userService.addUser(user)) {
            return RespResult.success("添加成功");
        } else {
            return RespResult.fail("添加失败");
        }
    }

    // 管理员：更新用户
    @PostMapping("/update")
    public RespResult updateUser(@RequestBody User user, HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null || !userService.isAdmin(loginUser.getUserId())) {
            return RespResult.fail("无权限");
        }
        if (userService.updateUser(user)) {
            return RespResult.success("更新成功");
        } else {
            return RespResult.fail("更新失败");
        }
    }

    // 管理员：删除用户
    @PostMapping("/delete")
    public RespResult deleteUser(@RequestParam("userId") Integer userId, HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null || !userService.isAdmin(loginUser.getUserId())) {
            return RespResult.fail("无权限");
        }
        if (userService.deleteUser(userId)) {
            return RespResult.success("删除成功");
        } else {
            return RespResult.fail("删除失败");
        }
    }
} 