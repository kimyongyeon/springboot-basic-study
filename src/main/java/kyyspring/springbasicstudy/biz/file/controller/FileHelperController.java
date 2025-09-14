package kyyspring.springbasicstudy.biz.file.controller;

import kyyspring.springbasicstudy.biz.comm.response.ApiResponse;
import kyyspring.springbasicstudy.biz.file.response.FileUploadResponse;
import kyyspring.springbasicstudy.biz.file.service.FileUploadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class FileHelperController {

    private final FileUploadService fileUploadService;

    @PostMapping("/upload")
    public ResponseEntity<ApiResponse<FileUploadResponse>> upload(
            @RequestParam("file") MultipartFile file) {

        try {
            FileUploadResponse result = fileUploadService.uploadFile(file);

            return ResponseEntity.ok(
                    ApiResponse.<FileUploadResponse>builder()
                            .success(true)
                            .message("파일 업로드가 완료되었습니다.")
                            .data(result)
                            .build()
            );

        } catch (RuntimeException e) {
            log.warn("잘못된 파일 업로드 시도: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.<FileUploadResponse>builder()
                            .success(false)
                            .message(e.getMessage())
                            .build());

        }
    }

    // 여러 파일 동시 업로드
    @PostMapping("/upload/multiple")
    public ResponseEntity<ApiResponse<List<FileUploadResponse>>> uploadMultiple(
            @RequestParam("files") MultipartFile[] files) {

        List<FileUploadResponse> results = Arrays.stream(files)
                .map(fileUploadService::uploadFile)
                .collect(Collectors.toList());

        return ResponseEntity.ok(
                ApiResponse.<List<FileUploadResponse>>builder()
                        .success(true)
                        .message(String.format("%d개 파일 업로드가 완료되었습니다.", results.size()))
                        .data(results)
                        .build()
        );
    }
}
