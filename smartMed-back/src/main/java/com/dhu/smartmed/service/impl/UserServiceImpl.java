package com.dhu.smartmed.service.impl;

import com.dhu.smartmed.entity.User;
import com.dhu.smartmed.mapper.UserMapper;
import com.dhu.smartmed.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    
    @Autowired
    private UserMapper userMapper;

    @Override
    public boolean login(String username, String password) {
        User user = userMapper.findUserByName(username);
        return user != null && user.getPassword().equals(password);
    }

    @Override
    public boolean register(User user) {
        return userMapper.insertUser(user) > 0;
    }

    @Override
    public User getUserById(Integer userId) {
        return userMapper.findUserById(userId);
    }

    @Override
    public User getUserByName(String username) {
        return userMapper.findUserByName(username);
    }

    @Override
    public List<User> findAll() {   
        return userMapper.findAllUsers();
    }

    @Override
    public List<User> findByPage(Integer pageNo, Integer pageSize) {
        return userMapper.findUsersByPage(pageNo, pageSize);
    }

    @Override
    public boolean addUser(User user) {
        return userMapper.insertUser(user) > 0;
    }

    @Override
    public boolean deleteUser(Integer userId) {
        return userMapper.deleteUser(userId) > 0;
    }

    @Override
    public boolean isAdmin(Integer userId) {
        return userMapper.findUserById(userId).getIsAdmin();
    }

    @Override
    public boolean updateUser(User user) {
        return userMapper.updateUser(user) > 0;
    }
    
    
    
    
}
