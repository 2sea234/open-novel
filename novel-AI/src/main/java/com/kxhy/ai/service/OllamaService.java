package com.kxhy.ai.service;

import com.opennovel.common.domain.dto.AIMetaResponse;
import com.opennovel.common.domain.dto.AiMetaRequest;

public interface OllamaService {

    /**
     * 根据标题和摘要生成meta
     * @param aiMetaRequest
     * @return
     */
    AIMetaResponse generateMetaByTitleAndExcerpt(AiMetaRequest aiMetaRequest);




}
