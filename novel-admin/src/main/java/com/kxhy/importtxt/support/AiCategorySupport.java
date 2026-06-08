package com.kxhy.importtxt.support;

import com.kxhy.domain.dto.*;
import com.kxhy.domain.po.Categories;
import com.kxhy.service.CategoriesService;
import com.kxhy.service.NovelAiClient;
import com.opennovel.common.domain.dto.AIMetaResponse;
import com.opennovel.common.domain.dto.AiMetaRequest;
import com.opennovel.common.exception.BizException;
import com.opennovel.common.result.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class AiCategorySupport {


    private final NovelAiClient novelAiClient;
    private final CategoriesService categoriesService;

    public AiCategoryResult resolveCategory(String title, String excerpt) {

        AiCategoryResult result = new AiCategoryResult();

        AiMetaRequest aiMetaRequest = new AiMetaRequest();
        aiMetaRequest.setTitle(title);
        aiMetaRequest.setExcerpt(excerpt);

        Result<AIMetaResponse> aiMetaResponseResult = novelAiClient.generateMetaByTitleAndExcerpt(aiMetaRequest);

        if (aiMetaResponseResult == null) {
            throw new BizException(500, "AI生成出错，请检查是否联网");
        }

        AIMetaResponse aiMetaResponse = aiMetaResponseResult.getData();

        if (aiMetaResponse == null) {
            throw new BizException(400, "未拿到数据");
        }

        String categoryName = normalizeCategoryName(aiMetaResponse.getCategory());

        if (categoryName == null) {
            categoryName = "其他";
        }
        Categories categories = categoriesService.queryCategories(categoryName);

        if (categories ==  null) {
            categoriesService.insertCategory(categoryName, "");
            categories = categoriesService.queryCategories(categoryName);
        }

        if (categories ==  null) {
            throw new BizException(500, "分类处理失败");
        }
        result.setCategoryId(categories.getId());
        result.setCategoryName(categories.getName());
        result.setSummary(aiMetaResponse.getSummary());

        return result;
    }

    /**
     * 标准化分类名称
     * @param aiCategoryName aiCategoryName
     * @return aiCategoryName
     */
    public String normalizeCategoryName(String aiCategoryName) {

        if (aiCategoryName == null) {
            return null;
        }

        aiCategoryName = aiCategoryName.trim();

        if (aiCategoryName.isBlank()) {
            return null;
        }


        Map<String, String> map = new HashMap<>();
        // getOrDefault(key, defaultValue) 的意思是：先用 key 去 Map 里找值；如果找到了，就返回这个 key 对应的 value；
        // 如果没找到，就返回你给的默认值
        map.put("末世生存","科幻");
        map.put("修真玄幻","玄幻");
        map.put("刑侦悬疑","悬疑");
        map.put("科技","科幻");

        /*
         *  科幻：涉及未来、科技、外星生物等主题的小说。
            玄幻：包含魔法、武侠、神话等元素的小说。
            言情：侧重于爱情、婚姻、家庭等情感关系的小说。
            历史：以历史事件或人物为背景的小说，可能包含虚构的情节。
            悬疑：围绕谜团、推理和侦探故事展开的小说。
            现代言情：现代背景下的爱情故事，通常涉及当代社会问题。
            古代言情：设定在古代背景下的爱情故事，常常带有历史色彩。
            青春：描绘青少年成长、友情和爱情的小说。
            游戏竞技：围绕游戏、竞技和比赛展开的小说，常见于网络文学。
            灵异：涉及超自然现象、鬼怪等元素的小说。
         **/

        return map.getOrDefault(aiCategoryName, aiCategoryName);

    }


}
