package com.kxhy.importtxt.support;

import com.opennovel.common.exception.BizException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Service
public class TxtFileReadSupport {

    /**
     * 验证并读取TXT文件内容是否为空（自动检测编码：UTF-8 → GBK回退）
     */
    public String validateAndReadTxt(MultipartFile file) {

        if (file == null || file.isEmpty()) {
            throw new BizException(400, "TXT文件不能为空");
        }

        String filename = file.getOriginalFilename();
        if (filename ==  null || !filename.toLowerCase().endsWith(".txt")) {
            throw new BizException(400, "请上传.txt文件");
        }

        try {
            byte[] raw = file.getBytes();
            String content = new String(raw, StandardCharsets.UTF_8);
            if (content.indexOf('\uFFFD') >= 0) {
                content = new String(raw, java.nio.charset.Charset.forName("GBK"));
            }
            if (content.trim().isEmpty()) {
                throw new BizException(400, "文件内容为空");
            }
            return content;
        } catch (IOException e) {
            throw new BizException(400, "文件读取失败");
        }
    }


}
