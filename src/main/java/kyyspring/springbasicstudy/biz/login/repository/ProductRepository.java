package kyyspring.springbasicstudy.biz.login.repository;

import kyyspring.springbasicstudy.biz.login.domain.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // 기본 검색
    Optional<Product> findByProductCode(String productCode);

    List<Product> findByNameContainingIgnoreCase(String name);

    List<Product> findByCategoryIdAndEnabledTrue(Long categoryId);

    List<Product> findByBrandNameAndEnabledTrue(String brandName);

    // 재고 관련
    List<Product> findByStockQuantityLessThan(Integer threshold);

    List<Product> findByStockQuantityGreaterThan(Integer threshold);

    // 가격 범위 검색
    @Query("SELECT p FROM Product p WHERE p.price BETWEEN :minPrice AND :maxPrice AND p.enabled = true")
    List<Product> findByPriceRange(@Param("minPrice") BigDecimal minPrice, @Param("maxPrice") BigDecimal maxPrice);

    // 복합 검색
    @Query("SELECT p FROM Product p WHERE " +
            "(:keyword IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
            "(:categoryId IS NULL OR p.categoryId = :categoryId) AND " +
            "(:brandName IS NULL OR p.brandName = :brandName) AND " +
            "(:minPrice IS NULL OR p.price >= :minPrice) AND " +
            "(:maxPrice IS NULL OR p.price <= :maxPrice) AND " +
            "(:inStock IS NULL OR (:inStock = true AND p.stockQuantity > 0) OR (:inStock = false)) AND " +
            "(:enabled IS NULL OR p.enabled = :enabled)")
    Page<Product> searchProducts(
            @Param("keyword") String keyword,
            @Param("categoryId") Long categoryId,
            @Param("brandName") String brandName,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            @Param("inStock") Boolean inStock,
            @Param("enabled") Boolean enabled,
            Pageable pageable
    );

    // 통계 쿼리
    @Query("SELECT COUNT(p) FROM Product p WHERE p.enabled = true")
    Long countActiveProducts();

    @Query("SELECT COUNT(p) FROM Product p WHERE p.stockQuantity = 0 AND p.enabled = true")
    Long countOutOfStockProducts();

    @Query("SELECT p.categoryName, COUNT(p) FROM Product p WHERE p.enabled = true GROUP BY p.categoryName")
    List<Object[]> getProductCountByCategory();

    @Query("SELECT AVG(p.price) FROM Product p WHERE p.enabled = true")
    BigDecimal getAveragePrice();

    // 대량 업데이트
    @Modifying
    @Query("UPDATE Product p SET p.enabled = :enabled, p.updatedAt = :updatedAt WHERE p.id IN :ids")
    void bulkUpdateEnabled(@Param("ids") List<Long> ids, @Param("enabled") Boolean enabled, @Param("updatedAt") LocalDateTime updatedAt);

    @Modifying
    @Query("UPDATE Product p SET p.categoryId = :categoryId, p.categoryName = :categoryName, p.updatedAt = :updatedAt WHERE p.id IN :ids")
    void bulkUpdateCategory(@Param("ids") List<Long> ids, @Param("categoryId") Long categoryId, @Param("categoryName") String categoryName, @Param("updatedAt") LocalDateTime updatedAt);
}
