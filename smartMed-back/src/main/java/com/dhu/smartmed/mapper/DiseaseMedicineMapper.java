package com.dhu.smartmed.mapper;

import com.dhu.smartmed.entity.DiseaseMedicine;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DiseaseMedicineMapper {
    List<DiseaseMedicine> findRelationsByDisease(Integer diseaseId);
    List<DiseaseMedicine> findRelationsByMedicine(Integer medicineId);
    int insertRelation(DiseaseMedicine diseaseMedicine);
    int deleteRelation(@Param("diseaseId") Integer diseaseId, @Param("medicineId") Integer medicineId);
}
