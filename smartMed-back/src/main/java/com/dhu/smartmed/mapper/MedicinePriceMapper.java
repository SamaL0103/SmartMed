package com.dhu.smartmed.mapper;

import com.dhu.smartmed.entity.MedicinePrice;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MedicinePriceMapper {
    List<MedicinePrice> findPricesByMedicineId(Integer medicineId);
    int insertPrices(List<MedicinePrice> prices);
    int insertPrice(MedicinePrice medicinePrice);
    int updatePrice(MedicinePrice medicinePrice);
    int deletePrice(Integer priceId);
}
