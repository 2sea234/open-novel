package com.kxhy.ai.controller;

import com.opennovel.common.domain.dto.AIMetaResponse;
import com.kxhy.ai.service.OllamaService;
import com.opennovel.common.domain.dto.AiMetaRequest;
import com.opennovel.common.result.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ai/novel")
@RequiredArgsConstructor
public class OllamaController {

    private final OllamaService ollamaService;

    @PostMapping("generate-meta-by-excerpt")
    public Result<AIMetaResponse> generateMeta(@RequestBody AiMetaRequest aiMetaRequest) {
        AIMetaResponse aiMetaResponse = ollamaService.generateMetaByTitleAndExcerpt(aiMetaRequest);
        return Result.success(aiMetaResponse);
    }

}
