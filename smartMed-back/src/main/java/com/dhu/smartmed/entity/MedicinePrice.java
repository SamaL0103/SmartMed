package com.dhu.smartmed.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MedicinePrice {
    private Integer priceId;
    private Integer medicineId;
    private String storeName;
    private Double price;
    private String specification; // 规格说明
    private String url;
}
