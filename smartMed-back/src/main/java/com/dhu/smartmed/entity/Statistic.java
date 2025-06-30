package com.dhu.smartmed.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Statistic {
    private Integer statId;
    private String statName;
    private Integer statValue;
    private Timestamp lastUpdated;
}
