package kyyspring.springbasicstudy.biz.login.service;

import jakarta.persistence.EntityNotFoundException;
import kyyspring.springbasicstudy.biz.login.domain.User;
import kyyspring.springbasicstudy.biz.login.domain.UserType;
import kyyspring.springbasicstudy.biz.login.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Override
    @Transactional(readOnly = true)
    public User authenticateUser(String email, String password) {
        try {
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + email));

            if (!passwordEncoder.matches(password, user.getPassword())) {
                throw new BadCredentialsException("비밀번호가 일치하지 않습니다");
            }

            // 로그인 시간 업데이트
            updateLastLoginTime(user.getId());

            return user;
        } catch (Exception e) {
            logger.error("Authentication failed for email: {}", email, e);
            throw new AuthenticationException("인증에 실패했습니다") {};
        }
    }

    @Override
    public User registerMember(String email, String password, String name) {
        if (existsByEmail(email)) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다: " + email);
        }

        User user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setName(name);
        user.setUserType(UserType.MEMBER);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        user.setEnabled(true);

        User savedUser = userRepository.save(user);
        logger.info("New member registered: {}", email);

        return savedUser;
    }

    @Override
    @Transactional(readOnly = true)
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + email));
    }

    @Override
    @Transactional(readOnly = true)
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public void updateLastLoginTime(Long userId) {
        userRepository.updateLastLoginTime(userId, LocalDateTime.now());
        logger.debug("Updated last login time for user: {}", userId);
    }

    @Override
    public User createGuestUser(String sessionId) {
        User guestUser = new User();
        guestUser.setEmail("guest_" + sessionId + "@temp.com");
        guestUser.setUserType(UserType.GUEST);
        guestUser.setSessionId(sessionId);
        guestUser.setCreatedAt(LocalDateTime.now());
        guestUser.setUpdatedAt(LocalDateTime.now());
        guestUser.setEnabled(true);

        User savedGuest = userRepository.save(guestUser);
        logger.info("Guest user created with sessionId: {}", sessionId);

        return savedGuest;
    }

    // ✅ @PreAuthorize에서 사용할 메서드
    public boolean isActiveUser(String email) {
        try {
            User user = findByEmail(email);
            return user.getEnabled() && user.getUserType() == UserType.MEMBER;
        } catch (Exception e) {
            logger.error("User active status check failed", e);
            return false;
        }
    }
}
