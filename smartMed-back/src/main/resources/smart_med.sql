-- Create the database
CREATE DATABASE smart_med CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE smart_med;

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0; -- 暂时禁用外键约束，方便数据插入

-- 用户表（包含基本信息和状态）
CREATE TABLE users (
                       user_id INT AUTO_INCREMENT PRIMARY KEY,
                       username VARCHAR(50) NOT NULL,
                       password VARCHAR(255) NOT NULL,
                       phone VARCHAR(20),
                       age INT,
                       gender ENUM('Male', 'Female'),
                       avatar VARCHAR(255),
                       is_admin BOOLEAN DEFAULT FALSE,
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 用户病史表
CREATE TABLE user_medical_history (
                                      history_id INT AUTO_INCREMENT PRIMARY KEY,
                                      user_id INT NOT NULL,
                                      disease_name VARCHAR(100) NOT NULL,
                                      description TEXT,
                                      FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- 用户过敏表
CREATE TABLE user_allergies (
                                allergy_id INT AUTO_INCREMENT PRIMARY KEY,
                                user_id INT NOT NULL,
                                allergen VARCHAR(100) NOT NULL,
                                description TEXT,
                                FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 疾病表
CREATE TABLE diseases (
                          disease_id INT AUTO_INCREMENT PRIMARY KEY,
                          name VARCHAR(100) NOT NULL,
                          category VARCHAR(100),
                          causes TEXT,
                          symptoms TEXT
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 药品表
CREATE TABLE medicines (
                           medicine_id INT AUTO_INCREMENT PRIMARY KEY,
                           name VARCHAR(100) NOT NULL,
                           category VARCHAR(100) COMMENT '药品分类',
                           efficacy TEXT COMMENT '药品作用',
                           usage_method TEXT COMMENT '药品使用说明',
                           contraindications TEXT COMMENT '药品禁忌症说明',
                           side_effects TEXT COMMENT '药品副作用说明'
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 疾病药品关联表
CREATE TABLE disease_medicine (
                                  relation_id INT AUTO_INCREMENT PRIMARY KEY,
                                  disease_id INT NOT NULL,
                                  medicine_id INT NOT NULL,
                                  FOREIGN KEY (disease_id) REFERENCES diseases(disease_id) ON DELETE CASCADE,
                                  FOREIGN KEY (medicine_id) REFERENCES medicines(medicine_id) ON DELETE CASCADE
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 药品价格表
CREATE TABLE medicine_prices (
                                 price_id INT AUTO_INCREMENT PRIMARY KEY,
                                 medicine_id INT NOT NULL,
                                 store_name VARCHAR(100) NOT NULL,
                                 price DECIMAL(10, 2) NOT NULL,
                                 specification VARCHAR(100) COMMENT '药品规格说明',
                                 url VARCHAR(255),
                                 FOREIGN KEY (medicine_id) REFERENCES medicines(medicine_id) ON DELETE CASCADE
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 聊天记录表
CREATE TABLE chat_records (
                              chat_id INT AUTO_INCREMENT PRIMARY KEY,
                              user_id INT NOT NULL,
                              is_from_user BOOLEAN DEFAULT TRUE,
                              content TEXT NOT NULL,
                              created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                              FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 反馈表
CREATE TABLE feedback (
                          feedback_id INT AUTO_INCREMENT PRIMARY KEY,
                          user_id INT,
                          content TEXT NOT NULL,
                          status ENUM('Pending', 'InProgress', 'Resolved') DEFAULT 'Pending',
                          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE SET NULL
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 系统统计表（用于仪表盘）
CREATE TABLE dashboard_statistics (
                                      stat_id INT AUTO_INCREMENT PRIMARY KEY,
                                      stat_name VARCHAR(50) NOT NULL UNIQUE,
                                      stat_value INT NOT NULL,
                                      last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 创建自动更新统计的存储过程
DELIMITER //
CREATE PROCEDURE update_dashboard_statistics()
BEGIN
    -- 如果统计记录不存在则插入，存在则更新
    INSERT INTO dashboard_statistics (stat_name, stat_value) VALUES
        ('total_users', (SELECT COUNT(*) FROM users))
    ON DUPLICATE KEY UPDATE stat_value = (SELECT COUNT(*) FROM users);

    INSERT INTO dashboard_statistics (stat_name, stat_value) VALUES
        ('total_diseases', (SELECT COUNT(*) FROM diseases))
    ON DUPLICATE KEY UPDATE stat_value = (SELECT COUNT(*) FROM diseases);

    INSERT INTO dashboard_statistics (stat_name, stat_value) VALUES
        ('total_medicines', (SELECT COUNT(*) FROM medicines))
    ON DUPLICATE KEY UPDATE stat_value = (SELECT COUNT(*) FROM medicines);

    INSERT INTO dashboard_statistics (stat_name, stat_value) VALUES
        ('total_chats', (SELECT COUNT(*) FROM chat_records))
    ON DUPLICATE KEY UPDATE stat_value = (SELECT COUNT(*) FROM chat_records);

    INSERT INTO dashboard_statistics (stat_name, stat_value) VALUES
        ('pending_feedback', (SELECT COUNT(*) FROM feedback WHERE status = 'Pending'))
    ON DUPLICATE KEY UPDATE stat_value = (SELECT COUNT(*) FROM feedback WHERE status = 'Pending');

    INSERT INTO dashboard_statistics (stat_name, stat_value) VALUES
        ('in_progress_feedback', (SELECT COUNT(*) FROM feedback WHERE status = 'In Progress'))
    ON DUPLICATE KEY UPDATE stat_value = (SELECT COUNT(*) FROM feedback WHERE status = 'In Progress');

    INSERT INTO dashboard_statistics (stat_name, stat_value) VALUES
        ('resolved_feedback', (SELECT COUNT(*) FROM feedback WHERE status = 'Resolved'))
    ON DUPLICATE KEY UPDATE stat_value = (SELECT COUNT(*) FROM feedback WHERE status = 'Resolved');
END //
DELIMITER ;

-- 创建每日自动更新事件
DELIMITER //
CREATE EVENT daily_stats_update
    ON SCHEDULE EVERY 1 DAY
        STARTS CURRENT_TIMESTAMP
    DO
    BEGIN
        CALL update_dashboard_statistics();
    END //
DELIMITER ;

-- 确保事件调度器开启
SET GLOBAL event_scheduler = ON;

-- 初始化统计数据（立即执行一次更新）
CALL update_dashboard_statistics();

-- 创建必要的索引
CREATE INDEX idx_disease_name ON diseases(name);
CREATE INDEX idx_medicine_name ON medicines(name);
CREATE INDEX idx_medicine_price ON medicine_prices(medicine_id, price);

-- 插入测试数据
-- 插入用户数据
INSERT INTO users (username, password, phone, age, gender, is_admin) VALUES
                                                                         ('admin', '123456', '13800138000', 35, 'Male', 1),
                                                                         ('张三', '123456', '13900139000', 42, 'Male', 0),
                                                                         ('李四', '123456', '13700137000', 28, 'Female', 0),
                                                                         ('王五', '123456', '13600136000', 56, 'Male', 0),
                                                                         ('赵六', '123456', '13500135000', 33, 'Female', 0);

-- 插入用户病史数据
INSERT INTO user_medical_history (user_id, disease_name, description) VALUES
                                                                          (2, '高血压', '轻度高血压，需定期服用降压药'),
                                                                          (2, '糖尿病', '2型糖尿病，需控制饮食'),
                                                                          (3, '过敏性鼻炎', '春季花粉过敏，症状较明显'),
                                                                          (4, '胃溃疡', '需避免辛辣刺激性食物'),
                                                                          (5, '关节炎', '右膝关节疼痛，天气变化时加重');

-- 插入用户过敏数据
INSERT INTO user_allergies (user_id, allergen, description) VALUES
                                                                (2, '青霉素', '使用后会出现皮疹和呼吸困难'),
                                                                (3, '花粉', '接触后打喷嚏、流鼻涕'),
                                                                (3, '海鲜', '食用后出现轻微皮疹'),
                                                                (4, '阿司匹林', '服用后皮肤瘙痒'),
                                                                (5, '乳制品', '食用后出现腹痛、腹泻');

-- 插入疾病数据
INSERT INTO diseases (name, category, causes, symptoms) VALUES
                                                            ('普通感冒', '呼吸系统疾病', '病毒感染，主要是鼻病毒、冠状病毒等', '咳嗽、流涕、喷嚏、咽痛、低热'),
                                                            ('流行性感冒', '呼吸系统疾病', '由流感病毒引起的急性呼吸道感染', '高热、全身酸痛、咳嗽、流涕'),
                                                            ('过敏性鼻炎', '免疫系统疾病', '接触过敏原后引起的鼻腔黏膜炎症', '打喷嚏、鼻塞、流涕、鼻痒'),
                                                            ('高血压', '心血管疾病', '遗传因素、不良生活习惯等', '头痛、头晕、耳鸣、颈部不适'),
                                                            ('糖尿病', '代谢性疾病', '胰岛素分泌不足或作用障碍', '多饮、多食、多尿、体重减轻'),
                                                            ('支气管炎', '呼吸系统疾病', '细菌或病毒感染引起的支气管黏膜炎症', '咳嗽、咳痰、胸闷、气短'),
                                                            ('胃炎', '消化系统疾病', '饮食不规律、药物刺激等导致的胃黏膜炎症', '上腹痛、恶心、呕吐、食欲不振'),
                                                            ('结膜炎', '眼科疾病', '细菌、病毒感染或过敏原刺激', '眼红、眼痒、有分泌物、畏光'),
                                                            ('牙周炎', '口腔疾病', '细菌感染导致的牙龈和牙周组织炎症', '牙龈红肿、出血、疼痛、口臭'),
                                                            ('头痛', '神经系统症状', '多种原因如压力、疲劳、血管扩张等', '头部疼痛、头晕、恶心');

-- 插入药品数据
INSERT INTO medicines (name, category, efficacy, usage_method, contraindications, side_effects) VALUES
                                                                                                    ('感冒灵颗粒', '非处方药', '缓解感冒引起的头痛、发热、鼻塞、流涕、咽痛等症状', '开水冲服，一次1袋，一日3次', '肝肾功能不全者慎用，孕妇禁用', '可能出现皮疹、瘙痒、嗜睡等'),
                                                                                                    ('布洛芬片', '非处方药', '缓解轻至中度疼痛如头痛、关节痛、偏头痛、牙痛、肌肉痛、神经痛及感冒发热', '口服，一次1-2片，一日3次', '对布洛芬过敏者禁用，胃溃疡活动期患者禁用', '可能出现胃肠道反应、皮疹等'),
                                                                                                    ('氯雷他定片', '非处方药', '缓解过敏性鼻炎、慢性荨麻疹等过敏症状', '口服，一次1片，一日1次', '对本品过敏者禁用', '可能出现口干、头痛、嗜睡等'),
                                                                                                    ('阿莫西林胶囊', '处方药', '治疗敏感菌所致的上呼吸道感染、下呼吸道感染、泌尿生殖道感染等', '口服，一次500mg，一日3次', '对青霉素类药物过敏者禁用', '可能出现恶心、呕吐、腹泻等'),
                                                                                                    ('奥美拉唑肠溶胶囊', '处方药', '用于胃溃疡、十二指肠溃疡、反流性食管炎等', '口服，一次20mg，一日1-2次', '对奥美拉唑过敏者禁用', '可能出现头痛、腹泻、恶心等'),
                                                                                                    ('硝苯地平缓释片', '处方药', '用于高血压、心绞痛等', '口服，一次10-20mg，一日2次', '低血压者禁用，肝功能不全者慎用', '可能出现头痛、面色潮红、心悸等'),
                                                                                                    ('二甲双胍片', '处方药', '用于2型糖尿病', '口服，一次0.5g，一日3次', '肾功能不全者禁用', '可能出现恶心、呕吐、腹泻等胃肠道反应'),
                                                                                                    ('复方氨酚烷胺胶囊', '非处方药', '缓解普通感冒及流行性感冒引起的发热、头痛、四肢酸痛、打喷嚏、流鼻涕、鼻塞、咽痛等症状', '口服，一次1粒，一日3次', '高血压、冠心病患者慎用', '可能出现嗜睡、口干、眩晕等'),
                                                                                                    ('盐酸左西替利嗪片', '处方药', '用于过敏性鼻炎、荨麻疹等过敏性疾病', '口服，一次5mg，一日1次', '对左西替利嗪过敏者禁用', '可能出现嗜睡、口干、疲劳等'),
                                                                                                    ('复方甘草片', '非处方药', '用于咳嗽、咽喉疼痛', '口服，一次2-4片，一日3次', '高血压患者慎用', '可能出现恶心、食欲不振等');


-- 插入疾病药品关联数据
INSERT INTO disease_medicine (disease_id, medicine_id) VALUES
                                                           (1, 1), (1, 2), (1, 8), -- 普通感冒相关药物
                                                           (2, 1), (2, 2), (2, 8), -- 流行性感冒相关药物
                                                           (3, 3), (3, 9), -- 过敏性鼻炎相关药物
                                                           (4, 6), -- 高血压相关药物
                                                           (5, 7), -- 糖尿病相关药物
                                                           (6, 4), (6, 10), -- 支气管炎相关药物
                                                           (7, 5); -- 胃炎相关药物

-- 插入药品价格数据
INSERT INTO medicine_prices (medicine_id, store_name, price, specification, url) VALUES
                                                                                     (1, '安徽御元堂大药房', 45.00, '14g*15袋', 'https://www.315jiage.cn/mn150133.aspx'),
                                                                                     (1, '朝阳成大方圆益峰药店', 28.50, '14g*15袋', 'https://www.315jiage.cn/mn150133.aspx'),
                                                                                     (1, '新华济平大药房', 34.00, '14g*15袋', 'https://www.315jiage.cn/mn150133.aspx'),
                                                                                     (2, '广州卓馨药业有限公司', 7.82, '0.3g*20粒', 'https://www.315jiage.cn/mn75550.aspx'),
                                                                                     (2, '西安琪华堂大药房', 7.20, '0.3g*20片', 'https://www.315jiage.cn/mn75550.aspx'),
                                                                                     (2, '山东仁华大药房旗舰店', 6.98, '0.3g*20片', 'https://www.315jiage.cn/mn75550.aspx');


-- 插入聊天记录测试数据
INSERT INTO chat_records (user_id, is_from_user, content, created_at) VALUES
                                                                          (2, 1, '我最近总是咳嗽，有点发热，请问可能是什么病？', '2025-04-11 10:15:30'),
                                                                          (2, 0, '根据您的症状，可能是普通感冒或支气管炎。建议您可以考虑使用感冒灵颗粒或复方甘草片缓解症状。由于您有高血压病史，请避免使用含有伪麻黄碱的感冒药。', '2023-05-01 10:16:15'),
                                                                          (3, 1, '我对花粉过敏，有什么药可以缓解？', '2025-03-02 14:20:10'),
                                                                          (3, 0, '对于花粉过敏(过敏性鼻炎)，可以考虑使用氯雷他定片或盐酸左西替利嗪片。这些药物可以有效缓解打喷嚏、流鼻涕等症状。', '2023-05-02 14:21:05'),
                                                                          (4, 1, '我胃疼，有灼热感，请问是怎么回事？', '2025-03-03 09:30:45'),
                                                                          (4, 0, '您的症状可能与胃炎或胃溃疡有关。建议您可以考虑使用奥美拉唑肠溶胶囊。由于您有胃溃疡病史和对阿司匹林过敏，请避免使用含有阿司匹林的药物。', '2023-05-03 09:32:10'),
                                                                          (5, 1, '我关节疼痛，尤其是右膝，有什么缓解方法？', '2025-03-04 16:45:20'),
                                                                          (5, 0, '您的症状与关节炎相符。布洛芬片可以缓解关节疼痛，但由于您对乳制品过敏，请确认药片中不含乳糖成分。同时建议保持适当运动和热敷疼痛部位。', '2023-05-04 16:47:35');

-- 插入反馈数据
INSERT INTO feedback (user_id, content, status, created_at) VALUES
                                                                (2, '系统推荐的药物对我的感冒症状很有效，谢谢！', 'Resolved', '2025-05-05 11:20:30'),
                                                                (3, '希望能增加更多关于过敏性疾病的信息。', 'InProgress', '2025-05-06 15:40:25'),
                                                                (4, '药品价格比较功能非常实用，帮我省了不少钱。', 'Resolved', '2025-05-07 09:15:10'),
                                                                (5, '聊天回复有时候不够准确，希望能改进。', 'Pending', '2025-05-08 14:30:55'),
                                                                (NULL, '网站加载速度有点慢，希望能优化。', 'Pending', '2025-05-09 10:25:40');

SET FOREIGN_KEY_CHECKS = 1; -- 重新启用外键约束