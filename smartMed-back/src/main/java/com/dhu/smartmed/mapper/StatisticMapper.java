package com.dhu.smartmed.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.dhu.smartmed.entity.Statistic;

import java.util.List;

@Mapper
public interface StatisticMapper {

    Statistic findStatisticByName(String statName);
    List<Statistic> findAllStatistics();
    int updateStatistic(@Param("statName") String statName, @Param("statValue") Integer statValue);
    int insertStatistic(Statistic statistic);
    void updateAllStatistics();
    
    // 用户统计相关方法
    int countUserAllergies(@Param("userId") Integer userId);
    int countUserHistories(@Param("userId") Integer userId);
    int countUserChatRecords(@Param("userId") Integer userId);
    int countUserFeedbacks(@Param("userId") Integer userId);
}
