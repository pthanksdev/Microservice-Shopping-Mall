package com.mall.media.service;

import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.http.Method;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class MediaService {

    private final MinioClient minioClient;

    @Value("${minio.bucket.products}")
    private String productBucket;

    public String uploadFile(MultipartFile file, String bucket) throws Exception {
        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        
        try (InputStream is = file.getInputStream()) {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucket)
                            .object(fileName)
                            .stream(is, file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );
        }
        
        return fileName;
    }

    public String uploadImageWithThumbnail(MultipartFile file) throws Exception {
        String originalName = uploadFile(file, productBucket);
        
        // Generate thumbnail
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        Thumbnails.of(file.getInputStream())
                .size(200, 200)
                .outputFormat("jpg")
                .toOutputStream(os);
        
        byte[] thumbnailData = os.toByteArray();
        String thumbnailName = "thumb_" + originalName;
        
        try (InputStream is = new ByteArrayInputStream(thumbnailData)) {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(productBucket)
                            .object(thumbnailName)
                            .stream(is, thumbnailData.length, -1)
                            .contentType("image/jpeg")
                            .build()
            );
        }
        
        return originalName;
    }

    public String getFileUrl(String bucket, String fileName) throws Exception {
        return minioClient.getPresignedObjectUrl(
                GetPresignedObjectUrlArgs.builder()
                        .method(Method.GET)
                        .bucket(bucket)
                        .object(fileName)
                        .expiry(1, TimeUnit.HOURS)
                        .build()
        );
    }
}
