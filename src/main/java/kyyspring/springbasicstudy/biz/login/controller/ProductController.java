package kyyspring.springbasicstudy.biz.login.controller;

import jakarta.validation.Valid;
import kyyspring.springbasicstudy.biz.login.domain.Product;
import kyyspring.springbasicstudy.biz.login.request.*;
import kyyspring.springbasicstudy.biz.login.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    // 상품 생성
    @PostMapping
    @PreAuthorize("@securityExpressions.hasPermission(authentication.name, 'CREATE_PRODUCT')")
    public ResponseEntity<Product> createUserProduct(@RequestBody @Valid CreateProductRequest request) {
        Product product = productService.createProduct(request);
        return ResponseEntity.ok(product);
    }

    // 상품 수정
    @PutMapping("/{productId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Product> updateProduct(
            @PathVariable Long productId,
            @RequestBody @Valid UpdateProductRequest request) {
        Product product = productService.updateProduct(productId, request);
        return ResponseEntity.ok(product);
    }

    // 상품 검색
    @GetMapping("/search")
    public ResponseEntity<Page<Product>> searchProducts(
            @ModelAttribute @Valid ProductSearchRequest request) {
        Page<Product> products = productService.searchProducts(request);
        return ResponseEntity.ok(products);
    }

    // 재고 업데이트
    @PatchMapping("/stock")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Product> updateStock(@RequestBody @Valid UpdateStockRequest request) {
        Product product = productService.updateStock(request);
        return ResponseEntity.ok(product);
    }

    // 대량 업데이트
    @PatchMapping("/bulk")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Product>> bulkUpdate(@RequestBody @Valid BulkProductUpdateRequest request) {
        List<Product> products = productService.bulkUpdate(request);
        return ResponseEntity.ok(products);
    }
}
