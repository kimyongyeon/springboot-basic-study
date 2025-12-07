package kyyspring.springbasicstudy.biz.login.repository;

import kyyspring.springbasicstudy.biz.login.domain.User;
import kyyspring.springbasicstudy.biz.login.domain.UserType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    Optional<User> findBySessionId(String sessionId);

    List<User> findByUserType(UserType userType);

    @Modifying
    @Query("UPDATE User u SET u.lastLoginAt = :loginTime, u.updatedAt = :loginTime WHERE u.id = :userId")
    void updateLastLoginTime(@Param("userId") Long userId, @Param("loginTime") LocalDateTime loginTime);

    @Query("SELECT u FROM User u WHERE u.userType = :userType AND u.createdAt >= :since")
    List<User> findByUserTypeAndCreatedAtAfter(@Param("userType") UserType userType, @Param("since") LocalDateTime since);

    // 비활성 게스트 사용자 정리용
    @Query("SELECT u FROM User u WHERE u.userType = 'GUEST' AND u.updatedAt < :expireTime")
    List<User> findExpiredGuestUsers(@Param("expireTime") LocalDateTime expireTime);

    @Modifying
    @Query("DELETE FROM User u WHERE u.userType = 'GUEST' AND u.updatedAt < :expireTime")
    void deleteExpiredGuestUsers(@Param("expireTime") LocalDateTime expireTime);

//    Optional<User> findByUserId(String id);
}
