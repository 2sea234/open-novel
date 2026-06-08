package com.kxhy.component;

import com.kxhy.domain.dto.ChapterParseResult;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class TxtParseComponent {


    private static final String HORIZONTAL_SPACE = "[ \\t\\u3000]*";

    private static final Pattern CHAPTER_PATTERN = Pattern.compile(
            "(?m)^" + HORIZONTAL_SPACE + "(?:" +
                    "第" + HORIZONTAL_SPACE +
                    "[0-9零〇一二三四五六七八九十百千万两]+" +
                    HORIZONTAL_SPACE +
                    "[章节卷回部集篇]" +
                    HORIZONTAL_SPACE +
                    ".{0,80}" +
                    "|" +
                    "[Cc][Hh][Aa][Pp][Tt][Ee][Rr]" +
                    HORIZONTAL_SPACE +
                    "\\d+" +
                    HORIZONTAL_SPACE +
                    ".{0,80}" +
                    "|" +
                    "(?:序章|楔子|前言|后记|尾声|番外|终章|结局|引子|开篇|跋|附录|外传|前传|后传)" +
                    "(?:" + HORIZONTAL_SPACE + "[0-9零〇一二三四五六七八九十百千万两]+)?" +
                    HORIZONTAL_SPACE +
                    ".{0,80}" +
                    "|" +
                    "(?:第" + HORIZONTAL_SPACE + ")?卷" +
                    HORIZONTAL_SPACE +
                    "[之的]?" +
                    HORIZONTAL_SPACE +
                    "[0-9零〇一二三四五六七八九十百千万两]+" +
                    HORIZONTAL_SPACE +
                    ".{0,80}" +
                    ")" + HORIZONTAL_SPACE + "$"
    );

    private static final Pattern FALLBACK_NUMBERED_PATTERN = Pattern.compile(
            "(?m)^" + HORIZONTAL_SPACE + "\\d+[\\.、\\)）]" + HORIZONTAL_SPACE + ".{0,80}$"
    );

    private static final Pattern FALLBACK_CN_NUMBERED_PATTERN = Pattern.compile(
            "(?m)^" + HORIZONTAL_SPACE + "[零〇一二三四五六七八九十百千万两]+[\\.、\\)）]" + HORIZONTAL_SPACE + ".{0,80}$"
    );

    public List<ChapterParseResult> parseChapters(String content) {
        content = preprocess(content);
        if (content == null || content.trim().isEmpty()) {
            return new ArrayList<>();
        }

        List<ChapterParseResult> result = tryParse(content, CHAPTER_PATTERN);
        if (!result.isEmpty()) {
            return tryPrependPreContent(content, result, CHAPTER_PATTERN);
        }

        result = tryParse(content, FALLBACK_NUMBERED_PATTERN);
        if (!result.isEmpty()) {
            return tryPrependPreContent(content, result, FALLBACK_NUMBERED_PATTERN);
        }

        result = tryParse(content, FALLBACK_CN_NUMBERED_PATTERN);
        if (!result.isEmpty()) {
            return tryPrependPreContent(content, result, FALLBACK_CN_NUMBERED_PATTERN);
        }

        return result;
    }

    private static String preprocess(String content) {
        if (content == null) {
            return null;
        }
        content = content.replace("\uFEFF", "");
        content = content.replace("\r\n", "\n").replace("\r", "\n");
        content = content.replace("\u3000", "");
        return content;
    }

    private List<ChapterParseResult> tryParse(String content, Pattern pattern) {
        List<ChapterParseResult> result = new ArrayList<>();
        Matcher matcher = pattern.matcher(content);

        List<String> titles = new ArrayList<>();
        List<Integer> starts = new ArrayList<>();
        List<Integer> ends = new ArrayList<>();

        while (matcher.find()) {
            titles.add(matcher.group().trim());
            starts.add(matcher.start());
            ends.add(matcher.end());
        }

        for (int i = 0; i < titles.size(); i++) {
            String chapterTitle = titles.get(i);
            Integer chapterIndex = i + 1;

            int contentStart = ends.get(i);
            int contentEnd = (i == titles.size() - 1) ? content.length() : starts.get(i + 1);
            String chapterContent = content.substring(contentStart, contentEnd).trim();
            result.add(new ChapterParseResult(chapterIndex, chapterTitle, chapterContent));
        }

        return result;
    }

    private List<ChapterParseResult> tryPrependPreContent(String content, List<ChapterParseResult> chapters, Pattern pattern) {
        return chapters;
    }

    public List<String> parseParagraphs(String content) {

        List<String> paragraphs = new ArrayList<>();

        if (content == null || content.trim().isEmpty()) {
            return paragraphs;
        }

        content = content.replace("\r\n", "\n").replace("\r", "\n");
        String[] lines = content.split("\n");

        for (String line :
                lines) {
            String text = line.trim();
            if (!text.isEmpty()) {
                paragraphs.add(text);
            }

        }

        return paragraphs;
    }

    public String generateParagraphHash(Long chapterId, Integer paragraphIndex, String content) {
        if (chapterId == null) {
            throw new IllegalArgumentException("chapterId 不能为空");
        }

        if (paragraphIndex == null) {
            throw new IllegalArgumentException("paragraphIndex 不能为空");
        }

        if (content == null) {
            content = "";
        }

        String raw = chapterId + "|" + paragraphIndex + "|" + content.trim();

        try {

            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(raw.getBytes(StandardCharsets.UTF_8));

            StringBuilder stringBuilder = new StringBuilder();

            for (byte b :
                    digest) {
                stringBuilder.append(String.format("%02x", b));
            }

            return stringBuilder.toString();
        }catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("生成段落 hash 失败", e);
        }
    }

    public String normalizeParagraphContent(String content) {
        if (content == null) {
            return "";
        }

        return content
                .replace("\r\n", "\n")
                .replace("\r", "\n")
                .replace('\u3000', ' ')
                .replace('\t', ' ')
                .replaceAll("\\s+", "");
    }

    public String generateAnchorHash(Integer chapterIndex, String normalizedContent) {
        if (chapterIndex == null) {
            throw new IllegalArgumentException("chapterIndex 不能为空");
        }

        if (normalizedContent == null) {
            normalizedContent = "";
        }

        String raw = chapterIndex + "|" + normalizedContent;
        try {
            MessageDigest instance = MessageDigest.getInstance("SHA-256");
            byte[] digest = instance.digest(raw.getBytes(StandardCharsets.UTF_8));
            StringBuilder stringBuilder = new StringBuilder();
            for (byte b:  digest) {
                stringBuilder.append(String.format("%02x", b));
            }
            return stringBuilder.toString();
        }catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("生成段落 hash 失败", e);
        }
    }

    public int countTextWordCount(String text) {
        if (text == null || text.trim().isEmpty()) {
            return 0;
        }
        return text.replaceAll("\\s+", "").length();
    }

    public int countParagraphListWordCount(List<String> paragraphList) {
        if (paragraphList == null || paragraphList.isEmpty()) {
            return 0;
        }
        int count = 0;
        for (String paragraph : paragraphList) {
            count += countTextWordCount(paragraph);
        }
        return count;
    }


}
