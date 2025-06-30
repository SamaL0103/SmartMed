package com.dhu.smartmed.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class History {
    private Integer historyId;
    private Integer userId;
    private String diseaseName;
    private String description;
}
