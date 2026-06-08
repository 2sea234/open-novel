package com.kxhy.component;

import com.kxhy.domain.dto.FilenameParseResult;
import org.ahocorasick.trie.Emit;
import org.ahocorasick.trie.Trie;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 文件名解析组件
 */
@Component
public class FilenameParseComponent {

    private static final String[] TRAILING_META_KEYWORDS = {
            "精修",
            "加料",
            "校对",
            "精校",
            "校正版",
            "番外",
            "全本",
            "全集",
            "完结",
            "完本",
            "完整版",
            "二次精修"
    };

    private static final Pattern TRAILING_BRACKET_META_PATTERN =
            Pattern.compile("\\s*[（(\\[]([^）)\\]]{1,60})[）)\\]]\\s*$");


    /**
     * 解析文件名
     * @param originalFilename 文件名
     * @return 解析结果
     */
    public FilenameParseResult parseFilename(String originalFilename) {

        FilenameParseResult result = new FilenameParseResult();

        if (originalFilename == null || originalFilename.isBlank()) return result;

        // 调用normalizeFilenameForParse方法清洗尾部元信息
        String filename = normalizeFilenameForParse(originalFilename);

        System.out.println("原始文件名 = " + originalFilename);
        System.out.println("清洗后文件名 = " + filename);

        if (filename.isBlank()) return result;

        // 判断是否包含作者
        if (filename.contains("作者：")) {

            // 如果存在作者，则将作者和标题分开
            String[] parts = filename.split("作者：", 2);

            // 获取标题
            String leftPart = parts[0].trim();
            // 获取作者
            String rightPart = parts[1].trim();

            // 判断标题是否以 - 结尾
            if (leftPart.endsWith("-")) {
                // 去掉 -
                leftPart = leftPart.substring(0, leftPart.length() -1).trim();
            }

            // 组装结果
            result.setTitle(leftPart);
            result.setAuthor(rightPart);
            return result;
        } else if (filename.contains("-")) {
            String[] parts = filename.split("-", 2);
            String leftPart = parts[0].trim();
            String rightPart = parts[1].trim();

            result.setTitle(leftPart);
            result.setAuthor(rightPart);
            return result;
        } else if (filename.contains(" ")) {

            // 获取最后一个空格
            int lastSpaceIndex  = filename.lastIndexOf(" ");
            // 获取标题
            String leftPart = filename.substring(0, lastSpaceIndex ).trim();
            // 获取作者
            String rightPart = filename.substring(lastSpaceIndex  + 1).trim();

            // 判断是否是正常的作者名
            if (isLikelyAuthorName(rightPart)) {
                result.setTitle(leftPart);
                result.setAuthor(rightPart);
            }else {
                result.setTitle(filename);
            }
            return result;
        }

        result.setTitle(filename);
        return result;
    }

    /**
     * 清洗尾部元信息
     * @param filename 文件名
     * @return 清洗后的文件名
     */
    private String normalizeFilenameForParse(String filename) {

        // 如果为空，则返回空
        if (filename == null || filename.isBlank()) {
            return "";
        }

        // 获取文件名
        String value = filename.trim();

        // 去掉.txt / .TXT / .txt.txt
        value = value.replaceAll("(?i)(\\.txt)+$", "");

        // 统一去空白
        value = value.replace('\u3000', ' ');
        value = value.replaceAll("\\s+", " ").trim();

        // 循环清理尾部括号元信息
        while (true) {

            // 匹配元信息
            Matcher matcher = TRAILING_BRACKET_META_PATTERN.matcher(value);

            // 如果没有匹配，则退出循环
            if (!matcher.find()) break;

            // 获取元信息
            String inner = matcher.group(1).trim();

            // 如果不是元信息，则退出循环
            if (!isTrailingMeta(inner)) break;

            // 清洗元信息
            value = value.substring(0, matcher.start()).trim();

        }

        value = removePlainTrailingMeta(value);

        // 返回结果
        return value;
    }


    /**
     * 判断是否是尾部元信息
     * @param text 元信息
     * @return 是否是尾部元信息
     */
    private boolean isTrailingMeta(String text) {

        if (text == null || text.isBlank()) {
            return false;
        }

        String value = text.trim();

        if (containsAnyKeyword(value, TRAILING_META_KEYWORDS)) return true;
        if ("全".equals(value)) return true;
        if (value.matches(".*\\d+\\s*[-—_]+\\s*\\d+.*")) return true;
        return false;
    }

    /**
     * 判断字符串中是否包含任意关键词
     * @param text 待判断的字符串
     * @param keywords 关键词列表
     * @return 是否包含任意关键词
     */
    private boolean containsAnyKeyword(String text, String[] keywords) {

        if (text == null || text.isBlank()) {
            return false;
        }
        Trie.TrieBuilder builder = Trie.builder();
        for (String keyword : keywords) {
            builder.addKeyword(keyword);
        }

        Trie trie = builder.build();

        Collection<Emit> emits = trie.parseText(text);

        return !emits.isEmpty();

    }

    /**
     * 兜底方案
     * 主要处理：
     * 有数字：462
     * 有分隔符：-
     * 有版本词：加料、精修
     * 更像文件元信息，不像人名/笔名
     */
    private boolean isLikelyAuthorName(String text) {
        if (text == null || text.isBlank()) return false;

        String value = text.trim();

        if (value.length() < 2 || value.length() > 20) return false;

        if (value.matches(".*\\d.*")) return false;

        if (value.matches(".*[-—_()（）\\[\\]【】].*")) return false;

        if (isTrailingMeta(value)) return false;

        return value.matches("^[\\u4e00-\\u9fa5a-zA-Z]+$");
    }

    /**
     * 清理无括号尾部元信息
     * @param filename 文件名
     * @return 清理后的文件名
     */
    private String removePlainTrailingMeta(String filename) {

        if (filename == null || filename.isBlank()) {

            return "";
        }

        String value = filename.trim();

        int lastSpaceIndex = value.lastIndexOf(" ");
        if (lastSpaceIndex < 0) {
            return value;
        }

        String leftPart = value.substring(0, lastSpaceIndex).trim();
        String rightPart = value.substring(lastSpaceIndex + 1).trim();

        if (leftPart.isBlank() || rightPart.isBlank()) {
            return value;
        }

        if (isTrailingMeta(rightPart)) {
            return leftPart;
        }

        return value;
    }



}
