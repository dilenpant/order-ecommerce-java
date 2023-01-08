package com.order.ecommerce.service;

import com.order.ecommerce.dto.ProductDto;

import java.util.List;

public interface IProductService {

    ProductDto createProduct(ProductDto productDto);

    ProductDto findProductById(String productId);

    List<ProductDto> findAllById(List<String> ids);

    List<ProductDto> getAllProducts(int offSet, int pageSize);
}
