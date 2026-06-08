package com.kxhy.mapper;

import com.kxhy.domain.po.Paragraphs;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ParagraphsMapper {

    void insertParagraph(Paragraphs paragraphs);

    void insertBatchParagraphs(@Param("list")List<Paragraphs> list);

}
