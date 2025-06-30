package com.dhu.smartmed.mapper;

import com.dhu.smartmed.entity.Disease;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;


@Mapper
public interface DiseaseMapper {
    Disease findDiseaseById(Integer diseaseId);
    List<Disease> findDiseasesByName(String name);
    List<Disease> findDiseasesByKeyword(String keyword); //根据疾病名称、病因、症状、分类等关键字查询疾病
    List<Disease> findAllDiseases();
    List<Disease> findDiseasesByKind(String keyword);
    List<Disease> findDiseasesBySymptoms(String keyword);
    List<Disease> findDiseasesByPage(@Param("pageNo") Integer pageNo, @Param("pageSize") Integer pageSize);
    int insertDisease(Disease disease);
    int updateDisease(Disease disease);
    int deleteDisease(Integer diseaseId);
}
