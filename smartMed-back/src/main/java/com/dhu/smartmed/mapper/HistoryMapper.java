package com.dhu.smartmed.mapper;


import com.dhu.smartmed.entity.History;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface HistoryMapper {
    List<History> findHistoriesByUserId(Integer userId);
    History findHistoryById(Integer id);
    int insertHistory(History history);
    int updateHistory(History history);
    int deleteHistory(Integer historyId);

}
