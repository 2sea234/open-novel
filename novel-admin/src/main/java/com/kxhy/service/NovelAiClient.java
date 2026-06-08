package com.kxhy.service;

import com.opennovel.common.domain.dto.AIMetaResponse;
import com.opennovel.common.domain.dto.AiMetaRequest;
import com.opennovel.common.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "novel-ai")
public interface NovelAiClient {

    @PostMapping("/ai/novel/generate-meta-by-excerpt")
    Result<AIMetaResponse> generateMetaByTitleAndExcerpt(@RequestBody AiMetaRequest aiMetaRequest);

}
