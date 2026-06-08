package com.opennovel.common.domain.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class AiMetaRequest implements Serializable {

    private static final long serialVersionUID = 1L;
    private String title;
    private String excerpt;

}
