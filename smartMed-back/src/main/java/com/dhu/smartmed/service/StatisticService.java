package com.dhu.smartmed.service;

import com.dhu.smartmed.entity.Statistic;
import java.util.List;
import java.util.Map;

public interface StatisticService {
    Statistic findStatisticByName(String statName);
    List<Statistic> findAllStatistics();
    boolean updateStatistic(String statName, Integer statValue);
    Map<String, Integer> getDashboardStatistics();
    Map<String, Integer> getUserStatistics(Integer userId);
}
