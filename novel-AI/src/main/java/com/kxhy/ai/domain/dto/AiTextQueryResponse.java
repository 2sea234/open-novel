package com.kxhy.ai.domain.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class AiTextQueryResponse implements Serializable {

    private static final long serialVersionUID = 1L;
    private String text; // 原文, 例如: 这
    private String pinyin; // 拼音, 例如: zhě
    private String explanation;  // 简短解释
    private List<String> meanings; // 多条释义

}
