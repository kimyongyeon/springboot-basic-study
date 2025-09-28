package kyyspring.springbasicstudy.biz.login.service;

import kyyspring.springbasicstudy.biz.login.domain.User;

public interface UserService {
    User authenticateUser(String email, String password);
    User registerMember(String email, String password, String name);
    User findByEmail(String email);
    User findById(Long id);
    boolean existsByEmail(String email);
    void updateLastLoginTime(Long userId);
    User createGuestUser(String sessionId);
}
