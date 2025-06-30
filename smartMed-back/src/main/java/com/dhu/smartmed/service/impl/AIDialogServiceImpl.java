package com.dhu.smartmed.service.impl;

import com.dhu.smartmed.entity.Allergy;
import com.dhu.smartmed.entity.History;
import com.dhu.smartmed.entity.User;
import com.dhu.smartmed.service.AIDialogService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@Service
public class AIDialogServiceImpl implements AIDialogService {

    private static final Logger logger = Logger.getLogger(AIDialogServiceImpl.class.getName());

    @Value("${ai.api.url:https://dashscope-intl.aliyuncs.com/compatible-mode/v1}")
    private String apiUrl;
    
    @Value("${ai.api.key:}")
    private String apiKey;
    
    @Value("${ai.model:qwen2.5-72b-instruct}")
    private String model;
    
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String getAIResponse(String prompt, List<String> contextMessages) {
        try {
            logger.info("开始调用AI API，URL: " + apiUrl);
            logger.info("使用模型: " + model);
            
            // 检查API密钥是否配置
            if (apiKey == null || apiKey.trim().isEmpty()) {
                logger.warning("API密钥未配置，使用备选响应");
                return generateSmartFallbackResponse(prompt, null, null, null, contextMessages);
            }
            
            // 构建请求
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", model);
            
            List<Map<String, String>> messages = new ArrayList<>();
            
            // 添加系统角色消息
            Map<String, String> systemMessage = new HashMap<>();
            systemMessage.put("role", "system");
            systemMessage.put("content", "你是一个专业的医疗助手，请根据用户的问题提供准确、简洁的医疗建议，但请注明你不是医生，重要医疗问题仍需咨询专业医生。");
            messages.add(systemMessage);
            
            // 添加上下文消息
            if (contextMessages != null && !contextMessages.isEmpty()) {
                for (int i = 0; i < contextMessages.size(); i++) {
                    Map<String, String> message = new HashMap<>();
                    message.put("role", i % 2 == 0 ? "user" : "assistant");
                    message.put("content", contextMessages.get(i));
                    messages.add(message);
                }
            }
            
            // 添加当前用户问题
            Map<String, String> userMessage = new HashMap<>();
            userMessage.put("role", "user");
            userMessage.put("content", prompt);
            messages.add(userMessage);
            
            requestBody.put("messages", messages);
            requestBody.put("temperature", 0.7);
            requestBody.put("max_tokens", 1000);
            
            try {
                String result = sendRequest(requestBody);
                if (result != null) {
                    return result;
                }
            } catch (Exception e) {
                logger.warning("API请求失败: " + e.getMessage());
            }
            
            // 如果API请求失败，使用备选响应
            return generateSmartFallbackResponse(prompt, null, null, null, contextMessages);
        } catch (Exception e) {
            logger.warning("AI响应生成异常: " + e.getMessage());
            e.printStackTrace();
            // 异常情况下使用备选响应
            return generateSmartFallbackResponse(prompt, null, null, null, contextMessages);
        }
    }

    @Override
    public String getPersonalizedAIResponse(String prompt, User user, List<Allergy> allergies, List<History> histories, List<String> contextMessages) {
        try {
            logger.info("开始调用个性化AI API");
            
            // 检查API密钥是否配置
            if (apiKey == null || apiKey.trim().isEmpty()) {
                logger.warning("API密钥未配置，使用备选响应");
                return generateSmartFallbackResponse(prompt, user, allergies, histories, contextMessages);
            }
            
            // 生成包含用户健康信息的系统提示
            String systemPrompt = generateSystemPrompt(user, allergies, histories);
            logger.info("生成的系统提示: " + systemPrompt);
            
            // 构建请求
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", model);
            
            List<Map<String, String>> messages = new ArrayList<>();
            
            // 添加系统角色消息
            Map<String, String> systemMessage = new HashMap<>();
            systemMessage.put("role", "system");
            systemMessage.put("content", systemPrompt);
            messages.add(systemMessage);
            
            // 添加上下文消息
            if (contextMessages != null && !contextMessages.isEmpty()) {
                for (int i = 0; i < contextMessages.size(); i++) {
                    Map<String, String> message = new HashMap<>();
                    message.put("role", i % 2 == 0 ? "user" : "assistant");
                    message.put("content", contextMessages.get(i));
                    messages.add(message);
                }
            }
            
            // 添加当前用户问题
            Map<String, String> userMessage = new HashMap<>();
            userMessage.put("role", "user");
            userMessage.put("content", prompt);
            messages.add(userMessage);
            
            requestBody.put("messages", messages);
            requestBody.put("temperature", 0.7);
            requestBody.put("max_tokens", 1500);
            
            try {
                String result = sendRequest(requestBody);
                if (result != null) {
                    // 提取可能的疾病和药品
                    List<String> diseases = extractDiseaseMentions(prompt + " " + result);
                    List<String> medicines = extractMedicineMentions(prompt + " " + result);
                    
                    // 存储识别的疾病和药品
                    ThreadLocalStorage.setDiseases(diseases);
                    ThreadLocalStorage.setMedicines(medicines);
                    
                    return result;
                }
            } catch (Exception e) {
                logger.warning("个性化API请求失败: " + e.getMessage());
            }
            
            // 如果API请求失败，使用备选响应
            return generateSmartFallbackResponse(prompt, user, allergies, histories, contextMessages);
        } catch (Exception e) {
            logger.warning("个性化AI响应生成异常: " + e.getMessage());
            e.printStackTrace();
            // 异常情况下使用备选响应
            return generateSmartFallbackResponse(prompt, user, allergies, histories, contextMessages);
        }
    }
    
