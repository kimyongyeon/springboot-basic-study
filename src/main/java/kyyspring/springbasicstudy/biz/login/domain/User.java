package kyyspring.springbasicstudy.biz.login.domain;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column
    private String password; // 비회원은 null 가능

    @Column
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserType userType;

    @Column(name = "session_id")
    private String sessionId; // 비회원용

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    @Column(nullable = false)
    private Boolean enabled = true;

    // ✅ 추가 프로필 필드들
    @Column(name = "phone_number")
    private String phoneNumber;

    @Column
    private String address;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(name = "profile_image_url")
    private String profileImageUrl;

    // 기본 생성자
    public User() {}

    // 회원용 생성자
    public User(String email, String password, String name) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.userType = UserType.MEMBER;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.enabled = true;
    }

    // 비회원용 생성자
    public User(String sessionId) {
        this.email = "guest_" + sessionId + "@temp.com";
        this.userType = UserType.GUEST;
        this.sessionId = sessionId;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.enabled = true;
    }

    // 편의 메서드
    public boolean isMember() {
        return UserType.MEMBER.equals(this.userType);
    }

    public boolean isGuest() {
        return UserType.GUEST.equals(this.userType);
    }
}