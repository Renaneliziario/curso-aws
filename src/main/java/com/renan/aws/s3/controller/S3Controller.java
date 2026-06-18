package com.renan.aws.s3.controller;

import com.renan.aws.s3.service.S3Service;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.HeadObjectResponse;

import java.io.IOException;

@RestController
@RequestMapping("/s3")
@Tag(name = "S3", description = "Upload, download e listagem de arquivos no Amazon S3")
public class S3Controller {

    private final S3Service service;

    public S3Controller(S3Service service) {
        this.service = service;
    }

    @Operation(summary = "Listar arquivos", description = "Lista arquivos e pastas. Sem prefix lista a raiz do bucket")
    @GetMapping("/list")
    public ResponseEntity<S3Service.ListResult> list(
            @RequestParam(value = "prefix", required = false) String prefix) {
        return ResponseEntity.ok(service.list(prefix));
    }

    @Operation(summary = "Upload de arquivo", description = "Faz upload de um arquivo para o bucket. Prefix é opcional (ex: Times/)")
    @PostMapping("/upload")
    public ResponseEntity<String> upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "prefix", required = false) String prefix) throws IOException {
        service.upload(file, prefix);
        return ResponseEntity.ok("Upload realizado com sucesso");
    }

    @Operation(summary = "Download de arquivo", description = "Baixa o arquivo pelo key (ex: Times/foto.png)")
    @GetMapping("/file")
    public ResponseEntity<InputStreamResource> getFile(@RequestParam("key") String key) {
        ResponseInputStream<GetObjectResponse> stream = service.getFile(key);
        GetObjectResponse meta = stream.response();

        String filename = key.contains("/") ? key.substring(key.lastIndexOf('/') + 1) : key;

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(
                        meta.contentType() != null ? meta.contentType() : MediaType.APPLICATION_OCTET_STREAM_VALUE))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                .body(new InputStreamResource(stream));
    }

    @Operation(summary = "Metadados do arquivo", description = "Retorna tamanho, tipo e data do arquivo sem baixar")
    @GetMapping("/metadata")
    public ResponseEntity<?> getMetadata(@RequestParam("key") String key) {
        HeadObjectResponse meta = service.getMetadata(key);
        return ResponseEntity.ok(new MetadataResponse(
                key,
                meta.contentLength(),
                meta.contentType(),
                meta.lastModified() != null ? meta.lastModified().toString() : null,
                meta.eTag()
        ));
    }

    public record MetadataResponse(String key, Long sizeBytes, String contentType, String lastModified, String eTag) {}
}
