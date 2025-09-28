package kyyspring.springbasicstudy.biz.login.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Valid
public class UpdateStockRequest {

    @NotNull(message = "상품 ID는 필수입니다")
    private Long productId;

    @NotNull(message = "수량은 필수입니다")
    private Integer quantity;

    @NotNull(message = "재고 변경 타입은 필수입니다")
    private StockChangeType changeType; // ADD, SUBTRACT, SET

    @Size(max = 200, message = "변경 사유는 200자를 초과할 수 없습니다")
    private String reason;

    // 기본 생성자
    public UpdateStockRequest() {}

    // 전체 생성자
    public UpdateStockRequest(Long productId, Integer quantity, StockChangeType changeType, String reason) {
        this.productId = productId;
        this.quantity = quantity;
        this.changeType = changeType;
        this.reason = reason;
    }

    // Getter, Setter
    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public StockChangeType getChangeType() { return changeType; }
    public void setChangeType(StockChangeType changeType) { this.changeType = changeType; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}