    /**
     * 发送请求到AI API并处理响应
     */
    private String sendRequest(Map<String, Object> requestBody) throws Exception {
        // 设置请求头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + apiKey);
        
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
        
        // 发送请求
        String endpoint = apiUrl;
        if (!endpoint.endsWith("/chat/completions")) {
            if (!endpoint.endsWith("/")) {
                endpoint += "/";
            }
            endpoint += "chat/completions";
        }
        
        logger.info("发送API请求到: " + endpoint);
        ResponseEntity<Map> response = restTemplate.postForEntity(endpoint, request, Map.class);
        logger.info("API响应状态码: " + response.getStatusCodeValue());
        
        // 解析响应
        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            Map<String, Object> responseBody = response.getBody();
            
            List<Map<String, Object>> choices = (List<Map<String, Object>>) responseBody.get("choices");
            
            if (choices != null && !choices.isEmpty()) {
                Map<String, Object> choice = choices.get(0);
                Map<String, String> message = (Map<String, String>) choice.get("message");
                
                if (message != null && message.containsKey("content")) {
                    return message.get("content");
                }
            }
            
            // 检查是否有错误信息
            if (responseBody.containsKey("error")) {
                Map<String, Object> error = (Map<String, Object>) responseBody.get("error");
                String errorMessage = "API错误: " + error.get("message");
                logger.warning(errorMessage);
                throw new RuntimeException(errorMessage);
            }
        }
        
