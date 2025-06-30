package com.dhu.smartmed.controller;

import com.dhu.smartmed.dto.RespResult;
import com.dhu.smartmed.entity.MedicinePrice;
import com.dhu.smartmed.entity.User;
import com.dhu.smartmed.service.MedicinePriceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.util.List;

@RestController
@RequestMapping("/api/medicinePrice")
public class MedicinePriceController {

    @Autowired
    private MedicinePriceService medicinePriceService;


    /**
     * 删除药品价格（根据药品 ID）
     */
    @PostMapping("/deleteByMedicineId")
    public RespResult deletePricesByMedicineId(
            @RequestParam("medicineId") Integer medicineId,
            HttpSession session) {
        // 检查用户是否登录并是管理员
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null || !loginUser.getIsAdmin()) {
            return RespResult.fail("未登录或无权限");
        }

        // 删除药品价格
        if (medicinePriceService.deletePricesByMedicineId(medicineId)) {
            return RespResult.success("药品价格删除成功");
        } else {
            return RespResult.fail("药品价格删除失败");
        }
    }

    
    /**
     * 获取药品价格列表（根据药品 ID）- RESTful API
     * 为前端AJAX调用提供JSON响应
     */
    @GetMapping("/price/{id}")
    public RespResult getPricesByMedicineIdRest(
            @PathVariable("id") Integer medicineId) {
        try {
            // 获取药品价格列表
            List<MedicinePrice> prices = medicinePriceService.getPricesByMedicineId(medicineId);
            return RespResult.success("获取成功", prices);
        } catch (Exception e) {
            return RespResult.fail("获取药品价格失败: " + e.getMessage());
        }
    }
}