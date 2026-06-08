package com.kxhy.domain.po;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class Paragraphs implements Serializable {

    private Long id;
    private Long chapterId;
    private Integer paragraphIndex;
    private String content;
    private String paragraphHash;
    private LocalDateTime createTime;

}
