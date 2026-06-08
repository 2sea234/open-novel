package com.kxhy.minio.service.impl;

import com.kxhy.minio.config.MinioProperties;
import com.opennovel.common.domain.vo.FileUploadVO;
import com.kxhy.minio.service.ImageService;
import com.opennovel.common.exception.BizException;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {


    private final MinioClient minioClient;
    private final MinioProperties minioProperties;

    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            "image/png",
            "image/jpg",
            "image/jpeg",
            "image/webp"
    );

    private static final Set<String> ALLOWED_SUFFIXES = Set.of(
            ".png",
            ".jpg",
            ".jpeg",
            ".webp"
    );


    @Override
    public FileUploadVO uploadImage(MultipartFile file) {

        // 判断文件是否为空
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("上传文件不能为空");
        }


       try {

           byte[] bytes = file.getBytes();

           validateImageFile(
                   bytes,
                   file.getOriginalFilename(),
                   file.getContentType()
           );

           return uploadImageInternal(
                   new ByteArrayInputStream(bytes),
                   bytes.length,
                   file.getContentType(),
                   file.getOriginalFilename()
           );
       }catch (BizException e) {
           throw e;
       }catch (Exception e) {
           throw new BizException(400, "上传图片失败");
       }
    }

    @Override
    public void deleteImage(String objectName) {
        if (objectName == null || objectName.isBlank()) {
            throw new RuntimeException("objectName 不能为空");
        }

        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(minioProperties.getBucketName())
                            .object(objectName)
                            .build()
            );
        }catch (Exception e) {
            throw new RuntimeException("图片删除失败", e);
        }

    }

    @Override
    public FileUploadVO uploadImageBytes(byte[] bytes, String originalFilename, String contentType) {

        if (bytes == null || bytes.length == 0) {
            throw new BizException(400, "上传文件不能为空");
        }

        try {
            return uploadImageInternal(
                    new ByteArrayInputStream(bytes),
                    bytes.length,
                    contentType,
                    originalFilename
            );
        }catch (BizException e) {
            throw e;
        }
        catch (Exception e) {
            throw new BizException(400, "上传图片失败");
        }
    }

    // 获取文件后缀
    public String getSuffix(String fileName) {

        if (fileName == null || fileName.isBlank()) {
            return "";
        }

        int index = fileName.lastIndexOf(".");
        // 判断文件名是否为空
        if (index == -1 || index == fileName.length() - 1) {
            return "";
        }
        // 返回文件后缀
        return fileName.substring(index);
    }

    private FileUploadVO uploadImageInternal(InputStream inputStream, long size, String contentType, String originalFilename) {

        // 验证文件格式
        if (contentType == null || !(contentType.equals("image/png")
        || contentType.equals("image/jpg") || contentType.equals("image/webp") || contentType.equals("image/jpeg"))) {
            throw new BizException(400, "仅支持png、jpg、webp、jpeg图片.");
        }


        String suffix = getSuffix(originalFilename);

        // 没有文件后缀时的，兜底方案
        if (suffix == null || suffix.isBlank()) {
            suffix = switch (contentType) {
                case "image/png" -> ".png";
                case "image/jpg" -> ".jpg";
                case "image/webp" -> ".webp";
                case "image/jpeg" -> ".jpeg";
                default -> ".jpg";
            };
        }

        // 创建目录
        String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        String objectName = "cover/" + datePath + "/" + UUID.randomUUID().toString().replace("-", "") + suffix;

        // 上传图片
        try (InputStream in = inputStream) {
            // 上传图片
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(minioProperties.getBucketName())
                            .object(objectName)
                            .stream(in, size, -1)
                            .contentType(contentType)
                            .build()
            );
        } catch (BizException e) {
            throw e;
        } catch (Exception e) {
            throw new BizException(500, "上传图片失败：");
        }

        // 获取图片 url
        String url = minioProperties.getEndpoint() + "/" + minioProperties.getBucketName() + "/" + objectName;

        // 返回图片信息
        return new FileUploadVO(
                objectName,
                url,
                size,
                contentType,
                originalFilename
        );
    }

    /**
     * 判断文件是否为图片
     * @param bytes 文件字节
     * @param suffix 文件后缀
     * @return 是否为图片
     */

    private boolean isRealImage(byte[] bytes, String suffix) {
        if (bytes.length < 12) {
            return false;
        }

        // PNG: 89 50 4E 47
        boolean isPng = (bytes[0] & 0xFF) == 0x89
                && bytes[1] == 0x50
                && bytes[2] == 0x4E
                && bytes[3] == 0x47;

        // JPG/JPEG: FF D8 FF
        boolean isJpg = (bytes[0] & 0xFF) == 0xFF
                && (bytes[1] & 0xFF) == 0xD8
                && (bytes[2] & 0xFF) == 0xFF;

        // WEBP: RIFF....WEBP
        boolean isWebp = bytes[0] == 0x52
                && bytes[1] == 0x49
                && bytes[2] == 0x46
                && bytes[3] == 0x46
                && bytes[8] == 0x57
                && bytes[9] == 0x45
                && bytes[10] == 0x42
                && bytes[11] == 0x50;


        return switch (suffix) {
            case ".png" -> isPng;
            case ".jpg" -> isJpg;
            case ".webp" -> isWebp;
            default -> false;
        };
    }

    /**
     *
     * 校验是否是图片
     * @param bytes 文件字节
     * @param originalFilename 文件名
     * @param contentType 文件类型
     */
    private void validateImageFile(byte[] bytes, String originalFilename, String contentType) {

        if (bytes == null || bytes.length == 0) {
            throw new BizException(400, "上传文件不能为空");
        }
        String suffix = getSuffix(originalFilename).toLowerCase();

        if (!ALLOWED_SUFFIXES.contains(suffix)) {
            throw new BizException(400, "仅支持png、jpg、webp、jpeg图片.");
        }

        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType.toLowerCase())) {
            throw new BizException(400, "文件类型不支持，仅允许上传图片");
        }

        if (!isRealImage(bytes, suffix)) {
            throw new BizException(400, "文件内容不是有效图片");
        }

    }

}
