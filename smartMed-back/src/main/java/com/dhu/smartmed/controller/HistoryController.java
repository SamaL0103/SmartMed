    package com.dhu.smartmed.controller;

    import com.dhu.smartmed.dto.RespResult;
    import com.dhu.smartmed.entity.History;
    import com.dhu.smartmed.entity.User;
    import com.dhu.smartmed.service.HistoryService;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.web.bind.annotation.*;
    import jakarta.servlet.http.HttpSession;
    import java.util.List;

    @RestController
    @RequestMapping("/api/users/{userId}/histories")
    public class HistoryController {

        @Autowired
        private HistoryService historyService;

        /**
         * 获取特定用户的病历列表
         */
        @GetMapping
        public RespResult getUserHistories(
                @PathVariable Integer userId,
                HttpSession session) {
            User loginUser = (User) session.getAttribute("loginUser");
            if (!isAuthenticated(loginUser, userId)) {
                return RespResult.fail("未登录或无权访问");
            }

            List<History> histories = historyService.getHistoriesByUserId(userId);
            return RespResult.success("获取成功", histories);
        }

        /**
         * 添加病历记录
         */
        @PostMapping
        public RespResult addHistory(
                @PathVariable Integer userId,
                @RequestBody History newHistory,
                HttpSession session) {
            User loginUser = (User) session.getAttribute("loginUser");
            if (!isAuthenticated(loginUser, userId)) {
                return RespResult.fail("未登录或无权操作");
            }

            newHistory.setUserId(userId);
            if (historyService.addHistory(newHistory)) {
                return RespResult.success("病历添加成功", newHistory);
            } else {
                return RespResult.fail("病历添加失败");
            }
        }

        /**
         * 获取单个病历详情
         */
        @GetMapping("/{id}")
        public RespResult getHistoryDetails(
                @PathVariable Integer userId,
                @PathVariable Integer id,
                HttpSession session) {
            User loginUser = (User) session.getAttribute("loginUser");
            if (!isAuthenticated(loginUser, userId)) {
                return RespResult.fail("未登录或无权访问");
            }

            History history = historyService.getHistoryById(id);
            if (history == null || !history.getUserId().equals(userId)) {
                return RespResult.fail("病历不存在");
            }
            return RespResult.success("获取成功", history);
        }

        /**
         * 更新病历记录 (使用 PUT)
         */
        @PutMapping("/{id}")
        public RespResult updateHistory(
                @PathVariable Integer userId,
                @PathVariable Integer id,
                @RequestBody History updatedHistory,
                HttpSession session) {
            User loginUser = (User) session.getAttribute("loginUser");
            if (!isAuthenticated(loginUser, userId)) {
                return RespResult.fail("未登录或无权操作");
            }

            History existingHistory = historyService.getHistoryById(id);
            if (existingHistory == null || !existingHistory.getUserId().equals(userId)) {
                return RespResult.fail("病历记录不存在");
            }

            updatedHistory.setHistoryId(id);
            updatedHistory.setUserId(userId);
            if (historyService.updateHistory(id, updatedHistory)) {
                return RespResult.success("病历更新成功", updatedHistory);
            } else {
                return RespResult.fail("病历更新失败");
            }
        }

        /**
         * 删除病历记录
         */
        @DeleteMapping("/{id}")
        public RespResult deleteHistory(
                @PathVariable Integer userId,
                @PathVariable Integer id,
                HttpSession session) {
            User loginUser = (User) session.getAttribute("loginUser");
            if (!isAuthenticated(loginUser, userId)) {
                return RespResult.fail("未登录或无权操作");
            }

            History existingHistory = historyService.getHistoryById(id);
            if (existingHistory == null || !existingHistory.getUserId().equals(userId)) {
                return RespResult.fail("病历记录不存在");
            }

            if (historyService.deleteHistory(id)) {
                return RespResult.success("病历删除成功");
            } else {
                return RespResult.fail("病历删除失败");
            }
        }

        /**
         * 辅助方法：检查用户是否已登录且有权访问该 userId 的资源
         */
        private boolean isAuthenticated(User loginUser, Integer resourceUserId) {
            return loginUser != null && loginUser.getUserId().equals(resourceUserId);
        }
    }