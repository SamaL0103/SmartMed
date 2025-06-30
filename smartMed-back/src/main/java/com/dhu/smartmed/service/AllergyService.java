package com.dhu.smartmed.service;

import com.dhu.smartmed.entity.Allergy;
import java.util.List;

public interface AllergyService {
    // 获取用户的所有过敏史
    List<Allergy> getAllergiesByUserId(Integer userId);
    
    // 添加新的过敏史记录
    boolean addAllergy(Allergy allergy);
    
    // 更新过敏史记录
    boolean updateAllergy(Integer id, Allergy allergy);
    
    // 删除过敏史记录
    boolean deleteAllergy(Integer id);
    
    // 获取单个过敏史记录
    Allergy getAllergyById(Integer id);

    // 检查用户是否对特定药物过敏
    boolean isAllergicTo(Integer userId, String medicineName);
} 