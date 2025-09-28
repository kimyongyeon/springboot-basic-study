package kyyspring.springbasicstudy.biz.login.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.List;

@Valid
public class BulkProductUpdateRequest {

    @NotEmpty(message = "상품 ID 목록은 필수입니다")
    @Size(max = 100, message = "한 번에 최대 100개 상품까지 업데이트 가능합니다")
    private List<Long> productIds;

    private String action; // ENABLE, DISABLE, UPDATE_CATEGORY, UPDATE_PRICE

    // 카테고리 변경용
    private Long newCategoryId;
    private String newCategoryName;

    // 가격 변경용 (퍼센트 또는 고정값)
    @DecimalMin(value = "0", message = "가격 조정값은 0 이상이어야 합니다")
    private BigDecimal priceAdjustment;

    private String priceAdjustmentType; // PERCENT, FIXED

    // 활성화/비활성화용
    private Boolean enabled;

    @Size(max = 200, message = "변경 사유는 200자를 초과할 수 없습니다")
    private String reason;

    // 기본 생성자
    public BulkProductUpdateRequest() {}

    // Getter, Setter
    public List<Long> getProductIds() { return productIds; }
    public void setProductIds(List<Long> productIds) { this.productIds = productIds; }

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }

    public Long getNewCategoryId() { return newCategoryId; }
    public void setNewCategoryId(Long newCategoryId) { this.newCategoryId = newCategoryId; }

    public String getNewCategoryName() { return newCategoryName; }
    public void setNewCategoryName(String newCategoryName) { this.newCategoryName = newCategoryName; }

    public BigDecimal getPriceAdjustment() { return priceAdjustment; }
    public void setPriceAdjustment(BigDecimal priceAdjustment) { this.priceAdjustment = priceAdjustment; }

    public String getPriceAdjustmentType() { return priceAdjustmentType; }
    public void setPriceAdjustmentType(String priceAdjustmentType) { this.priceAdjustmentType = priceAdjustmentType; }

    public Boolean getEnabled() { return enabled; }
    public void setEnabled(Boolean enabled) { this.enabled = enabled; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}
