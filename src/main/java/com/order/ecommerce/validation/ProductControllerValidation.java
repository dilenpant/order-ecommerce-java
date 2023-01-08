package com.order.ecommerce.validation;

import com.order.ecommerce.dto.ProductDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
@Slf4j
public class ProductControllerValidation {

    public void validateArgument(ProductDto productDto) {
        validateArgument(productDto == null, "Product cannot be null");
        validateArgument(productDto.getProductId() == null || productDto.getProductId().isEmpty(), "Product Id cannot be null or empty");
        validateArgument(productDto.getSku() == null || productDto.getSku().isEmpty(), "Product Sku cannot be null or empty");
        validateArgument(productDto.getTitle() == null || productDto.getTitle().isEmpty(), "Product Title cannot be null");
        validateArgument(productDto.getDescription() == null || productDto.getDescription().isEmpty(), "Product Description cannot be empty");
    }
    public void validateArgument(boolean condition, String message) {
        if (condition) {
            log.error("Error while processing request with message = {}", message);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
        }
    }
}
