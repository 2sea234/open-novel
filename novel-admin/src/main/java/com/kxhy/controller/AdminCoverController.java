package com.kxhy.controller;

import com.kxhy.domain.dto.CoverGenerateRequest;
import com.kxhy.domain.dto.CoverGenerateResponse;
import com.kxhy.service.ComfyuiCoverService;
import com.opennovel.common.result.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("admin/cover")
@RequiredArgsConstructor
public class AdminCoverController {

    private final ComfyuiCoverService comfyuiCoverService;

    @PostMapping("/generate")
    public Result<CoverGenerateResponse> generateCover(@RequestBody CoverGenerateRequest request) {
        CoverGenerateResponse response = comfyuiCoverService.generateCover(request);
        return Result.success(response);
    }
}
