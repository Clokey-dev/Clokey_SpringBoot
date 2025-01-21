package com.clokey.server.global.infra.s3;

import org.springframework.web.multipart.MultipartFile;

public interface S3ImageService {
    String upload(MultipartFile image);
    void deleteImageFromS3(String imageAddress);
}
