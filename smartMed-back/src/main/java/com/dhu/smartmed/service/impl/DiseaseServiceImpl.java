package com.dhu.smartmed.service.impl;

import com.dhu.smartmed.entity.Disease;
import com.dhu.smartmed.entity.DiseaseMedicine;
import com.dhu.smartmed.entity.Medicine;
import com.dhu.smartmed.mapper.DiseaseMapper;
import com.dhu.smartmed.mapper.DiseaseMedicineMapper;
import com.dhu.smartmed.mapper.MedicineMapper;
import com.dhu.smartmed.service.DiseaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class DiseaseServiceImpl implements DiseaseService {
    @Autowired
    private DiseaseMapper diseaseMapper;
    @Autowired
    private DiseaseMedicineMapper diseasemedicineMapper;
    @Autowired
    private MedicineMapper medicineMapper;

    @Override
    public Disease findDiseaseById(Integer diseaseId) {
        return diseaseMapper.findDiseaseById(diseaseId);
    }

    @Override
    public List<Disease> findDiseaseByName(String name) {
        return diseaseMapper.findDiseasesByName(name);
    }

    @Override
    public List<Disease> findDiseaseBySymptom(String symptom) {
        return diseaseMapper.findDiseasesBySymptoms(symptom);
    }

    @Override
    public List<Disease> findDiseaseByCategory(String category) {
        return diseaseMapper.findDiseasesByKind(category);
    }

    @Override
    public List<Disease> findAllDiseases() {
        return diseaseMapper.findAllDiseases();
    }

    @Override
    public List<Disease> findDiseasesByPage(Integer pageNo, Integer pageSize) {
        return diseaseMapper.findDiseasesByPage(pageNo, pageSize);
    }

    @Override
    public boolean addDisease(Disease disease) {
        return diseaseMapper.insertDisease(disease) > 0;
    }

    @Override
    public boolean updateDisease(Disease disease) {
        return diseaseMapper.updateDisease(disease) > 0;
    }

    @Override
    public boolean deleteDisease(Integer diseaseId) {
        return diseaseMapper.deleteDisease(diseaseId) > 0;
    }

    @Override
    public List<Medicine> findRelatedMedicines(Integer diseaseId) {
        List<DiseaseMedicine> diseaseMedicineList = diseasemedicineMapper.findRelationsByDisease(diseaseId);
        List<Medicine> medicines = new ArrayList<>();
        for (DiseaseMedicine diseaseMedicine : diseaseMedicineList) {
            Medicine medicine = medicineMapper.findMedicineById(diseaseMedicine.getMedicineId());
            medicines.add(medicine);
        }
        return medicines;
    }
}
