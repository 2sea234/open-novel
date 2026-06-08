package com.kxhy.ai.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kxhy.ai.config.OllamaProperties;
import com.opennovel.common.domain.dto.AIMetaResponse;
import com.kxhy.ai.service.OllamaService;
import com.opennovel.common.domain.dto.AiMetaRequest;
import com.opennovel.common.exception.BizException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class OllamaServiceImpl implements OllamaService {

    private final RestTemplate restTemplate;
    private final OllamaProperties ollamaProperties;
    private final ObjectMapper objectMapper;

    /**
     * 根据书名及内容生成简介及标签
     */
    @Override
    public AIMetaResponse generateMetaByTitleAndExcerpt(AiMetaRequest aiMetaRequest) {

        if (aiMetaRequest == null) {
            throw new BizException(400, "参数不能为空");
        }

        String title = aiMetaRequest.getTitle();
        String excerpt = aiMetaRequest.getExcerpt();

        if (title == null || title.isBlank()) {
            throw new BizException(400, "请输入书名,参数不能为空");
        }

        if (excerpt == null || excerpt.isBlank()) {
            throw new BizException(400, "正文摘录不能为空");
        }

        String baseUrl = ollamaProperties.getBaseUrl();
        String model = ollamaProperties.getModel();
        if (baseUrl == null || baseUrl.isBlank()) {
            throw new BizException(500, "ollama.base-url 未配置");
        }
        if (model == null || model.isBlank()) {
            throw new BizException(500, "ollama.model 未配置");
        }
        String url = baseUrl + "/api/generate";

        String prompt = """
                你是小说信息提取助手，请根据书名和正文摘录生成小说元数据。
                
                  要求：
                  1. 只返回 JSON
                  2. 不要返回 markdown
                  3. 不要返回代码块
                  4. 不要返回任何解释性文字
                  5. category 只返回一个最合适的小说分类
                  6. tags 返回 3 到 5 个简短标签
                  7. summary 请写成更自然的小说简介，像编辑写的推荐文案，而不是机械总结
                  8. summary 控制在 90 到 130 字
                  9. summary 不要使用“本文讲述了”“文章讲述了”“故事围绕”“本书主要讲述”等模板句
                  10. summary 不要生硬罗列设定，要先点出主角处境，再带出核心冲突或悬念
                  11. 可以适当使用一次“……”增强语气，但不要刻意堆砌
                  12. 仅依据书名和正文摘录判断，不要编造与正文摘录明显冲突的信息
                
                  返回格式：
                  {
                    "category":"玄幻",
                    "tags":["热血","升级流","成长"],
                    "summary":"文章简介"
                  }
                
                  书名：%s
                
                  正文摘录：
                  %s
                """.formatted(title, excerpt);  // 字符串内容替换： 它使用类似 C 语言 printf 的风格（如 %s, %d），将特定参数替换到字符串的占位符中。
                                                // 代码可读性： 相比 String.format("...", args)，它让代码更像模板风格，阅读时更容易理解字符串结构
        Map<String, Object> requestBody = new HashMap<>();
        // 模型名称
        requestBody.put("model", model);
        requestBody.put("prompt", prompt);
        requestBody.put("stream", false);
        requestBody.put("think", false);
        requestBody.put("keep_alive", "30m");
        requestBody.put("format", "json");

        Map<String, Object> options = new HashMap<>();
        options.put("num_predict", 400);
        requestBody.put("options", options);

        return postForMetaResponse(url, requestBody);

    }

    /**
     * 清理Json
     * @param text jsonText
     * @return jsonText
     */
    private String cleanJson(String text) {
        if (text == null) {
            return null;
        }
        String result = text.trim();

        if (result.startsWith("```json")) {
            result = result.substring(7).trim();
        }else if (result.startsWith("```")) {
            result = result.substring(3).trim();
        }

        if (result.endsWith("```")) {
            result = result.substring(0, result.length() -3).trim();
        }
        return result;
    }

    /**
     * 解析jsonText，校验并清洗tags、category、summary字段
     * @param jsonText jsonText
     * @return AIMetaResponse
     */
    private AIMetaResponse parseAndValidateMetaResponse(String jsonText) {
        try {

            if (jsonText == null || jsonText.isBlank()) {
                throw new BizException(500, "模型结果清洗后为空");
            }

            // 处理结果
            AIMetaResponse aiMetaResponse = objectMapper.readValue(jsonText, AIMetaResponse.class);

            if (aiMetaResponse.getTags() == null || aiMetaResponse.getTags().isEmpty()) {
                throw new BizException(500, "标签不能为空");
            }

            // 标签清洗顺序 --> 清洗标签时用 LinkedHashSet，返回给 AIMetaResponse 时转回 List。
            // 去重： 为什么用LinkedHashSet而不用HashSet
            LinkedHashSet<String> tagSet = new LinkedHashSet<>();
            // HashSet：去重，但不保证顺序 LinkedHashSet：去重，而且保留插入顺序 口诀： HashSet 只管不重复，LinkedHashSet 还管顺序。
            // 遍历标签
            for (String tag : aiMetaResponse.getTags()) {

                if (tag == null) {
                    continue;
                }

                // trim() 的作用：把字符串两端多余的空白去掉
                tag = tag.trim();
                if (tag.isBlank()) {
                    continue;
                }

                if (tagSet.size() == 5) {
                    break;
                }

                tagSet.add(tag);

            }

            // isEmpty() 只看长度是不是 0。 也就是字符串里一个字符都没有。
            if (tagSet.isEmpty()) {
                throw new BizException(500, "标签不能为空");
            }

            // tagSet 负责 去重 + 保序
            List<String> tags = new ArrayList<>(tagSet);  // new ArrayList<>(tagSet) 负责把结果转成 List<String>
            aiMetaResponse.setTags(tags);  // 赋值给 AIMetaResponse

            String category = aiMetaResponse.getCategory();
            category = normalizeRequiredText(category, "分类不能为空");
            aiMetaResponse.setCategory(category);

            String summary = aiMetaResponse.getSummary();
            summary = normalizeRequiredText(summary, "简介不能为空");
            // tip: 你可以把它记成一句话：isEmpty() 只防“没字符”，isBlank() 还防“全是空格”。
            aiMetaResponse.setSummary(summary);

            // 返回结果
            return aiMetaResponse;
        } catch (JsonProcessingException e) {
            throw new BizException(500, "模型结果解析失败");
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * post 请求
     * @param url  url
     * @param requestBody requestBody
     * @return AIMetaResponse
     */
    private AIMetaResponse postForMetaResponse(String url, Map<String, Object> requestBody) {
        try {
            // 发送请求
            Map<String, Object> result = restTemplate.postForObject(url, requestBody, Map.class);
            if (result ==  null) {
                throw new BizException(500, "模型未返回内容");
            }
            // 获取结果
            Object responseObject = result.get("response");
            // isBlank() 更严格一点。它看的是：是不是空串，或者全是空白字符。 空格、制表符这类都算空白。
            if (responseObject == null || responseObject.toString().isBlank()) {
                throw new BizException(500, "模型内容为空");
            }
            // 清理Json
            String jsonText = cleanJson(responseObject.toString());
            AIMetaResponse aiMetaResponse = parseAndValidateMetaResponse(jsonText);
            return aiMetaResponse;
        } catch (RestClientException e) {
            throw new BizException(500, "信息生成失败");
        }
    }

    /**
     * 校验非空
     * @param value value
     * @param message message
     * @return String
     */
    private String normalizeRequiredText(String value, String message) {
        if (value == null) {
            throw new BizException(500, message);
        }
        value = value.trim();
        if (value.isBlank()) {
            throw new BizException(500, message);
        }
        return value;
    }

}
