package kyyspring.springbasicstudy.biz.login.domain;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
@Data
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "product_name", nullable = false)
    private String productName;

    @Column(nullable = false)
    private Integer quantity;

    @Column(name = "unit_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal unitPrice;

    @Column(name = "total_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalPrice;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @Column(name = "shipping_address")
    private String shippingAddress;

    @Column(name = "shipping_phone")
    private String shippingPhone;

    @Column(name = "order_notes")
    private String orderNotes;

    @Column(name = "payment_method")
    private String paymentMethod;

    @Column(name = "payment_status")
    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

    @Column(name = "tracking_number")
    private String trackingNumber;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "confirmed_at")
    private LocalDateTime confirmedAt;

    @Column(name = "shipped_at")
    private LocalDateTime shippedAt;

    @Column(name = "delivered_at")
    private LocalDateTime deliveredAt;

    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;

    @Column(name = "cancel_reason")
    private String cancelReason;

    // 기본 생성자
    public Order() {}

    // 생성용 생성자
    public Order(Long userId, Long productId, String productName, Integer quantity, BigDecimal unitPrice) {
        this.userId = userId;
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.totalPrice = unitPrice.multiply(BigDecimal.valueOf(quantity));
        this.status = OrderStatus.PENDING;
        this.paymentStatus = PaymentStatus.PENDING;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // 비즈니스 메서드들
    public void confirm() {
        this.status = OrderStatus.CONFIRMED;
        this.confirmedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void ship(String trackingNumber) {
        if (this.status != OrderStatus.CONFIRMED) {
            throw new IllegalStateException("확인된 주문만 배송할 수 있습니다");
        }
        this.status = OrderStatus.SHIPPED;
        this.trackingNumber = trackingNumber;
        this.shippedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void deliver() {
        if (this.status != OrderStatus.SHIPPED) {
            throw new IllegalStateException("배송 중인 주문만 배송완료 처리할 수 있습니다");
        }
        this.status = OrderStatus.DELIVERED;
        this.deliveredAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void cancel(String reason) {
        if (this.status == OrderStatus.DELIVERED || this.status == OrderStatus.CANCELLED) {
            throw new IllegalStateException("배송완료 또는 이미 취소된 주문은 취소할 수 없습니다");
        }
        this.status = OrderStatus.CANCELLED;
        this.cancelReason = reason;
        this.cancelledAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public boolean canBeCancelled() {
        return this.status == OrderStatus.PENDING || this.status == OrderStatus.CONFIRMED;
    }

    public boolean isOwnedBy(Long userId) {
        return this.userId.equals(userId);
    }
}