package com.kxhy.domain.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class AnchorDraft implements Serializable {

    private static final long serialVersionUID = 1L;
    private Integer chapterIndex;
    private String chapterTitle;
    private Integer paragraphIndex;
    private String anchorHash;
    private Integer occurrenceNo;

}
