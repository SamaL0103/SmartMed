package com.dhu.smartmed.controller;

import com.dhu.smartmed.dto.RespResult;
import com.dhu.smartmed.entity.Allergy;
import com.dhu.smartmed.entity.User;
import com.dhu.smartmed.service.AllergyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.util.List;

@RestController
@RequestMapping("/api/users/{userId}/allergies") // 过敏记录作为用户资源的子资源
public class AllergyController {

    @Autowired
    private AllergyService allergyService;

    /**
     * 获取特定用户的过敏记录列表
     */
    @GetMapping
    public RespResult getUserAllergies(
            @PathVariable Integer userId,
            HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (!isAuthenticated(loginUser, userId)) {
            return RespResult.fail("未登录或无权访问");
        }

        List<Allergy> allergies = allergyService.getAllergiesByUserId(userId);
        return RespResult.success("获取成功", allergies);
    }

    /**
     * 添加过敏记录
     */
    @PostMapping
    public RespResult addAllergy(
            @PathVariable Integer userId,
            @RequestBody Allergy newAllergy,
            HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (!isAuthenticated(loginUser, userId)) {
            return RespResult.fail("未登录或无权操作");
        }

        newAllergy.setUserId(userId);
        if (allergyService.addAllergy(newAllergy)) {
            return RespResult.success("过敏记录添加成功", newAllergy);
        } else {
            return RespResult.fail("过敏记录添加失败");
        }
    }

    /**
     * 获取单个过敏记录详情
     */
    @GetMapping("/{id}")
    public RespResult getAllergyDetails(
            @PathVariable Integer userId,
            @PathVariable Integer id,
            HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (!isAuthenticated(loginUser, userId)) {
            return RespResult.fail("未登录或无权访问");
        }

        Allergy allergy = allergyService.getAllergyById(id);
        if (allergy == null || !allergy.getUserId().equals(userId)) {
            return RespResult.fail("过敏记录不存在");
        }
        return RespResult.success("获取成功", allergy);
    }

    /**
     * 更新过敏记录 (使用 PUT)
     */
    @PutMapping("/{id}")
    public RespResult updateAllergy(
            @PathVariable Integer userId,
            @PathVariable Integer id,
            @RequestBody Allergy updatedAllergy,
            HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (!isAuthenticated(loginUser, userId)) {
            return RespResult.fail("未登录或无权操作");
        }

        Allergy existingAllergy = allergyService.getAllergyById(id);
        if (existingAllergy == null || !existingAllergy.getUserId().equals(userId)) {
            return RespResult.fail("过敏记录不存在");
        }

        updatedAllergy.setAllergyId(id);
        updatedAllergy.setUserId(userId);
        if (allergyService.updateAllergy(id, updatedAllergy)) {
            return RespResult.success("过敏记录更新成功", updatedAllergy);
        } else {
            return RespResult.fail("过敏记录更新失败");
        }
    }

    /**
     * 删除过敏记录
     */
    @DeleteMapping("/{id}")
    public RespResult deleteAllergy(
            @PathVariable Integer userId,
            @PathVariable Integer id,
            HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (!isAuthenticated(loginUser, userId)) {
            return RespResult.fail("未登录或无权操作");
        }

        Allergy existingAllergy = allergyService.getAllergyById(id);
        if (existingAllergy == null || !existingAllergy.getUserId().equals(userId)) {
            return RespResult.fail("过敏记录不存在");
        }

        if (allergyService.deleteAllergy(id)) {
            return RespResult.success("过敏记录删除成功");
        } else {
            return RespResult.fail("过敏记录删除失败");
        }
    }

    /**
     * 检查用户是否对某种药物过敏
     */
    @GetMapping("/check")
    public RespResult checkAllergy(
            @PathVariable Integer userId,
            @RequestParam("medicineName") String medicineName,
            HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (!isAuthenticated(loginUser, userId)) {
            return RespResult.fail("未登录或无权访问");
        }

        boolean isAllergic = allergyService.isAllergicTo(userId, medicineName);
        return RespResult.success("检查成功", isAllergic);
    }

    /**
     * 辅助方法：检查用户是否已登录且有权访问该 userId 的资源
     */
    private boolean isAuthenticated(User loginUser, Integer resourceUserId) {
        return loginUser != null && loginUser.getUserId().equals(resourceUserId);
    }
}