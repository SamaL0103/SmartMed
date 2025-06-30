package com.dhu.smartmed.controller;

import com.dhu.smartmed.dto.RespResult;
import com.dhu.smartmed.entity.DiseaseMedicine;
import com.dhu.smartmed.entity.User;
import com.dhu.smartmed.service.DiseaseMedicineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/illness_medicine")
public class IllnessMedicineController {

    @Autowired
    private DiseaseMedicineService diseaseMedicineService;

    /**
     * 保存疾病药品关联关系
     */
    @PostMapping("/save")
    public RespResult saveRelation(
            @RequestParam("illnessId") Integer diseaseId,
            @RequestParam("medicineId") Integer medicineId,
            HttpSession session) {
        
        // 检查用户是否登录并有权限
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            return RespResult.fail("未登录");
        }
        
        if (!loginUser.getIsAdmin()) {
            return RespResult.fail("无权操作");
        }
        
        // 创建关联关系
        DiseaseMedicine relation = DiseaseMedicine.builder()
                .diseaseId(diseaseId)
                .medicineId(medicineId)
                .build();
        
        // 保存关联关系
        if (diseaseMedicineService.addRelation(relation)) {
            return RespResult.success("关联成功");
        } else {
            return RespResult.fail("关联失败");
        }
    }

    /**
     * 删除疾病药品关联关系
     */
    @PostMapping("/delete")
    public RespResult deleteRelation(
            @RequestParam("illnessId") Integer diseaseId,
            @RequestParam("medicineId") Integer medicineId,
            HttpSession session) {
        
        // 检查用户是否登录并有权限
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            return RespResult.fail("未登录");
        }
        
        if (!loginUser.getIsAdmin()) {
            return RespResult.fail("无权操作");
        }
        
        // 删除关联关系
        if (diseaseMedicineService.deleteRelation(diseaseId, medicineId)) {
            return RespResult.success("删除成功");
        } else {
            return RespResult.fail("删除失败");
        }
    }
} 