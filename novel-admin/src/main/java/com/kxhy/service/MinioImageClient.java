package com.kxhy.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@FeignClient(name = "minio-image")
public interface MinioImageClient {

    @PostMapping(value = "/image/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    com.opennovel.common.domain.vo.FileUploadVO uploadImage(@RequestPart("file") MultipartFile file);

}
