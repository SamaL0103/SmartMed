package com.dhu.smartmed.mapper;

import com.dhu.smartmed.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface UserMapper {
    User findUserById(Integer userId);
    User findUserByName(String username);
    List<User> findAllUsers();
    List<User> findUsersByPage(@Param("pageNo") Integer pageNo, @Param("pageSize") Integer pageSize);
    int insertUser(User user);
    int updateUser(User user);
    int deleteUser(Integer userId);
}
