package com.kxhy.component;

import com.kxhy.domain.dto.ContentHeaderParseResult;
import com.kxhy.domain.dto.FieldDecisionResult;
import com.kxhy.domain.dto.FilenameParseResult;
import org.springframework.stereotype.Component;

/**
 * 字段决策组件
 */
@Component
public class FieldDecisionComponent {



    /**
     * 根据文件名和内容头部信息，进行字段决策
     * @param filenameParseResult 文件名解析结果
     * @param contentHeaderParseResult 内容头部解析结果
     * @return 字段决策结果
     */
    public FieldDecisionResult decide(FilenameParseResult filenameParseResult,
                                      ContentHeaderParseResult contentHeaderParseResult) {

        /*
         * 1. 文件名有标题，则使用文件名标题
         * 2. 文件名没有标题，则使用内容头部标题
         * 3. 文件名没有标题，内容头部没有标题，则使用“未知”
         */
        FieldDecisionResult result = new FieldDecisionResult();
        String filenameTitle = filenameParseResult.getTitle();
        String headerTitle = contentHeaderParseResult.getTitle();

        if (headerTitle != null && !headerTitle.isBlank()) {
            result.setTitle(headerTitle);
        }else if (filenameTitle != null && !filenameTitle.isBlank()){
            result.setTitle(filenameTitle);
        }

        String filenameAuthor = filenameParseResult.getAuthor();
        String headerAuthor = contentHeaderParseResult.getAuthor();

        if (headerAuthor != null && !headerAuthor.isBlank()) {
            result.setAuthor(headerAuthor);
        }else if (filenameAuthor != null && !filenameAuthor.isBlank()){
            result.setAuthor(filenameAuthor);
        }else {
            result.setAuthor("未知");
        }


        return result;
    }

}
