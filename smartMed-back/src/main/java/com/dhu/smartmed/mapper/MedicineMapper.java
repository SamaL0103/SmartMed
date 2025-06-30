package com.dhu.smartmed.mapper;

import com.dhu.smartmed.entity.Medicine;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MedicineMapper {
    Medicine findMedicineById(Integer medicineId);
    Medicine findMedicineByName(String name);
    List<Medicine> findMedicinesByKeyword(String keyword); //根据药品名称（、功效、规格等）关键字查询药品，目前仅实现名称关键词匹配，括号内想实现自行修改xml文件
    List<Medicine> findMedicinesByEfficacy(String efficacy);
    List<Medicine> findAllMedicines();
    int insertMedicine(Medicine medicine);
    int updateMedicine(Medicine medicine);
    int deleteMedicine(Integer medicineId);
}
