package com.dhu.smartmed.entity;

import com.dhu.smartmed.enums.Gender;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.sql.Timestamp;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    private Integer userId;
    private String username; 
    private String password;
    private String phone;
    private Integer age;
    private Gender gender;
    private String avatar;
    private Boolean isAdmin;
    private Timestamp createdAt;

}
