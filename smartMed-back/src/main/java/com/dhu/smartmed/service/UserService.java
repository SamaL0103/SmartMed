package com.dhu.smartmed.service;

import com.dhu.smartmed.entity.User;

import java.util.List;

public interface UserService {

    boolean login(String username,String password);
    boolean register(User user);
    User getUserById(Integer userId);
    User getUserByName(String username);
    List<User> findAll();
    List<User> findByPage(Integer pageNo,Integer pageSize);
    boolean addUser(User user);
    boolean updateUser(User user);
    boolean deleteUser(Integer userId);
    boolean isAdmin(Integer userId);
}
