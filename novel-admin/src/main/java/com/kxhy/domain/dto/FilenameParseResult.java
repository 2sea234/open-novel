package com.kxhy.domain.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class FilenameParseResult implements Serializable {

    private static final long serialVersionUID = 1L;
    private String title;
    private String author;

}
