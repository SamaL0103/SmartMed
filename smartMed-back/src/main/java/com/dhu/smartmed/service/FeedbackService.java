package com.dhu.smartmed.service;

import com.dhu.smartmed.entity.Feedback;
import com.dhu.smartmed.enums.Status;

import java.util.List;

public interface FeedbackService {
    boolean submitFeedback(Feedback feedback);
    List<Feedback> getAllFeedback();
    List<Feedback> getFeedbackByuserId(Integer id);
    List<Feedback> findFeedbacksByStatus(Status status);
    Feedback getFeedbackById(Integer id);
    boolean updateFeedbackStatus(Integer id, Status status);
    boolean deleteFeedback(Integer id);
} 