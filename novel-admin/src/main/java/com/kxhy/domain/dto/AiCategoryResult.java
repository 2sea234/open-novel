package com.kxhy.domain.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class AiCategoryResult implements Serializable {

    private static final long serialVersionUID = 1L;
    private Integer categoryId;
    private String categoryName;
    private String summary;

}
