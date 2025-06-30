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
public class ChatRecord {
    private Integer chatId;
    private Integer userId;
    private Boolean isFromUser;
    private String content;
    private Timestamp createdAt;
}
