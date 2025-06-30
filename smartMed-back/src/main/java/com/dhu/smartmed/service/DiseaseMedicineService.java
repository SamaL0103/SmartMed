package com.dhu.smartmed.service;

import com.dhu.smartmed.entity.DiseaseMedicine;
import java.util.List;

public interface DiseaseMedicineService {
    boolean addRelation(DiseaseMedicine diseaseMedicine);
    boolean deleteRelation(Integer diseaseId, Integer medicineId);
    List<DiseaseMedicine> findByDiseaseId(Integer diseaseId);  
    List<DiseaseMedicine> findByMedicineId(Integer medicineId);
} 