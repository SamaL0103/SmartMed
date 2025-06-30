package com.dhu.smartmed.service;

import com.dhu.smartmed.entity.User;

public interface UserProfileService {
    User getUserProfile(Integer userId);
    boolean updateUserProfile(Integer userId, User user);
    boolean checkPassword(Integer userId, String password);
} 