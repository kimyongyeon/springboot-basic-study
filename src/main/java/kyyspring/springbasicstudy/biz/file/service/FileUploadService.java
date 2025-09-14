package kyyspring.springbasicstudy.biz.file.service;

import jakarta.annotation.PostConstruct;
import kyyspring.springbasicstudy.biz.file.response.FileUploadResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import static org.apache.commons.io.FilenameUtils.removeExtension;

@Service
@Slf4j
public class FileUploadService {

    @Value("${file.upload.path}")
    private String uploadPath;

    @Value("${file.upload.allowed-extensions}")
    private String allowedExtensions;

    @Value("${file.upload.max-size}")
    private long maxSize;

    @PostConstruct
    public void init() {
        createDirectoryIfNotExists(uploadPath);
    }

    public FileUploadResponse uploadFile(MultipartFile file) {
        validateFile(file);

        String originalFileName = file.getOriginalFilename();
        String savedFileName = generateUniqueFileName(originalFileName);
        String filePath = uploadPath + savedFileName;

        try {
            // 파일 저장
            Path targetPath = Paths.get(filePath);
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            // 응답 객체 생성
            return FileUploadResponse.builder()
                    .originalFileName(originalFileName)
                    .savedFileName(savedFileName)
                    .fileSize(file.getSize())
                    .contentType(file.getContentType())
                    .uploadPath(filePath)
                    .uploadDate(LocalDateTime.now())
                    .build();

        } catch (IOException e) {
            log.error("파일 저장 실패: {}", originalFileName, e);
            throw new RuntimeException("파일 저장에 실패했습니다.", e);
        }
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("파일이 비어있습니다.");
        }

        if (file.getSize() > maxSize) {
            throw new RuntimeException("파일 크기가 제한을 초과했습니다.");
        }

        String extension = getFileExtension(file.getOriginalFilename());
        if (!isAllowedExtension(extension)) {
            throw new RuntimeException("허용되지 않는 파일 형식입니다.");
        }
    }

    private String generateUniqueFileName(String originalFileName) {
        String extension = getFileExtension(originalFileName);
        String baseName = removeExtension(originalFileName);
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String uuid = UUID.randomUUID().toString().substring(0, 8);

        return String.format("%s_%s_%s.%s", baseName, timestamp, uuid, extension);
    }

    private String getFileExtension(String fileName) {
        return Optional.ofNullable(fileName)
                .filter(f -> f.contains("."))
                .map(f -> f.substring(f.lastIndexOf(".") + 1).toLowerCase())
                .orElse("");
    }

    private boolean isAllowedExtension(String extension) {
        return Arrays.asList(allowedExtensions.split(","))
                .contains(extension.toLowerCase());
    }

    private void createDirectoryIfNotExists(String path) {
        try {
            Path directory = Paths.get(path);
            if (!Files.exists(directory)) {
                Files.createDirectories(directory);
            }
        } catch (IOException e) {
            log.error("디렉토리 생성 실패: {}", path, e);
            throw new RuntimeException("업로드 디렉토리 생성에 실패했습니다.", e);
        }
    }
}