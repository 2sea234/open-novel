package com.opennovel.common.domain.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class AIMetaResponse implements Serializable {

    private final static long serialVersionUID = 1L;
    private String category;
    private List<String> tags;
    private String summary;

}
