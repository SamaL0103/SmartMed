package com.dhu.smartmed.service.impl;

import com.dhu.smartmed.entity.Medicine;
import com.dhu.smartmed.mapper.MedicineMapper;
import com.dhu.smartmed.service.MedicineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MedicineServiceImpl implements MedicineService {

    @Autowired
    private MedicineMapper medicineMapper;

    @Override
    public List<Medicine> getAllMedicines() {
        return medicineMapper.findAllMedicines();
    }

    @Override
    public List<Medicine> searchMedicines(String keyword) {
        return medicineMapper.findMedicinesByKeyword(keyword);
    }

    @Override
    public Medicine searchMedicinesByName(String name) {
        return medicineMapper.findMedicineByName(name);
    }

    @Override
    public List<Medicine> searchMedicinesByEfficiacy(String efficiacy) {
        return medicineMapper.findMedicinesByEfficacy(efficiacy);
    }

    @Override
    public Medicine getMedicineById(Integer id) {
        // 调用 Mapper 方法获取药品信息
        Medicine medicine = medicineMapper.findMedicineById(id);
        // 检查返回值是否为 null
        if (medicine == null) {
            // 如果返回值为 null，抛出自定义异常
            throw new RuntimeException("Medicine not found");
        }
        // 如果返回值不为 null，返回药品信息
        return medicine;
    }

    @Override
    @Transactional
    public boolean addMedicine(Medicine medicine) {
        return medicineMapper.insertMedicine(medicine)>0;
    }

    @Override
    @Transactional
    public boolean updateMedicine(Integer id, Medicine medicine) {
        Medicine existingMedicine = getMedicineById(id);
        existingMedicine.setName(medicine.getName());
        existingMedicine.setCategory(medicine.getCategory());
        existingMedicine.setEfficacy(medicine.getEfficacy());
        existingMedicine.setUsageMethod(medicine.getUsageMethod());
        existingMedicine.setContraindications(medicine.getContraindications());
        existingMedicine.setSideEffects(medicine.getSideEffects());
        return medicineMapper.updateMedicine(existingMedicine)>0;
    }

    @Override
    @Transactional
    public boolean deleteMedicine(Integer id) {
        return medicineMapper.deleteMedicine(id)>0;
    }

} 