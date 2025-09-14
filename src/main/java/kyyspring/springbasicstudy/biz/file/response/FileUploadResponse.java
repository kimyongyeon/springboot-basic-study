package kyyspring.springbasicstudy.biz.file.response;

import lombok.Builder;
import lombok.Data;
import org.apache.commons.io.FileUtils;

import java.time.LocalDateTime;

@Data
@Builder
public class FileUploadResponse {
    private String originalFileName;
    private String savedFileName;
    private long fileSize;
    private String contentType;
    private String uploadPath;
    private LocalDateTime uploadDate;
    private String downloadUrl;

    // 파일 크기를 읽기 쉬운 형태로 변환
    public String getFormattedFileSize() {
        return FileUtils.byteCountToDisplaySize(fileSize);
    }
}
