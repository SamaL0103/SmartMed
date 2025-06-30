package com.dhu.smartmed.controller;

import com.dhu.smartmed.dto.RespResult;
import com.dhu.smartmed.entity.*;
import com.dhu.smartmed.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/message")
public class MessageController {

    @Autowired
    private ChatRecordService chatRecordService;

    @Autowired
    private UserService userService;

    @Autowired
    private AllergyService allergyService;

    @Autowired
    private HistoryService historyService;

    @Autowired
    private DiseaseService diseaseService;

    @Autowired
    private MedicineService medicineService;

    @Autowired
    private MedicinePriceService medicinePriceService;

    @Autowired
    private AIDialogService aiDialogService;

    @Value("${chat.context.size:5}")
    private Integer contextSize;

    private boolean isAdmin(HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        return loginUser != null && loginUser.getIsAdmin();
    }

    @GetMapping("/history")
    public RespResult getChatHistory(HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            return RespResult.fail("未登录");
        }

        List<ChatRecord> chatRecords = chatRecordService.findRecordsByUserId(loginUser.getUserId());
        return RespResult.success("获取成功", chatRecords);
    }

    @GetMapping("/recent")
    public RespResult getRecentChatHistory(
            @RequestParam(value = "limit", defaultValue = "10") Integer limit,
            HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            return RespResult.fail("未登录");
        }

        List<ChatRecord> chatRecords = chatRecordService.findRecentRecordsByUserId(loginUser.getUserId(), limit);
        return RespResult.success("获取成功", chatRecords);
    }

    @PostMapping("/clear")
    public RespResult clearChatHistory(HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            return RespResult.fail("未登录");
        }

        boolean success = chatRecordService.deleteRecordsByUserId(loginUser.getUserId());
        if (success) {
            return RespResult.success("清空成功");
        } else {
            return RespResult.fail("清空失败");
        }
    }

    @PostMapping("/query")
    public RespResult processQuery(
            @RequestParam("content") String content,
            HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        Integer userId = null;

        User user = null;
        List<Allergy> allergies = new ArrayList<>();
        List<History> histories = new ArrayList<>();

        if (loginUser != null) {
            userId = loginUser.getUserId();

            user = userService.getUserById(userId);

            allergies = allergyService.getAllergiesByUserId(userId);
            histories = historyService.getHistoriesByUserId(userId);
        }

        ChatRecord userQuery = ChatRecord.builder()
                .userId(userId)
                .isFromUser(true)
                .content(content)
                .build();

        chatRecordService.saveRecord(userQuery);

        List<ChatRecord> conversation = chatRecordService.getConversation(userId, contextSize);
        List<String> contextMessages = conversation.stream()
                .map(ChatRecord::getContent)
                .collect(Collectors.toList());

        String responseContent;
        if (user != null) {
            responseContent = aiDialogService.getPersonalizedAIResponse(
                    content, user, allergies, histories, contextMessages);
        } else {
            responseContent = aiDialogService.getAIResponse(content, contextMessages);
        }

        ChatRecord systemResponse = ChatRecord.builder()
                .userId(userId)
                .isFromUser(false)
                .content(responseContent)
                .build();

        chatRecordService.saveRecord(systemResponse);

        List<String> diseaseMentions = aiDialogService.extractDiseaseMentions(content);
        List<Disease> relatedDiseases = new ArrayList<>();

        for (String diseaseName : diseaseMentions) {
            List<Disease> diseases = diseaseService.findDiseaseByName(diseaseName);
            if (diseases != null && !diseases.isEmpty()) {
                relatedDiseases.addAll(diseases);
            }
        }

        List<String> medicineMentions = aiDialogService.extractMedicineMentions(content);
        Map<Medicine, List<MedicinePrice>> medicinesWithPrices = new HashMap<>();

        for (String medicineName : medicineMentions) {
            Medicine medicine = medicineService.searchMedicinesByName(medicineName);
            if (medicine != null) {
                List<MedicinePrice> prices = medicinePriceService.getPricesByMedicineId(medicine.getMedicineId());
                medicinesWithPrices.put(medicine, prices);
            }
        }

        Map<String, Object> responseData = new HashMap<>();
        responseData.put("message", responseContent);
        responseData.put("relatedDiseases", relatedDiseases);
        responseData.put("medicinesWithPrices", medicinesWithPrices);

        return RespResult.success("处理成功", responseData);
    }

    @PostMapping("/query-simple")
    public RespResult processSimpleQuery(
            @RequestParam("content") String content,
            HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        Integer userId = null;

        if (loginUser != null) {
            userId = loginUser.getUserId();
        }

        ChatRecord userQuery = ChatRecord.builder()
                .userId(userId)
                .isFromUser(true)
                .content(content)
                .build();

        chatRecordService.saveRecord(userQuery);

        String responseMessage = generateSimpleResponse(content);

        ChatRecord systemResponse = ChatRecord.builder()
                .userId(userId)
                .isFromUser(false)
                .content(responseMessage)
                .build();

        chatRecordService.saveRecord(systemResponse);

        return RespResult.success("处理成功", responseMessage);
    }

    private String generateSimpleResponse(String query) {
        query = query.toLowerCase();

        if (query.contains("你好") || query.contains("hello")) {
            return "您好！很高兴为您服务。请问有什么可以帮助您的吗？";
        }

        if (query.contains("感冒") || query.contains("咳嗽") || query.contains("发烧")) {
            return "感冒是一种常见疾病，症状包括咳嗽、发烧、流鼻涕等。建议多休息，多喝水，必要时服用感冒药。如症状严重，请及时就医。";
        }

        if (query.contains("头痛") || query.contains("头晕")) {
            return "头痛可能由多种原因引起，如疲劳、压力、颈椎问题等。建议休息，放松身心，保持良好的姿势。如持续头痛，请咨询医生。";
        }

        if (query.contains("药") || query.contains("medicine")) {
            return "请详细描述您的症状，以便我们推荐合适的药物。请注意，自行用药前最好咨询医生或药师的建议。";
        }

        return "感谢您的咨询。我们的智能助手正在学习中，如有更多问题，请随时提问。您也可以尝试具体描述您的症状或疑问，我们会尽力提供帮助。";
    }

    @GetMapping("/admin/all")
    public RespResult getAllChatRecords(HttpSession session) {
        if (!isAdmin(session)) {
            return RespResult.fail("无权限访问");
        }

        List<ChatRecord> chatRecords = chatRecordService.findAllRecords();
        return RespResult.success("获取成功", chatRecords);
    }

    @PostMapping("/admin/delete")
    public RespResult deleteChatRecord(
            @RequestParam("recordId") Integer recordId,
            HttpSession session) {
        if (!isAdmin(session)) {
            return RespResult.fail("无权限访问");
        }

        boolean success = chatRecordService.deleteRecord(recordId);
        if (success) {
            return RespResult.success("删除成功");
        } else {
            return RespResult.fail("删除失败");
        }
    }

}