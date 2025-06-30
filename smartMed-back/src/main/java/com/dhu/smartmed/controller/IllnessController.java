package com.dhu.smartmed.controller;

import com.dhu.smartmed.dto.RespResult;
import com.dhu.smartmed.entity.Disease;
import com.dhu.smartmed.entity.Medicine;
import com.dhu.smartmed.entity.User;
import com.dhu.smartmed.service.DiseaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/illness")
public class IllnessController {

    @Autowired
    private DiseaseService diseaseService;

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
     * 保存疾病信息（仅管理员）
     * 支持部分字段更新，字段名与前端匹配
     */
    @PostMapping("/save")
    public RespResult saveIllness(
            @RequestParam(value = "id", required = false) Integer id,
            @RequestParam("illnessName") String name,
            @RequestParam("includeReason") String causes,
            @RequestParam("illnessSymptom") String symptoms,
            @RequestParam("kindId") String category,
            HttpSession session) {
        
        // 检查管理员权限
        RespResult checkResult = checkAdmin(session);
        if (checkResult != null) {
            return checkResult;
        }
        
        // 创建或更新疾病信息
        Disease disease;
        if (id != null && id > 0) {
            // 更新已有疾病
            disease = diseaseService.findDiseaseById(id);
            if (disease == null) {
                return RespResult.fail("疾病不存在");
            }
        } else {
            // 创建新疾病
            disease = new Disease();
        }
        
        // 设置疾病属性
        disease.setName(name);
        disease.setCauses(causes);
        disease.setSymptoms(symptoms);
        disease.setCategory(category);
        
        boolean success;
        if (id != null && id > 0) {
            // 更新疾病
            success = diseaseService.updateDisease(disease);
        } else {
            // 添加新疾病
            success = diseaseService.addDisease(disease);
        }
        
        if (success) {
            return RespResult.success("保存成功");
        } else {
            return RespResult.fail("保存失败");
        }
    }

    /**
     * 删除疾病（仅管理员）
     */
    @PostMapping("/delete")
    public RespResult deleteIllness(
            @RequestParam("id") Integer id,
            HttpSession session) {
        
        // 检查管理员权限
        RespResult checkResult = checkAdmin(session);
        if (checkResult != null) {
            return checkResult;
        }
        
        // 删除疾病
        if (diseaseService.deleteDisease(id)) {
            return RespResult.success("删除成功");
        } else {
            return RespResult.fail("删除失败");
        }
    }

    /**
     * 疾病列表与搜索（所有用户可见）
     * 支持名称,症状和分类搜索
     */
    @GetMapping("/findIllness")
    public RespResult findIllness(
            @RequestParam(value = "illnessName", required = false) String name,
            @RequestParam(value = "kind", required = false) String category,
            @RequestParam(value = "illnessSymptom", required = false) String symptom,
            HttpSession session) {
        List<Disease> diseases = new ArrayList<>();

        // 优先级：名称 > 分类 > 症状
        if (name != null && !name.isEmpty()) {
            diseases = diseaseService.findDiseaseByName(name);
        } else if (category != null && !category.isEmpty()) {
            diseases = diseaseService.findDiseaseByCategory(category);
        } else if (symptom != null && !symptom.isEmpty()) {
            diseases = diseaseService.findDiseaseBySymptom(symptom);
        } else {
            diseases = diseaseService.findAllDiseases();
        }

        return RespResult.success("获取成功", diseases);
    }

    /**
     * 获取单个疾病详情（所有用户可见）
     * 包含关联药品信息，并支持点击药品名跳转
     */
    @GetMapping("/findIllnessOne")
    public RespResult findIllnessOne(
            @RequestParam("id") Integer id,
            HttpSession session) {
        
        // 获取疾病详情
        Disease disease = diseaseService.findDiseaseById(id);
        if (disease == null) {
            return RespResult.fail("疾病不存在");
        }
        
        // 获取相关药品
        List<Medicine> medicines = diseaseService.findRelatedMedicines(id);
        
        // 添加当前用户信息
        User loginUser = (User) session.getAttribute("loginUser");
        
        return RespResult.success("获取成功", disease);
    }
    
    /**
     * 管理员疾病管理页面（仅管理员）
     */
    @GetMapping("/manage")
    public RespResult manageIllness(HttpSession session) {
        // 检查用户是否登录并是管理员
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null || !loginUser.getIsAdmin()) {
            return RespResult.fail("未登录或非管理员");
        }
        
        // 获取所有疾病
        List<Disease> diseases = diseaseService.findAllDiseases();
        return RespResult.success("获取成功", diseases);
    }
    
    /**
     * 管理员添加/编辑疾病页面（仅管理员）
     */
    @GetMapping("/edit")
    public RespResult editIllness(
            @RequestParam(value = "id", required = false) Integer id,
            HttpSession session) {
        
        // 检查用户是否登录并是管理员
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null || !loginUser.getIsAdmin()) {
            return RespResult.fail("未登录或非管理员");
        }
        
        Disease disease = null;
        
        // 如果提供了ID，获取对应疾病信息
        if (id != null && id > 0) {
            disease = diseaseService.findDiseaseById(id);
            if (disease == null) {
                return RespResult.fail("疾病不存在");
            }
        }
        return RespResult.success("获取成功", disease);
    }
    
    /**
     * 获取疾病关联的药品列表（AJAX调用）
     */
    @GetMapping("/relatedMedicines")
    public RespResult getRelatedMedicines(@RequestParam("id") Integer diseaseId) {
        List<Medicine> medicines = diseaseService.findRelatedMedicines(diseaseId);
        return RespResult.success("获取成功", medicines);
    }
} 