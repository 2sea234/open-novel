package com.kxhy.mapper;

import com.kxhy.domain.po.ParagraphAnchors;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ParagraphAnchorsMapper {

    void insertBatchParagraphAnchors(@Param("list") List<ParagraphAnchors> list);

}
