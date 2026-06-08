package com.kxhy.component;

import com.kxhy.domain.dto.ContentHeaderParseResult;
import org.springframework.stereotype.Component;

@Component
public class ContentHeaderParseComponent {

    private static final int MAX_HEADER_SCAN_LINES = 80;

    public ContentHeaderParseResult parseHeader(String content) {
        String title = parseTitleFromContent(content);
        String author = parseAuthorFromContent(content);

        ContentHeaderParseResult result = new ContentHeaderParseResult();
        result.setTitle(title);
        result.setAuthor(author);
        return result;
    }

    private String parseTitleFromContent(String content) {
        if (content == null || content.isBlank()) {
            return null;
        }

        String[] lines = normalize(content).split("\n");

        for (int i = 0; i < lines.length && i < MAX_HEADER_SCAN_LINES; i++) {
            String line = cleanLine(lines[i]);

            if (line.isBlank()) {
                continue;
            }

            if (isChapterTitle(line)) {
                break;
            }

            if (line.startsWith("书名：")) {
                return blankToNull(line.substring("书名：".length()));
            }

            if (line.startsWith("书名:")) {
                return blankToNull(line.substring("书名:".length()));
            }

            if (line.startsWith("【") && line.endsWith("】")) {
                return blankToNull(line.substring(1, line.length() - 1));
            }
        }

        return null;
    }

    private String parseAuthorFromContent(String content) {
        if (content == null || content.isBlank()) {
            return null;
        }

        String[] lines = normalize(content).split("\n");

        for (int i = 0; i < lines.length && i < MAX_HEADER_SCAN_LINES; i++) {
            String line = cleanLine(lines[i]);

            if (line.isBlank()) {
                continue;
            }

            if (isChapterTitle(line)) {
                break;
            }

            if (line.startsWith("作者：")) {
                return blankToNull(line.substring("作者：".length()));
            }

            if (line.startsWith("作者:")) {
                return blankToNull(line.substring("作者:".length()));
            }
        }

        return null;
    }

    private String normalize(String content) {
        return content
                .replace("\uFEFF", "")
                .replace("\r\n", "\n")
                .replace("\r", "\n");
    }

    private String cleanLine(String line) {
        if (line == null) {
            return "";
        }

        return line
                .replace("\u00A0", " ")
                .replace("　", " ")
                .trim();
    }

    private String blankToNull(String value) {
        if (value == null) {
            return null;
        }

        String result = value.trim();
        return result.isBlank() ? null : result;
    }

    private boolean isChapterTitle(String line) {
        return line.matches("^第[0-9零〇一二三四五六七八九十百千万两]+[章节卷回部篇].*");
    }
}