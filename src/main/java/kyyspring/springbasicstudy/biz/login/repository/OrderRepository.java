package kyyspring.springbasicstudy.biz.login.repository;

import kyyspring.springbasicstudy.biz.login.domain.Order;
import kyyspring.springbasicstudy.biz.login.domain.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    // 사용자별 주문 조회
    List<Order> findByUserIdOrderByCreatedAtDesc(Long userId);

    // 상태별 주문 조회
    List<Order> findByStatusOrderByCreatedAtDesc(OrderStatus status);

    // 사용자별 + 상태별 주문 조회
    List<Order> findByUserIdAndStatusOrderByCreatedAtDesc(Long userId, OrderStatus status);

    // 사용자별 주문 수 조회
    @Query("SELECT COUNT(o) FROM Order o WHERE o.userId = :userId")
    Integer countByUserId(@Param("userId") Long userId);

    // 날짜 범위별 주문 조회
    @Query("SELECT o FROM Order o WHERE o.createdAt BETWEEN :startDate AND :endDate ORDER BY o.createdAt DESC")
    List<Order> findByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    // 사용자별 총 주문 금액
    @Query("SELECT COALESCE(SUM(o.totalPrice), 0) FROM Order o WHERE o.userId = :userId AND o.status != 'CANCELLED'")
    BigDecimal getTotalOrderAmountByUserId(@Param("userId") Long userId);

    // 특정 기간 동안의 주문 통계
    @Query("SELECT o.status, COUNT(o) FROM Order o WHERE o.createdAt >= :since GROUP BY o.status")
    List<Object[]> getOrderStatisticsSince(@Param("since") LocalDateTime since);

    // 추적번호로 주문 조회
    Optional<Order> findByTrackingNumber(String trackingNumber);

    // 페이징을 지원하는 사용자별 주문 조회
    Page<Order> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);
}