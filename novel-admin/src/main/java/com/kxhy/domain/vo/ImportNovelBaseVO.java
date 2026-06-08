package com.kxhy.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ImportNovelBaseVO implements Serializable {

    private static final long serialVersionUID = 1L;
    private Integer articleId;
    private Boolean success;

}
