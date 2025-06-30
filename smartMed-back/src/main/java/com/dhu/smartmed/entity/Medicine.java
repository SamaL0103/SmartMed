package com.dhu.smartmed.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Medicine {
    private Integer medicineId;
    private String name;
    private String category; // 药品分类（处方药/非处方药）
    private String efficacy; // 药品功效描述
    private String usageMethod; // 使用说明
    private String contraindications; // 使用禁忌
    private String sideEffects; // 副作用
}
