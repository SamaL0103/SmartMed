package com.dhu.smartmed.service.impl;

import com.dhu.smartmed.entity.Statistic;
import com.dhu.smartmed.mapper.StatisticMapper;
import com.dhu.smartmed.service.StatisticService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StatisticServiceImpl implements StatisticService {
    @Autowired
    private StatisticMapper statisticMapper; 
    
    @Override
    public Statistic findStatisticByName(String statName) {
        return statisticMapper.findStatisticByName(statName);
    }
    
    @Override
    public List<Statistic> findAllStatistics() {
        return statisticMapper.findAllStatistics();
    }
    
    @Override
    public boolean updateStatistic(String statName, Integer statValue) {
        try {
            // 检查是否存在该统计项
            Statistic statistic = statisticMapper.findStatisticByName(statName);
            
            if (statistic != null) {
                // 存在则更新
                return statisticMapper.updateStatistic(statName, statValue) > 0;
            } else {
                // 不存在则创建新的统计项
                Statistic newStatistic = new Statistic();
                newStatistic.setStatName(statName);
                newStatistic.setStatValue(statValue);
                return statisticMapper.insertStatistic(newStatistic) > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Map<String, Integer> getDashboardStatistics() {
        // 先调用存储过程确保统计数据是最新的
        try {
            statisticMapper.updateAllStatistics();
        } catch (Exception e) {
            // 存储过程调用失败，返回当前的统计数据
            System.err.println("更新统计数据存储过程调用失败: " + e.getMessage());
        }
        
        // 获取所有统计数据并构建Map返回
        List<Statistic> statistics = statisticMapper.findAllStatistics();
        Map<String, Integer> dashboardStats = new HashMap<>();
        
        for (Statistic stat : statistics) {
            dashboardStats.put(stat.getStatName(), stat.getStatValue());
        }
        
        return dashboardStats;
    }
    
    @Override
    public Map<String, Integer> getUserStatistics(Integer userId) {
        Map<String, Integer> userStats = new HashMap<>();
        
        try {
            // 获取用户特定的统计数据
            // 这里假设数据库可能有一些用户特定的统计，如果没有，可以根据其他表数据计算
            
            // 示例：计算用户的过敏记录数
            int allergyCount = statisticMapper.countUserAllergies(userId);
            userStats.put("allergyCount", allergyCount);
            
            // 示例：计算用户的病历记录数
            int historyCount = statisticMapper.countUserHistories(userId);
            userStats.put("historyCount", historyCount);
            
            // 示例：计算用户的聊天记录数
            int chatCount = statisticMapper.countUserChatRecords(userId);
            userStats.put("chatCount", chatCount);
            
            // 示例：计算用户的反馈记录数
            int feedbackCount = statisticMapper.countUserFeedbacks(userId);
            userStats.put("feedbackCount", feedbackCount);
            
        } catch (Exception e) {
            // 如果出现异常，添加一些基本的统计信息
            userStats.put("allergyCount", 0);
            userStats.put("historyCount", 0);
            userStats.put("chatCount", 0);
            userStats.put("feedbackCount", 0);
            System.err.println("获取用户统计数据失败: " + e.getMessage());
        }
        
        return userStats;
    }
}
