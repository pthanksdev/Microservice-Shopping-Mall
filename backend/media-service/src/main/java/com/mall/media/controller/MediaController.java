package com.mall.media.controller;

import com.mall.common.response.ApiResponse;
import com.mall.media.service.MediaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/media")
@RequiredArgsConstructor
@Tag(name = "Media", description = "File uploads and storage")
public class MediaController {

    private final MediaService mediaService;

    @PostMapping("/upload/product")
    @Operation(summary = "Upload a product image with thumbnail")
    public ResponseEntity<ApiResponse<String>> uploadProductImage(@RequestParam("file") MultipartFile file) throws Exception {
        String fileName = mediaService.uploadImageWithThumbnail(file);
        return ResponseEntity.ok(ApiResponse.success("Image uploaded", fileName));
    }

    @GetMapping("/url")
    @Operation(summary = "Get presigned URL for a file")
    public ResponseEntity<ApiResponse<String>> getUrl(@RequestParam String bucket, @RequestParam String fileName) throws Exception {
        return ResponseEntity.ok(ApiResponse.success(mediaService.getFileUrl(bucket, fileName)));
    }
}
