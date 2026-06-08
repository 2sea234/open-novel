package com.kxhy.domain.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class CoverGenerateRequest implements Serializable {

    private static final long serialVersionUID = 1L;
    private String title;

    private String summary;

    /**
     * 正向提示词，第一版建议直接传英文 prompt
     */
    private String prompt;

    /**
     * 负向提示词，可不传，不传走默认
     */
    private String negativePrompt;

}
