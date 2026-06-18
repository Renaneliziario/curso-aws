package com.renan.aws.s3.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class S3Service {

    private final S3Client s3Client;

    private static final String BUCKET = "curso-s3-213180001857-us-east-2-an";

    public S3Service(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    public void upload(MultipartFile file, String prefix) throws IOException {
        String key = (prefix == null || prefix.isBlank())
                ? file.getOriginalFilename()
                : normalizePrefix(prefix) + file.getOriginalFilename();

        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(BUCKET)
                .key(key)
                .contentType(file.getContentType())
                .build();

        s3Client.putObject(request,
                software.amazon.awssdk.core.sync.RequestBody.fromBytes(file.getBytes()));
    }

    public ListResult list(String prefix) {
        String normalized = (prefix == null || prefix.isBlank()) ? "" : normalizePrefix(prefix);

        ListObjectsV2Request request = ListObjectsV2Request.builder()
                .bucket(BUCKET)
                .prefix(normalized)
                .delimiter("/")
                .build();

        ListObjectsV2Response response = s3Client.listObjectsV2(request);

        List<String> folders = response.commonPrefixes()
                .stream()
                .map(CommonPrefix::prefix)
                .toList();

        List<String> files = new ArrayList<>();
        for (S3Object obj : response.contents()) {
            if (!obj.key().equals(normalized)) {
                files.add(obj.key());
            }
        }

        return new ListResult(folders, files);
    }

    public ResponseInputStream<GetObjectResponse> getFile(String key) {
        return s3Client.getObject(GetObjectRequest.builder()
                .bucket(BUCKET)
                .key(key)
                .build());
    }

    public HeadObjectResponse getMetadata(String key) {
        return s3Client.headObject(HeadObjectRequest.builder()
                .bucket(BUCKET)
                .key(key)
                .build());
    }

    private String normalizePrefix(String prefix) {
        return prefix.endsWith("/") ? prefix : prefix + "/";
    }

    public record ListResult(List<String> folders, List<String> files) {}
}
