package com.dhu.smartmed.service;

import com.dhu.smartmed.entity.ChatRecord;
import java.util.List;

public interface ChatRecordService {
    List<ChatRecord> findRecordsByUserId(Integer userId);
    List<ChatRecord> findRecentRecordsByUserId(Integer userId, Integer limit);
    List<ChatRecord> findAllRecords();
    boolean saveRecord(ChatRecord chatRecord);
    List<ChatRecord> getConversation(Integer userId, Integer messageCount);
    boolean deleteRecord(Integer chatId);
    boolean deleteRecordsByUserId(Integer userId);
}
