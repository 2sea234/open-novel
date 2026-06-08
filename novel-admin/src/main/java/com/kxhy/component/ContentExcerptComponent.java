package com.kxhy.component;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 读取正文内容,生成简介内容
 */
@Component
public class ContentExcerptComponent {

    private static final int MAX_EXCERPT_LENGTH = 2000; // 最大简介长度
    private static final Pattern CHAPTER_TITLE_PATTERN = Pattern.compile(
            "^第[0-9零〇一二三四五六七八九十百千万两]+[章节卷回部篇].*"
    ); // 章节标题正则

    public String buildExcerpt(String content) {

        // 预处理
        if (content == null || content.isBlank()) {
            return "";
        }

        String normalizedContent = content
                .replace("\uFEFF", "")
                .replace("\r\n", "\n")
                .replace("\r", "\n")
                .trim();
        List<String> cleanedLines = new ArrayList<>();

        for (String rawLine  : normalizedContent.split("\n")) {
            String line = rawLine == null ? "" : rawLine .trim();

            if (line.isBlank()) {
                continue;
            }

            if (isNoiseLine(line)) {
                continue;
            }

            cleanedLines.add(line);
        }

        if (cleanedLines.isEmpty()) {
            return "";
        }

        int startIndex = findChapterStartIndex(cleanedLines);

        List<String> excerptLines = cleanedLines.subList(startIndex, cleanedLines.size());
        String excerpt = String.join("\n", excerptLines).trim();

        if (excerpt.length() > MAX_EXCERPT_LENGTH) {
            return excerpt.substring(0, MAX_EXCERPT_LENGTH);
        }
        return excerpt;
    }

    /**
     * 寻找正文开始的索引
     * @param lines 文本行
     * @return 正文开始的索引
     */
    private int findChapterStartIndex(List<String> lines) {
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            if ("全文".equals(line)) {
                if (i + 1 < lines.size()) {
                    return i + 1;
                }
            }

            if (CHAPTER_TITLE_PATTERN.matcher(line).matches()) {
                return i;
            }
        }

        return 0;
    }

    /**
     * 判断是否是噪声行
     * @param line 文本行
     * @return 是否是噪声行
     */
    private boolean isNoiseLine(String line) {
        if (line.contains("电报群")) {
            return true;
        }

        if (line.contains("免费入群")) {
            return true;
        }
        if (line.contains("禁忌书屋")) {
            return true;
        }
        if (line.startsWith("书名：") || line.startsWith("书名:")) {
            return true;
        }
        if (line.startsWith("作者：") || line.startsWith("作者:")) {
            return true;
        }
        if (line.startsWith("视角：") || line.startsWith("视角:")) {
            return true;
        }
        if (line.startsWith("篇幅：") || line.startsWith("篇幅:")) {
            return true;
        }
        if (line.startsWith("分类：") || line.startsWith("分类:")) {
            return true;
        }
        if (line.startsWith("标签：") || line.startsWith("标签:")) {
            return true;
        }
        if (line.startsWith("肉量：") || line.startsWith("肉量:")) {
            return true;
        }
        if (line.startsWith("状态：") || line.startsWith("状态:")) {
            return true;
        }
        if (line.startsWith("简介：") || line.startsWith("简介:")) {
            return true;
        }

        return line.startsWith("【") && line.endsWith("】");

    }

}
