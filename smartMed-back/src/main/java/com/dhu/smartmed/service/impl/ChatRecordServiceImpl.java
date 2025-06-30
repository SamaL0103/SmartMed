package com.dhu.smartmed.service.impl;

import com.dhu.smartmed.entity.ChatRecord;
import com.dhu.smartmed.mapper.ChatRecordMapper;
import com.dhu.smartmed.service.ChatRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ChatRecordServiceImpl implements ChatRecordService {
    @Autowired
    private ChatRecordMapper chatRecordMapper;

    @Override
    public List<ChatRecord> findRecordsByUserId(Integer userId) {
        return chatRecordMapper.findRecordsByUserId(userId);
    }

    @Override
    public List<ChatRecord> findAllRecords() {
        return chatRecordMapper.findAllRecords();
    }

    @Override
    public List<ChatRecord> findRecentRecordsByUserId(Integer userId, Integer limit) {
        return chatRecordMapper.findRecentRecordsByUserId(userId, limit);
    }

    @Override
    public boolean saveRecord(ChatRecord chatRecord) {
        return chatRecordMapper.insertRecord(chatRecord) > 0;
    }

    @Override
    public List<ChatRecord> getConversation(Integer userId, Integer messageCount) {
        List<ChatRecord> records = chatRecordMapper.findRecordsByUserId(userId);
        return records.subList(Math.max(0, records.size() - messageCount), records.size());
    }

    @Override
    public boolean deleteRecord(Integer chatId) {
        return chatRecordMapper.deleteRecord(chatId) > 0;
    }

    @Override
    public boolean deleteRecordsByUserId(Integer userId) {
        List<ChatRecord> records = chatRecordMapper.findRecordsByUserId(userId);
        List<Integer> failedIds = new ArrayList<>();
    
        for (ChatRecord record : records) {
            if (chatRecordMapper.deleteRecord(record.getChatId()) <= 0) {
                failedIds.add(record.getChatId());
            }
        }
    
        if (!failedIds.isEmpty()) {
            System.out.println("以下记录删除失败,chatId为：" + failedIds);
            return false;
        }
    
        return true;
    }
}
