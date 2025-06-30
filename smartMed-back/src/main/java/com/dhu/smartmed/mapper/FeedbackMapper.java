package com.dhu.smartmed.mapper;

import com.dhu.smartmed.entity.Feedback;
import com.dhu.smartmed.enums.Status;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface FeedbackMapper {
    List<Feedback> findFeedbacksByUserId(Integer userId);
    List<Feedback> findFeedbacksByStatus(Status status);
    List<Feedback> findAllFeedbacks();
    Feedback findFeedbackById(Integer id);
    int insertFeedback(Feedback feedback);
    int updateFeedbackStatus(@Param("feedbackId") Integer feedbackId, @Param("status") Status status);
    int deleteFeedbackById(Integer feedbackId);
}
