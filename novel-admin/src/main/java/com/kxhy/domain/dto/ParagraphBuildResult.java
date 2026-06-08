package com.kxhy.domain.dto;

import com.kxhy.domain.po.Paragraphs;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class ParagraphBuildResult implements Serializable {

    private static final long serialVersionUID = 1L;
    private List<Paragraphs> batchList;
    private List<AnchorDraft> anchorDraftList;

}
