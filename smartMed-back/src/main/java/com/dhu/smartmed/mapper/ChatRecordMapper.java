package com.dhu.smartmed.mapper;

import com.dhu.smartmed.entity.ChatRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ChatRecordMapper {
    List<ChatRecord> findRecordsByUserId(Integer userId);
    List<ChatRecord> findRecentRecordsByUserId(@Param("userId") Integer userId,@Param("limit")Integer limit);
    List<ChatRecord> findAllRecords();
    int insertRecord(ChatRecord chatRecord);
    int deleteRecord(Integer chatId);
}
