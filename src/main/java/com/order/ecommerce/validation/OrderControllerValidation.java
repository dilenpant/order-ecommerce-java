package com.order.ecommerce.validation;

import com.order.ecommerce.dto.OrderDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
@Component
@Slf4j
public class OrderControllerValidation {
    public void validateArgument(OrderDto orderDto) {
        validateArgument(orderDto.getCustomerId() == null || orderDto.getCustomerId().isEmpty(), "customer id cannot be null or empty");
        validateArgument(orderDto.getTitle() == null || orderDto.getTitle().isEmpty(), "title cannot be null or empty");
        validateArgument(orderDto.getPaymentMode() == null || orderDto.getPaymentMode().isEmpty(), "payment mode cannot be null or empty");
        validateArgument(orderDto.getBillingAddress() == null, "billing address cannot be null");
        validateArgument(orderDto.getOrderItems() == null || orderDto.getOrderItems().isEmpty(), "order items cannot be null or empty");
        validateArgument(orderDto.getOrderStatus() == null || orderDto.getOrderStatus().isEmpty(), "order status cannot be null or empty");
    }
    public void validateArgument(boolean condition, String message) {
        if (condition) {
            log.error("Error while processing request with message = {}", message);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
        }
    }
}
