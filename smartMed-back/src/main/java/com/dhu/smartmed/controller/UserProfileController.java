package com.dhu.smartmed.controller;

import com.dhu.smartmed.dto.RespResult;
import com.dhu.smartmed.entity.User;
import com.dhu.smartmed.enums.Gender;
import com.dhu.smartmed.service.UserProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/userProfile")
public class UserProfileController {

    @Autowired
    private UserProfileService userProfileService;

    @Autowired
    private FileController fileController;

    /**
     * 获取当前登录用户个人资料
     */
    @GetMapping("/info")
    public RespResult getUserProfile(HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            return RespResult.fail("未登录");
        }
        User user = userProfileService.getUserProfile(loginUser.getUserId());
        if (user == null) {
            return RespResult.fail("用户不存在");
        }
        return RespResult.success("获取成功", user);
    }

    /**
     * 修改当前登录用户个人资料
     */
    @PostMapping("/update")
    public RespResult updateUserProfile(
            @RequestParam(value = "username", required = false) String username,
            @RequestParam(value = "phone", required = false) String phone,
            @RequestParam(value = "age", required = false) Integer age,
            @RequestParam(value = "gender", required = false) String gender,
            @RequestParam(value = "avatar", required = false) String avatar,
            HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            return RespResult.fail("未登录");
        }
        User existingUser = userProfileService.getUserProfile(loginUser.getUserId());
        if (existingUser == null) {
            return RespResult.fail("用户不存在");
        }
        if (username != null) existingUser.setUsername(username);
        if (phone != null) existingUser.setPhone(phone);
        if (age != null) existingUser.setAge(age);
        if (gender != null) existingUser.setGender(Gender.valueOf(gender));
        if (avatar != null) existingUser.setAvatar(avatar);
        if (userProfileService.updateUserProfile(loginUser.getUserId(), existingUser)) {
            // 同步session
            session.setAttribute("loginUser", existingUser);
            return RespResult.success("用户资料更新成功");
        } else {
            return RespResult.fail("用户资料更新失败");
        }
    }

    /**
     * 修改当前登录用户密码
     */
    @PostMapping("/updatePassword")
    public RespResult updatePassword(@RequestParam("oldPass") String oldPassword,
                                     @RequestParam("newPass") String newPassword,
                                     HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            return RespResult.fail("未登录");
        }
        // 校验旧密码
        if (!userProfileService.checkPassword(loginUser.getUserId(), oldPassword)) {
            return RespResult.fail("原密码错误");
        }
        User user = userProfileService.getUserProfile(loginUser.getUserId());
        user.setPassword(newPassword);
        if (userProfileService.updateUserProfile(loginUser.getUserId(), user)) {
            return RespResult.success("密码修改成功");
        } else {
            return RespResult.fail("密码修改失败");
        }
    }

    /**
     * 上传头像，调用FileController实现
     */
    @PostMapping("/uploadAvatar")
    public RespResult uploadAvatar(@RequestParam("file") MultipartFile file, HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            return RespResult.fail("未登录");
        }
        // 调用文件上传服务
        RespResult uploadResult = fileController.uploadFile(file);
        if (!"SUCCESS".equals(uploadResult.getCode())) {
            return uploadResult; // 上传失败直接返回
        }
        String fileUrl = (String) uploadResult.getData();
        User user = userProfileService.getUserProfile(loginUser.getUserId());
        user.setAvatar(fileUrl);
        if (userProfileService.updateUserProfile(loginUser.getUserId(), user)) {
            // 同步session
            session.setAttribute("loginUser", user);
            return RespResult.success("头像上传并更新成功", fileUrl);
        } else {
            return RespResult.fail("头像上传成功但用户信息更新失败");
        }
    }
}