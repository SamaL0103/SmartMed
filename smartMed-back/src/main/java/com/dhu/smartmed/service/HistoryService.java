package com.dhu.smartmed.service;

import com.dhu.smartmed.entity.History;
import java.util.List;
import java.time.LocalDate;

public interface HistoryService {
    // 获取用户的所有病史记录
    List<History> getHistoriesByUserId(Integer userId);
    
    // 添加新的病史记录
    boolean addHistory(History history);
    
    // 更新病史记录
    boolean updateHistory(Integer id, History history);
    
    // 删除病史记录
    boolean deleteHistory(Integer id);
    
    // 获取单个病史记录
    History getHistoryById(Integer id);
} 