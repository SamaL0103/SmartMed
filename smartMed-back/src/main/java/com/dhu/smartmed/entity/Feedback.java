package com.dhu.smartmed.entity;

import com.dhu.smartmed.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Feedback {
    private Integer feedbackId;
    private Integer userId;
    private String content;
    private Status status;
    private Timestamp createdAt;
}
