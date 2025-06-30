package com.dhu.smartmed.controller;

import com.dhu.smartmed.dto.RespResult;
import com.dhu.smartmed.entity.Feedback;
import com.dhu.smartmed.entity.User;
import com.dhu.smartmed.enums.Status;
import com.dhu.smartmed.service.FeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.util.List;

@RestController
@RequestMapping("/api/feedback")
public class FeedbackController {

    @Autowired
    private FeedbackService feedbackService;

    /**
     * 提交用户反馈
     */
    @PostMapping("/submit")
    public RespResult submitFeedback(
            @RequestParam("userId") Integer userId,
            @RequestParam("content") String content,
            HttpSession session) {
        // 检查用户是否登录
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            return RespResult.fail("未登录");
        }

        // 创建反馈对象
        Feedback feedback = Feedback.builder()
                .userId(userId)
                .content(content)
                .status(Status.Pending)
                .createdAt(new java.sql.Timestamp(System.currentTimeMillis()))
                .build();

        // 提交反馈
        if (feedbackService.submitFeedback(feedback)) {
            return RespResult.success("反馈提交成功");
        } else {
            return RespResult.fail("反馈提交失败");
        }
    }

    /**
     * 获取所有反馈列表（管理员）
     */
    @GetMapping("/all")
    public RespResult getAllFeedback(HttpSession session) {
        // 检查用户是否登录并是管理员
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null || !loginUser.getIsAdmin()) {
            return RespResult.fail("未登录或无权限");
        }

        // 获取所有反馈
        List<Feedback> feedbacks = feedbackService.getAllFeedback();
        return RespResult.success("获取成功", feedbacks);
    }

    /**
     * 获取特定用户的反馈列表
     */
    @GetMapping("/userFeedbacks")
    public RespResult getUserFeedbacks(
            @RequestParam("userId") Integer userId,
            HttpSession session) {
        // 检查用户是否登录
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            return RespResult.fail("未登录");
        }

        // 获取特定用户的反馈
        List<Feedback> feedbacks = feedbackService.getFeedbackByuserId(userId);
        return RespResult.success("获取成功", feedbacks);
    }

    /**
     * 获取特定状态的反馈列表（管理员）
     */
    @GetMapping("/statusFeedbacks")
    public RespResult getStatusFeedbacks(
            @RequestParam("status") Status status,
            HttpSession session) {
        // 检查用户是否登录并是管理员
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null || !loginUser.getIsAdmin()) {
            return RespResult.fail("未登录或无权限");
        }

        // 获取特定状态的反馈
        List<Feedback> feedbacks = feedbackService.findFeedbacksByStatus(status);
        return RespResult.success("获取成功", feedbacks);
    }

    /**
     * 更新反馈状态（管理员）
     */
    @PostMapping("/updateStatus")
    public RespResult updateFeedbackStatus(
            @RequestParam("id") Integer id,
            @RequestParam("status") Status status,
            HttpSession session) {
        // 检查用户是否登录并是管理员
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null || !loginUser.getIsAdmin()) {
            return RespResult.fail("未登录或无权限");
        }

        // 更新反馈状态
        if (feedbackService.updateFeedbackStatus(id, status)) {
            return RespResult.success("状态更新成功");
        } else {
            return RespResult.fail("状态更新失败");
        }
    }

    /**
     * 删除反馈（管理员）
     */
    @PostMapping("/delete")
    public RespResult deleteFeedback(
            @RequestParam("id") Integer id,
            HttpSession session) {
        // 检查用户是否登录并是管理员
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null || !loginUser.getIsAdmin()) {
            return RespResult.fail("未登录或无权限");
        }

        // 删除反馈
        if (feedbackService.deleteFeedback(id)) {
            return RespResult.success("删除成功");
        } else {
            return RespResult.fail("删除失败");
        }
    }
}