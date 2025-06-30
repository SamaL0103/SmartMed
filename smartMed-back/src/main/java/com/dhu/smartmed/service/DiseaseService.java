package com.dhu.smartmed.service;

import com.dhu.smartmed.entity.Disease;
import com.dhu.smartmed.entity.Medicine;
import java.util.List;

public interface DiseaseService {
    Disease findDiseaseById(Integer diseaseId);
    List<Disease> findDiseaseByName(String name);
    List<Disease> findDiseaseBySymptom(String symptom);
    List<Disease> findDiseaseByCategory(String category);
    List<Disease> findAllDiseases();
    List<Disease> findDiseasesByPage(Integer pageNo, Integer pageSize);
    boolean addDisease(Disease disease);
    boolean updateDisease(Disease disease);
    boolean deleteDisease(Integer diseaseId);
    List<Medicine> findRelatedMedicines(Integer diseaseId);

}
