package com.dhu.smartmed.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Allergy {
    private Integer allergyId;
    private Integer userId;
    private String allergen;  // 致敏源
    private String description;
}
