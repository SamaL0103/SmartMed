package com.dhu.smartmed.mapper;

import org.apache.ibatis.annotations.Mapper;
import com.dhu.smartmed.entity.Allergy;

import java.util.List;

@Mapper
public interface AllergyMapper {
    List<Allergy> findAllergiesByUserId(Integer userId);
    int insertAllergy(Allergy allergy);
    int updateAllergy(Allergy allergy);
    int deleteAllergy(Integer allergyId);
    Allergy findById(Integer id);
}
