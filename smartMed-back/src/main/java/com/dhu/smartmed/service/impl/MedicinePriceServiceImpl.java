package com.dhu.smartmed.service.impl;

import com.dhu.smartmed.entity.MedicinePrice;
import com.dhu.smartmed.mapper.MedicinePriceMapper;
import com.dhu.smartmed.service.MedicinePriceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class MedicinePriceServiceImpl implements MedicinePriceService {

    @Autowired
    private MedicinePriceMapper medicinePriceMapper;

    @Override
    @Transactional
    public boolean saveAllPrices(List<MedicinePrice> prices) {
        return medicinePriceMapper.insertPrices(prices)>0;
    }

    @Override
    @Transactional
    public boolean savePrice(MedicinePrice price) {
        return medicinePriceMapper.insertPrice(price)>0;
    }

    @Override
    @Transactional
    public boolean deletePricesByMedicineId(Integer medicineId) {
        return medicinePriceMapper.deletePrice(medicineId)>0;
    }

    @Override
    public List<MedicinePrice> getPricesByMedicineId(Integer medicineId) {
        return medicinePriceMapper.findPricesByMedicineId(medicineId);
    }

} 