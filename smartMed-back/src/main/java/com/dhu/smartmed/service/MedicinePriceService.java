package com.dhu.smartmed.service;

import com.dhu.smartmed.entity.MedicinePrice;
import java.util.List;

public interface MedicinePriceService {
    // 保存所有价格记录
    boolean saveAllPrices(List<MedicinePrice> prices);

    boolean savePrice(MedicinePrice price);
    // 根据药品ID删除所有价格记录
    boolean deletePricesByMedicineId(Integer medicineId);
    
    // 获取药品的所有价格记录
    List<MedicinePrice> getPricesByMedicineId(Integer medicineId);

} 