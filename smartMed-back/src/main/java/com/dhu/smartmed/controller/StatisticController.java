package com.dhu.smartmed.controller;

import com.dhu.smartmed.dto.RespResult;
import com.dhu.smartmed.entity.User;
import com.dhu.smartmed.service.StatisticService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.util.Map;

@RestController
@RequestMapping("/api/statistic")
public class StatisticController {

    @Autowired
    private StatisticService statisticService;

    /**
     * 管理员仪表盘首页
     * 显示统计数据和导航栏
     */
    @GetMapping("/dashboard")
    public RespResult dashboard(HttpSession session) {
        // 检查用户是否登录并是管理员
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null || !loginUser.getIsAdmin()) {
            return RespResult.fail("未登录或非管理员");
        }
        
        // 获取统计数据
        Map<String, Integer> statistics = statisticService.getDashboardStatistics();
        
        return RespResult.success("获取成功", statistics);
    }
    
    /**
     * 获取统计数据（API调用）
     */
    @GetMapping("/api/statistics")
    public RespResult getStatistics(HttpSession session) {
        // 检查用户是否登录并是管理员
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null || !loginUser.getIsAdmin()) {
            return RespResult.fail("无权限访问");
        }
        
        // 获取统计数据
        Map<String, Integer> statistics = statisticService.getDashboardStatistics();
        
        return RespResult.success("获取成功", statistics);
    }
} 