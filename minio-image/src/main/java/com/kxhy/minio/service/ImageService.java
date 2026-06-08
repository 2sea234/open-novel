package com.kxhy.minio.service;

import com.opennovel.common.domain.vo.FileUploadVO;
import org.springframework.web.multipart.MultipartFile;

public interface ImageService {

    FileUploadVO uploadImage(MultipartFile file);

    void deleteImage(String objectName);

    FileUploadVO uploadImageBytes(byte[] bytes, String originalFilename, String contentType);
}
