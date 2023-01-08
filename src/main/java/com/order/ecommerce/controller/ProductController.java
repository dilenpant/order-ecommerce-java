package com.order.ecommerce.controller;

import com.order.ecommerce.constant.CommonConstants;
import com.order.ecommerce.dto.ProductDto;
import com.order.ecommerce.service.IProductService;
import com.order.ecommerce.validation.ProductControllerValidation;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(CommonConstants.PRODUCTS_BASE_CONTEXT_PATH)
public class ProductController {

    private final IProductService productService;
    private final ProductControllerValidation validator;

    /**
     * Creates a product
     * @param productDto
     * @return
     */
    @PostMapping
    @Operation(summary = "Create a product", description = "Create a product")
    public ProductDto createProduct(@RequestBody ProductDto productDto) {
        validator.validateArgument(productDto);
        return productService.createProduct(productDto);
    }

    /**
     * Finds product by id
     * @param productId
     * @return
     */
    @GetMapping("/{productId}")
    @Operation(summary = "Find a product", description = "Find a product by id")
    public ProductDto findProductById(@PathVariable(name = "productId") String productId) {
        validator.validateArgument(productId == null || productId.isEmpty(), "Product Id cannot be null or empty");
        return productService.findProductById(productId);
    }

    /**
     * Find all products
     * @param offset
     * @param page
     * @return
     */
    @GetMapping
    @Operation(summary = "Find all products", description = "Find all the products")
    public List<ProductDto> findProducts(@RequestParam(defaultValue = "0") int offset, @RequestParam(defaultValue = "20") int page) {
        return productService.getAllProducts(offset, page);
    }

}