package com.dhu.smartmed.service.impl;

import com.dhu.smartmed.entity.DiseaseMedicine;
import com.dhu.smartmed.mapper.DiseaseMedicineMapper;
import com.dhu.smartmed.service.DiseaseMedicineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DiseaseMedicineServiceImpl implements DiseaseMedicineService {
    
    @Autowired
    private DiseaseMedicineMapper diseaseMedicineMapper;
    
    @Override
    public boolean addRelation(DiseaseMedicine diseaseMedicine) {
        return diseaseMedicineMapper.insertRelation(diseaseMedicine) > 0;
    }
    
    @Override
    public boolean deleteRelation(Integer diseaseId, Integer medicineId) {
        return diseaseMedicineMapper.deleteRelation(diseaseId, medicineId) > 0;
    }
    
    @Override
    public List<DiseaseMedicine> findByDiseaseId(Integer diseaseId) {
        return diseaseMedicineMapper.findRelationsByDisease(diseaseId);
    }

    @Override
    public List<DiseaseMedicine> findByMedicineId(Integer medicineId) {
        return diseaseMedicineMapper.findRelationsByMedicine(medicineId);
    }
}
