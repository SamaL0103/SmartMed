package com.dhu.smartmed.service.impl;

import com.dhu.smartmed.entity.User;
import com.dhu.smartmed.mapper.UserMapper;
import com.dhu.smartmed.service.UserProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserProfileServiceImpl implements UserProfileService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public User getUserProfile(Integer userId) {
        // 调用 Mapper 方法获取用户信息
        User user = userMapper.findUserById(userId);
        // 检查返回值是否为 null
        if (user == null) {
            // 如果返回值为 null，抛出自定义异常
            throw new RuntimeException("User not found");
        }
        // 如果返回值不为 null，返回用户信息
        return user;
    }

    @Override
    public boolean updateUserProfile(Integer userId, User user) {
        User existingUser = getUserProfile(userId);
        existingUser.setUsername(user.getUsername());
        existingUser.setPassword(user.getPassword());
        existingUser.setPhone(user.getPhone());
        existingUser.setAge(user.getAge());
        existingUser.setGender(user.getGender());
        existingUser.setAvatar(user.getAvatar());
        existingUser.setIsAdmin(user.getIsAdmin());
        return userMapper.updateUser(existingUser)>0;
    }
    @Override
    public boolean checkPassword(Integer userId, String oldPassword) {
        User user = getUserProfile(userId);
        return user.getPassword().equals(oldPassword);
    }
} 