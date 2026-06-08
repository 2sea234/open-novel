package com.kxhy.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data@AllArgsConstructor@NoArgsConstructor
public class CoverGenerateResponse implements Serializable {

    private static final long serialVersionUID = 1L;
    private String promptId;
    private String filename;
    private String subfolder;
    private String type;
    private String imageUrl;

}
