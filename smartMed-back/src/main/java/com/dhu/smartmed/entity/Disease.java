package com.dhu.smartmed.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Disease {
    private Integer diseaseId;
    private String name;
    private String category; // 疾病所属分类（呼吸系统疾病，消化系统疾病等）
    private String causes;
    private String symptoms;

}
