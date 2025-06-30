package com.dhu.smartmed.service;

import com.dhu.smartmed.entity.Medicine;
import java.util.List;

public interface MedicineService {
    List<Medicine> getAllMedicines();
    List<Medicine> searchMedicines(String keyword);
    Medicine getMedicineById(Integer id);
    List<Medicine> searchMedicinesByEfficiacy(String efficacy);
    Medicine searchMedicinesByName(String name);
    boolean addMedicine(Medicine medicine);
    boolean updateMedicine(Integer id, Medicine medicine);
    boolean deleteMedicine(Integer id);
} 