package com.eduplatform.gateway.controller;

import com.eduplatform.common.response.R;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.GetPresignedObjectUrlArgs;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.Map;
import java.util.UUID;

/**
 * 文件上传接口（MinIO）。
 */
@Slf4j
@Tag(name = "文件上传")
@RestController
@RequestMapping("/v1/file")
public class FileController {

    @Value("${minio.endpoint:http://localhost:9000}")
    private String endpoint;
    @Value("${minio.access-key:minioadmin}")
    private String accessKey;
    @Value("${minio.secret-key:minioadmin}")
    private String secretKey;
    @Value("${minio.bucket:edu-platform}")
    private String bucket;

    private MinioClient client;

    @PostConstruct
    public void init() {
        client = MinioClient.builder()
                .endpoint(endpoint)
                .credentials(accessKey, secretKey)
                .build();
    }

    @Operation(summary = "上传文件（返回文件名+预览URL）")
    @PostMapping("/upload")
    public R<Map<String, String>> upload(@RequestParam("file") MultipartFile file) {
        try {
            String originalName = file.getOriginalFilename();
            String ext = originalName != null && originalName.contains(".")
                    ? originalName.substring(originalName.lastIndexOf("."))
                    : "";
            String objectName = UUID.randomUUID().toString().replace("-", "") + ext;

            try (InputStream is = file.getInputStream()) {
                client.putObject(PutObjectArgs.builder()
                        .bucket(bucket)
                        .object(objectName)
                        .stream(is, file.getSize(), -1)
                        .contentType(file.getContentType())
                        .build());
            }

            String url = endpoint + "/" + bucket + "/" + objectName;
            log.info("文件上传成功: {} → {}", originalName, url);
            return R.ok(Map.of("objectName", objectName, "url", url, "originalName", originalName));
        } catch (Exception e) {
            log.error("文件上传失败", e);
            return R.fail(5002, "文件上传失败: " + e.getMessage());
        }
    }
}
