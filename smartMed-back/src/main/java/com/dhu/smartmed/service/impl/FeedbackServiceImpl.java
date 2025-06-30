package com.dhu.smartmed.service.impl;

import com.dhu.smartmed.enums.Status;
import com.dhu.smartmed.mapper.FeedbackMapper;
import com.dhu.smartmed.entity.Feedback;
import com.dhu.smartmed.service.FeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class FeedbackServiceImpl implements FeedbackService {

    @Autowired
    private FeedbackMapper feedbackMapper;

    @Override
    @Transactional
    public boolean submitFeedback(Feedback feedback) {
        feedback.setStatus(Status.Pending);
        return feedbackMapper.insertFeedback(feedback)>0;
    }

    @Override
    public List<Feedback> getAllFeedback() {
        return feedbackMapper.findAllFeedbacks();
    }

    @Override
    public List<Feedback> getFeedbackByuserId(Integer id) {
        List<Feedback> feedback = feedbackMapper.findFeedbacksByUserId(id);
        if (feedback == null) {
            // 如果返回值为 null，抛出自定义异常
            throw new RuntimeException("Feedback not found");
        }
        return feedback;
    }
    @Override
    public List<Feedback> findFeedbacksByStatus(Status status) {
        List<Feedback> feedback = feedbackMapper.findFeedbacksByStatus(status);
        if (feedback == null) {
            // 如果返回值为 null，抛出自定义异常
            throw new RuntimeException("Feedback not found");
        }
        return feedback;
    }
    @Override
    public Feedback getFeedbackById(Integer id) {
        return feedbackMapper.findFeedbackById(id);
    }
    @Override
    @Transactional
    public boolean updateFeedbackStatus(Integer id, Status status) {
        return feedbackMapper.updateFeedbackStatus(id,status)>0;
    }

    @Override
    @Transactional
    public boolean deleteFeedback(Integer id) {
        return feedbackMapper.deleteFeedbackById(id)>0;
    }
} 