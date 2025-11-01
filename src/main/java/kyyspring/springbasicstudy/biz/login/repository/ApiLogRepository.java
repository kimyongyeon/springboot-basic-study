package kyyspring.springbasicstudy.biz.login.repository;

import kyyspring.springbasicstudy.biz.login.domain.ApiLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ApiLogRepository extends JpaRepository<ApiLog, Long> {
    Optional<ApiLog> findByTxId(String txId);
}