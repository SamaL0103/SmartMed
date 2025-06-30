package com.dhu.smartmed.service.impl;

import com.dhu.smartmed.entity.Allergy;
import com.dhu.smartmed.mapper.AllergyMapper;
import com.dhu.smartmed.service.AllergyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AllergyServiceImpl implements AllergyService {

    @Autowired
    private AllergyMapper allergyMapper;

    @Override
    public List<Allergy> getAllergiesByUserId(Integer userId) {
        return allergyMapper.findAllergiesByUserId(userId);
    }

    @Override
    @Transactional
    public boolean addAllergy(Allergy allergy) {
        return allergyMapper.insertAllergy(allergy)>0;
    }

    @Override
    @Transactional
    public boolean updateAllergy(Integer id, Allergy allergy) {
        Allergy existingAllergy = getAllergyById(id);
        existingAllergy.setAllergen(allergy.getAllergen());
        existingAllergy.setAllergyId(allergy.getAllergyId());
        existingAllergy.setDescription(allergy.getDescription());
        return allergyMapper.updateAllergy(existingAllergy)>0;
    }

    @Override
    @Transactional
    public boolean deleteAllergy(Integer id) {
        return allergyMapper.deleteAllergy(id)>0;
    }

    @Override
    public Allergy getAllergyById(Integer id) {
        Allergy allergy = allergyMapper.findById(id);
        if (allergy == null) {
            throw new RuntimeException("Allergy record not found");
        }
        return allergy;
    }

    @Override
    public boolean isAllergicTo(Integer userId, String medicineName) {
        List<Allergy> allergies = getAllergiesByUserId(userId);
        return allergies.stream()
                .anyMatch(allergy -> allergy.getAllergen().toLowerCase()
                        .contains(medicineName.toLowerCase()));
    }
} 