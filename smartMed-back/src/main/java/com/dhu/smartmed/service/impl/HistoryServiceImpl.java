package com.dhu.smartmed.service.impl;

import com.dhu.smartmed.entity.History;
import com.dhu.smartmed.mapper.HistoryMapper;
import com.dhu.smartmed.service.HistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class HistoryServiceImpl implements HistoryService {

    @Autowired
    private HistoryMapper HistoryMapper;

    @Override
    public List<History> getHistoriesByUserId(Integer userId) {
        return HistoryMapper.findHistoriesByUserId(userId);
    }

    @Override
    @Transactional
    public boolean addHistory(History history) {
        return HistoryMapper.insertHistory(history)>0;
    }

    @Override
    @Transactional
    public boolean updateHistory(Integer id, History history) {
        History existingHistory = getHistoryById(id);
        existingHistory.setDiseaseName(history.getDiseaseName());
        existingHistory.setDescription(history.getDescription());
        existingHistory.setDescription(history.getDescription());
        return HistoryMapper.updateHistory(existingHistory)>0;
    }

    @Override
    @Transactional
    public boolean deleteHistory(Integer id) {
        return HistoryMapper.deleteHistory(id)>0;
    }

    @Override
    public History getHistoryById(Integer id) {
        // 调用 Mapper 方法获取历史记录
        History history = HistoryMapper.findHistoryById(id);
        // 检查返回值是否为 null
        if (history == null) {
            // 如果返回值为 null，抛出自定义异常
            throw new RuntimeException("Medical history record not found");
        }
        // 如果返回值不为 null，返回历史记录
        return history;
    }

} 