package kyyspring.springbasicstudy.biz.login.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.URL;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Valid
public class CreateProductRequest {

    @NotBlank(message = "상품명은 필수입니다")
    @Size(min = 2, max = 100, message = "상품명은 2-100자 사이여야 합니다")
    private String name;

    @Size(max = 1000, message = "상품 설명은 1000자를 초과할 수 없습니다")
    private String description;

    @NotNull(message = "가격은 필수입니다")
    @DecimalMin(value = "0.01", message = "가격은 0.01 이상이어야 합니다")
    @DecimalMax(value = "999999.99", message = "가격은 999,999.99를 초과할 수 없습니다")
    private BigDecimal price;

    @NotNull(message = "재고 수량은 필수입니다")
    @Min(value = 0, message = "재고 수량은 0 이상이어야 합니다")
    @Max(value = 99999, message = "재고 수량은 99,999를 초과할 수 없습니다")
    private Integer stockQuantity;

    private Long categoryId;

    @Size(max = 50, message = "카테고리명은 50자를 초과할 수 없습니다")
    private String categoryName;

    @Size(max = 50, message = "브랜드명은 50자를 초과할 수 없습니다")
    private String brandName;

    @Pattern(regexp = "^[A-Z0-9-]{3,20}$", message = "상품코드는 3-20자의 영문 대문자, 숫자, 하이픈만 사용 가능합니다")
    private String productCode;

    @URL(message = "올바른 이미지 URL 형식이 아닙니다")
    private String imageUrl;

    private Boolean enabled = true;

    // 추가 필드들
    @Size(max = 100, message = "제조사는 100자를 초과할 수 없습니다")
    private String manufacturer;

    @Size(max = 50, message = "원산지는 50자를 초과할 수 없습니다")
    private String countryOfOrigin;

    @DecimalMin(value = "0.001", message = "무게는 0.001kg 이상이어야 합니다")
    private BigDecimal weight;

    @Size(max = 20, message = "색상은 20자를 초과할 수 없습니다")
    private String color;

    @Size(max = 20, message = "크기는 20자를 초과할 수 없습니다")
    private String size;

    private List<String> tags = new ArrayList<>();

    // 기본 생성자
    public CreateProductRequest() {}

    // 전체 생성자
    public CreateProductRequest(String name, String description, BigDecimal price,
                                Integer stockQuantity, String categoryName, String brandName) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.categoryName = categoryName;
        this.brandName = brandName;
    }

    // Getter, Setter
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public Integer getStockQuantity() { return stockQuantity; }
    public void setStockQuantity(Integer stockQuantity) { this.stockQuantity = stockQuantity; }

    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    public String getBrandName() { return brandName; }
    public void setBrandName(String brandName) { this.brandName = brandName; }

    public String getProductCode() { return productCode; }
    public void setProductCode(String productCode) { this.productCode = productCode; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public Boolean getEnabled() { return enabled; }
    public void setEnabled(Boolean enabled) { this.enabled = enabled; }

    public String getManufacturer() { return manufacturer; }
    public void setManufacturer(String manufacturer) { this.manufacturer = manufacturer; }

    public String getCountryOfOrigin() { return countryOfOrigin; }
    public void setCountryOfOrigin(String countryOfOrigin) { this.countryOfOrigin = countryOfOrigin; }

    public BigDecimal getWeight() { return weight; }
    public void setWeight(BigDecimal weight) { this.weight = weight; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public String getSize() { return size; }
    public void setSize(String size) { this.size = size; }

    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }
}