package kyyspring.springbasicstudy.biz.login.response;

import kyyspring.springbasicstudy.biz.login.domain.User;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserProfileResponse {
    private Long id;
    private String email;
    private String name;
    private String userType;
    private Boolean enabled;
    private LocalDateTime createdAt;
    private LocalDateTime lastLoginAt;

    // 기본 생성자
    public UserProfileResponse() {}

    // User 엔티티로부터 생성하는 생성자
    public UserProfileResponse(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.name = user.getName();
        this.userType = user.getUserType().name().toLowerCase();
        this.enabled = user.getEnabled();
        this.createdAt = user.getCreatedAt();
        this.lastLoginAt = user.getLastLoginAt();
    }

    // 전체 파라미터 생성자
    public UserProfileResponse(Long id, String email, String name, String userType,
                               Boolean enabled, LocalDateTime createdAt, LocalDateTime lastLoginAt) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.userType = userType;
        this.enabled = enabled;
        this.createdAt = createdAt;
        this.lastLoginAt = lastLoginAt;
    }

    @Override
    public String toString() {
        return "UserProfileResponse{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", name='" + name + '\'' +
                ", userType='" + userType + '\'' +
                ", enabled=" + enabled +
                ", createdAt=" + createdAt +
                ", lastLoginAt=" + lastLoginAt +
                '}';
    }
}