        return null;
    }

    @Override
    public List<String> extractDiseaseMentions(String content) {
        // 简单实现：从内置的疾病列表中检查匹配项
        // 在实际项目中，这里应该使用更复杂的NLP方法或者调用专门的疾病实体识别服务
        List<String> commonDiseases = Arrays.asList(
            "感冒", "流感", "新冠", "肺炎", "糖尿病", "高血压", "心脏病", "中风", 
            "哮喘", "支气管炎", "肺结核", "肝炎", "胃炎", "肠炎", "胃溃疡", 
            "十二指肠溃疡", "结肠炎", "痔疮", "关节炎", "骨质疏松", "皮炎", 
            "湿疹", "荨麻疹", "脱发", "头痛", "偏头痛", "失眠", "抑郁症", "焦虑症",
            "颈椎病", "腰椎间盘突出", "腰痛", "关节痛"
        );
        
        List<String> foundDiseases = new ArrayList<>();
        for (String disease : commonDiseases) {
            if (content.toLowerCase().contains(disease.toLowerCase())) {
                foundDiseases.add(disease);
            }
        }
        
        return foundDiseases;
    }

    @Override
    public List<String> extractMedicineMentions(String content) {
        // 简单实现：从内置的药品列表中检查匹配项
        // 在实际项目中，这里应该使用更复杂的NLP方法或者调用专门的药品实体识别服务
        List<String> commonMedicines = Arrays.asList(
            "阿司匹林", "布洛芬", "扑热息痛", "对乙酰氨基酚", "阿莫西林", "青霉素", 
            "头孢菌素", "克拉霉素", "红霉素", "甲硝唑", "左氧氟沙星", "氯雷他定", 
            "西替利嗪", "苯海拉明", "氢氯噻嗪", "卡托普利", "硝苯地平", "维拉帕米", 
            "辛伐他汀", "氟伐他汀", "二甲双胍", "胰岛素", "格列美脲", "双氯芬酸", 
            "布洛芬", "塞来昔布", "奥美拉唑", "雷尼替丁", "法莫替丁", "多潘立酮",
            "感冒药", "止痛药", "消炎药", "抗生素", "抗过敏药", "降压药", "降糖药",
            "维生素", "钙片", "葡萄糖", "退烧药"
        );
        
        List<String> foundMedicines = new ArrayList<>();
        for (String medicine : commonMedicines) {
            if (content.toLowerCase().contains(medicine.toLowerCase())) {
                foundMedicines.add(medicine);
            }
        }
        
        return foundMedicines;
    }

    @Override
    public String generateSystemPrompt(User user, List<Allergy> allergies, List<History> histories) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("你是一个专业的医疗助手，请根据用户的问题提供准确、简洁的医疗建议，但请注明你不是医生，重要医疗问题仍需咨询专业医生。\n\n");
        
        // 添加用户基本信息
        if (user != null) {
            prompt.append("用户信息：\n");
            prompt.append("- 年龄：").append(user.getAge()).append("岁\n");
            prompt.append("- 性别：").append(user.getGender() == null ? "未知" : user.getGender()).append("\n");
        }
        
        // 添加过敏信息
        if (allergies != null && !allergies.isEmpty()) {
            prompt.append("\n用户过敏信息：\n");
            for (Allergy allergy : allergies) {
                prompt.append("- 过敏源：").append(allergy.getAllergen());
                if (allergy.getDescription() != null && !allergy.getDescription().isEmpty()) {
                    prompt.append("，描述：").append(allergy.getDescription());
                }
                prompt.append("\n");
            }
        }
        
        // 添加病史信息
        if (histories != null && !histories.isEmpty()) {
            prompt.append("\n用户病史信息：\n");
            for (History history : histories) {
                prompt.append("- 疾病：").append(history.getDiseaseName());
                if (history.getDescription() != null && !history.getDescription().isEmpty()) {
                    prompt.append("，描述：").append(history.getDescription());
                }
                prompt.append("\n");
            }
        }
        
        prompt.append("\n在回答用户的问题时，请考虑以上用户信息，特别是过敏史和病史。如果用户的问题涉及到药物，请特别注意用户的过敏情况。");
        prompt.append("如果发现用户询问的内容可能与其病史有关，请提供更有针对性的建议。");
        prompt.append("\n\n回答要简洁、易懂，避免医学术语过多，使普通人能够理解。回答中要主动提到用户的病史和过敏情况。");
        
        return prompt.toString();
    }
    
    /**
     * 生成智能备选响应，参考用户的病史和过敏情况
     */
    private String generateSmartFallbackResponse(String query, User user, List<Allergy> allergies, List<History> histories, List<String> contextMessages) {
        logger.info("使用智能备选响应方式回答: " + query);
        query = query.toLowerCase();
        
        // 构建响应前缀
        StringBuilder responsePrefix = new StringBuilder();
        if (user != null) {
            responsePrefix.append("根据您的个人信息");
            if (user.getAge() > 0) {
                responsePrefix.append("（").append(user.getAge()).append("岁");
                if (user.getGender() != null && !"".equals(user.getGender().toString())) {
                    responsePrefix.append("，").append(user.getGender());
                }
                responsePrefix.append("）");
            }
            responsePrefix.append("，");
        }
        
        // 检查是否与病史或过敏相关
        boolean mentionedHistory = false;
        if (histories != null && !histories.isEmpty()) {
            for (History history : histories) {
                if (query.contains(history.getDiseaseName().toLowerCase())) {
                    return responsePrefix.toString() + "关于您提到的" + history.getDiseaseName() + "，我注意到这在您的病史记录中。" +
                           "根据您的记录描述：\"" + history.getDescription() + "\"。我建议您定期随访，保持良好的生活习惯。" +
                           "如有任何不适，请及时就医。\n\n请注意，我只能提供一般性建议，具体治疗方案请遵医嘱。";
                }
            }
        }
        
        if (allergies != null && !allergies.isEmpty()) {
            for (Allergy allergy : allergies) {
                if (query.contains(allergy.getAllergen().toLowerCase())) {
                    return responsePrefix.toString() + "您提到的" + allergy.getAllergen() + "在您的过敏史中有记录。" +
                           "过敏描述：\"" + allergy.getDescription() + "\"。建议您避免接触此过敏原，如不慎接触出现过敏反应，请立即就医。\n\n" +
                           "请注意，我只能提供一般性建议，具体处理方法请咨询专业医生。";
                }
            }
        }
        
        // 常见问题的智能回复
        if (query.contains("你好") || query.contains("hello")) {
            return "您好！我是您的医疗助手。很高兴为您服务，请问有什么可以帮助您的吗？" + 
                (histories != null && !histories.isEmpty() ? "\n\n我注意到您有" + histories.size() + "条病史记录和" + 
                 (allergies != null ? allergies.size() : 0) + "条过敏记录，如果需要了解相关健康建议，可以随时告诉我。" : "");
        }
        
        if (query.contains("感冒") || query.contains("咳嗽") || query.contains("发烧")) {
            return responsePrefix.toString() + "感冒是一种常见的病毒性感染，症状包括咳嗽、发烧、流鼻涕等。建议您：\n\n" +
                "1. 多休息，保持充分睡眠\n" +
                "2. 多喝温水，保持体内水分\n" +
                "3. 可适当服用感冒药缓解症状\n" +
                "4. 如发热超过38.5℃或症状持续超过一周，请及时就医\n\n" +
                "请注意，我只能提供一般性建议，具体用药和治疗方案请咨询医生。";
        }
        
        if (query.contains("头痛") || query.contains("头晕")) {
            return responsePrefix.toString() + "头痛可能由多种原因引起，如疲劳、压力、颈椎问题等。建议您：\n\n" +
                "1. 适当休息，避免长时间用眼和伏案工作\n" +
                "2. 保持规律作息，减轻精神压力\n" +
                "3. 保持颈部放松，可做颈部轻柔按摩\n" +
                "4. 如头痛剧烈、频繁发作或伴随其他症状，请及时就医\n\n" +
                "请注意，我只能提供一般性建议，如果您的症状严重或反复发作，请及时就医。";
        }
        
        if (query.contains("药") || query.contains("medicine")) {
            return responsePrefix.toString() + "关于药物的建议，我需要了解更多您的具体症状和情况。请注意：\n\n" +
                "1. 药物应在医生或药师指导下使用\n" +
                "2. 遵循医嘱的剂量和服用时间\n" +
                "3. 关注可能的药物过敏和副作用\n" +
                (allergies != null && !allergies.isEmpty() ? "4. 特别提醒您注意自身的过敏史记录\n" : "") +
                "\n如果您有特定的药物问题，请提供更详细的信息，我会尽力协助您。";
        }
        
        if (query.contains("病史") || query.contains("疾病史")) {
            if (histories != null && !histories.isEmpty()) {
                StringBuilder historyResponse = new StringBuilder(responsePrefix.toString() + "根据您的记录，您的病史包括：\n\n");
                for (History history : histories) {
                    historyResponse.append("- ").append(history.getDiseaseName());
                    if (history.getDescription() != null && !history.getDescription().isEmpty()) {
                        historyResponse.append("：").append(history.getDescription());
                    }
                    historyResponse.append("\n");
                }
                historyResponse.append("\n定期体检和随访对管理这些健康问题很重要。如有任何不适，请及时就医。");
                return historyResponse.toString();
            } else {
                return "您的记录中暂无病史信息。保持健康的生活方式和定期体检非常重要。如果您有任何健康问题，请随时告诉我，我会尽力提供帮助。";
            }
        }
        
        if (query.contains("过敏") || query.contains("allergy")) {
            if (allergies != null && !allergies.isEmpty()) {
                StringBuilder allergyResponse = new StringBuilder(responsePrefix.toString() + "根据您的记录，您的过敏情况包括：\n\n");
                for (Allergy allergy : allergies) {
                    allergyResponse.append("- 过敏源：").append(allergy.getAllergen());
                    if (allergy.getDescription() != null && !allergy.getDescription().isEmpty()) {
                        allergyResponse.append("，描述：").append(allergy.getDescription());
                    }
                    allergyResponse.append("\n");
                }
                allergyResponse.append("\n建议您避免接触这些过敏原，并随身携带过敏药物。如发生严重过敏反应，请立即就医。");
                return allergyResponse.toString();
            } else {
                return "您的记录中暂无过敏信息。如果您对某些食物、药物或环境因素有过敏反应，请告诉我，这对于提供适合您的健康建议很重要。";
            }
        }
        
        // 默认回复，针对未匹配的问题
        return responsePrefix.toString() + "我理解您的问题是关于「" + query + "」。作为医疗助手，我可以提供一般性的健康信息，但请注意我不是医生，无法替代专业医疗诊断。\n\n" +
            "请提供更多细节，比如您的具体症状、持续时间等，我会尽力提供更有针对性的建议。如有严重健康问题，请及时就医。" +
            (histories != null && !histories.isEmpty() ? "\n\n考虑到您的病史记录，建议您在咨询医生时提及这些信息。" : "");
    }
    
    /**
     * 用于存储线程局部变量，保存识别的疾病和药品信息
     */
    private static class ThreadLocalStorage {
        private static final ThreadLocal<List<String>> DISEASES = new ThreadLocal<>();
        private static final ThreadLocal<List<String>> MEDICINES = new ThreadLocal<>();
        
        public static void setDiseases(List<String> diseases) {
            DISEASES.set(diseases != null ? diseases : Collections.emptyList());
        }
        
        public static List<String> getDiseases() {
            return DISEASES.get();
        }
        
        public static void setMedicines(List<String> medicines) {
            MEDICINES.set(medicines != null ? medicines : Collections.emptyList());
        }
        
        public static List<String> getMedicines() {
            return MEDICINES.get();
        }
        
        public static void clear() {
            DISEASES.remove();
            MEDICINES.remove();
        }
    }
} 