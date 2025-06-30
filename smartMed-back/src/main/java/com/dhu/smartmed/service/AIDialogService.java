package com.dhu.smartmed.service;

import com.dhu.smartmed.entity.Allergy;
import com.dhu.smartmed.entity.History;
import com.dhu.smartmed.entity.User;

import java.util.List;
import java.util.Map;

public interface AIDialogService {
    /**
     * 调用外部AI接口获取回复
     * @param prompt 用户输入的问题
     * @param contextMessages 上下文消息列表
     * @return AI回复内容
     */
    String getAIResponse(String prompt, List<String> contextMessages);
    
    /**
     * 使用用户信息获取个性化AI回复
     * @param prompt 用户输入的问题
     * @param user 用户信息
     * @param allergies 用户过敏信息
     * @param histories 用户病史信息
     * @param contextMessages 上下文消息列表
     * @return AI回复内容
     */
    String getPersonalizedAIResponse(String prompt, 
                                    User user, 
                                    List<Allergy> allergies, 
                                    List<History> histories, 
                                    List<String> contextMessages);
    
    /**
     * 从对话内容中提取可能提及的疾病
     * @param content 对话内容
     * @return 疾病名称列表
     */
    List<String> extractDiseaseMentions(String content);
    
    /**
     * 从对话内容中提取可能提及的药品
     * @param content 对话内容
     * @return 药品名称列表
     */
    List<String> extractMedicineMentions(String content);
    
    /**
     * 根据提示词生成包含用户健康信息的系统提示
     * @param user 用户信息
     * @param allergies 用户过敏信息
     * @param histories 用户病史信息
     * @return 包含用户健康信息的系统提示
     */
    String generateSystemPrompt(User user, List<Allergy> allergies, List<History> histories);
} 