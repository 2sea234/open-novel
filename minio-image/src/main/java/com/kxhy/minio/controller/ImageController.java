package com.kxhy.minio.controller;


import com.opennovel.common.domain.vo.FileUploadVO;
import com.kxhy.minio.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("image")
@RequiredArgsConstructor
public class ImageController {

    private final ImageService imageService;

    @PostMapping("/upload")
    public FileUploadVO uploadImage(@RequestPart("file") MultipartFile file) {
        return imageService.uploadImage(file);
    }

    @DeleteMapping("/delete")
    public void deleteImage(@RequestParam String objectName) {
        imageService.deleteImage(objectName);
    }

}
