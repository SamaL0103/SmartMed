package com.dhu.smartmed.controller;

import com.dhu.smartmed.dto.RespResult;
import com.dhu.smartmed.entity.Medicine;
import com.dhu.smartmed.entity.User;
import com.dhu.smartmed.service.MedicineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.util.List;

@RestController
@RequestMapping("/api/medicine")
public class MedicineController {

    @Autowired
    private MedicineService medicineService;

    /**
     * 检查是否是管理员
     */
    private RespResult checkAdmin(HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            return RespResult.fail("未登录");
        }

        if (!loginUser.getIsAdmin()) {
            return RespResult.fail("只有管理员可以操作");
        }

        return null; // 表示验证通过
    }

    /**
     * 保存药品信息（仅管理员）
     * 支持部分字段更新，字段名与前端匹配
     */
    @PostMapping("/save")
    public RespResult saveMedicine(
            @RequestParam(value = "id", required = false) Integer id,
            @RequestParam("name") String name,
            @RequestParam("category") String category,
            @RequestParam("efficacy") String efficacy,
            @RequestParam("usageMethod") String usageMethod,
            @RequestParam("contraindications") String contraindications,
            @RequestParam("sideEffects") String sideEffects,
            HttpSession session) {

        // 检查管理员权限
        RespResult checkResult = checkAdmin(session);
        if (checkResult != null) {
            return checkResult;
        }

        // 创建或更新药品信息
        Medicine medicine;
        if (id != null && id > 0) {
            // 更新已有药品
            medicine = medicineService.getMedicineById(id);
            if (medicine == null) {
                return RespResult.fail("药品不存在");
            }
        } else {
            // 创建新药品
            medicine = new Medicine();
        }

        // 设置药品属性
        medicine.setName(name);
        medicine.setCategory(category);
        medicine.setEfficacy(efficacy);
        medicine.setUsageMethod(usageMethod);
        medicine.setContraindications(contraindications);
        medicine.setSideEffects(sideEffects);

        boolean success;
        if (id != null && id > 0) {
            // 更新药品
            success = medicineService.updateMedicine(id, medicine);
        } else {
            // 添加新药品
            success = medicineService.addMedicine(medicine);
        }

        if (success) {
            return RespResult.success("保存成功");
        } else {
            return RespResult.fail("保存失败");
        }
    }

    /**
     * 删除药品（仅管理员）
     */
    @PostMapping("/delete")
    public RespResult deleteMedicine(
            @RequestParam("id") Integer id,
            HttpSession session) {

        // 检查管理员权限
        RespResult checkResult = checkAdmin(session);
        if (checkResult != null) {
            return checkResult;
        }

        // 删除药品
        if (medicineService.deleteMedicine(id)) {
            return RespResult.success("删除成功");
        } else {
            return RespResult.fail("删除失败");
        }
    }

    /**
     * 药品列表与搜索（所有用户可见）
     * 支持名称、作用和分类搜索
     */
    @GetMapping("/findMedicines")
    public RespResult findMedicines(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "efficacy", required = false) String efficacy,
            @RequestParam(value = "keyword", required = false) String keyword,
            HttpSession session) {
        List<Medicine> medicines;
        if (name != null && !name.isEmpty()) {
            medicines = medicineService.searchMedicines(name);
        } else if (efficacy != null && !efficacy.isEmpty()) {
            medicines = medicineService.searchMedicinesByEfficiacy(efficacy);
        } else if (keyword != null && !keyword.isEmpty()) {
            // 全局关键词查name+efficacy
            List<Medicine> byName = medicineService.searchMedicines(keyword);
            List<Medicine> byEfficacy = medicineService.searchMedicinesByEfficiacy(keyword);
            medicines = new java.util.ArrayList<>(byName);
            for (Medicine m : byEfficacy) {
                boolean exists = medicines.stream().anyMatch(x -> x.getMedicineId().equals(m.getMedicineId()));
                if (!exists) {
                    medicines.add(m);
                }
            }
        } else {
            medicines = medicineService.getAllMedicines();
        }
        return RespResult.success("获取成功", medicines);
    }

    /**
     * 获取单个药品详情（所有用户可见）
     */
    @GetMapping("/findMedicineOne")
    public RespResult findMedicineOne(
            @RequestParam("id") Integer id,
            HttpSession session) {

        // 获取药品详情
        Medicine medicine = medicineService.getMedicineById(id);
        if (medicine == null) {
            return RespResult.fail("药品不存在");
        }

        return RespResult.success("获取成功", medicine);
    }

    /**
     * 管理员药品管理页面（仅管理员）
     */
    @GetMapping("/manage")
    public RespResult manageMedicines(HttpSession session) {
        // 检查用户是否登录并是管理员
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null || !loginUser.getIsAdmin()) {
            return RespResult.fail("未登录或非管理员");
        }

        // 获取所有药品
        List<Medicine> medicines = medicineService.getAllMedicines();

        return RespResult.success("获取成功", medicines);
    }

    /**
     * 管理员编辑药品页面（仅管理员）
     */
    @GetMapping("/edit")
    public RespResult editMedicine(
            @RequestParam(value = "id", required = false) Integer id,
            HttpSession session) {

        // 检查用户是否登录并是管理员
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null || !loginUser.getIsAdmin()) {
            return RespResult.fail("未登录或非管理员");
        }

        Medicine medicine = null;

        // 如果提供了ID，获取对应药品信息
        if (id != null && id > 0) {
            medicine = medicineService.getMedicineById(id);
            if (medicine == null) {
                return RespResult.fail("药品不存在");
            }
        }

        return RespResult.success("获取成功", medicine);
    }
}