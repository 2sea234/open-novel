package com.kxhy.domain.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 预解析
 */
@Data
public class TxtImportPreviewResponse implements Serializable {

    private static final long serialVersionUID = 1L;
    private String title;
    private String authorName;
    private Integer categoryId;
    private String categoryName;
    private String summary;

}
