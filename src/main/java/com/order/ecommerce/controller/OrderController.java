package com.order.ecommerce.controller;

import com.order.ecommerce.constant.CommonConstants;
import com.order.ecommerce.dto.OrderDto;
import com.order.ecommerce.dto.OrderResponseDto;
import com.order.ecommerce.service.IOrderService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(CommonConstants.ORDER_BASE_CONTEXT_PATH)
public class OrderController {

    private final IOrderService orderService;

    /**
     * Creates order
     * @param orderDto
     * @return
     */
    @PostMapping
    @Operation(summary = "Create an order", description = "Create an order")
    public OrderResponseDto createOrder(@RequestBody OrderDto orderDto) {
        return orderService.createOrder(orderDto);
    }

    /**
     * Finds Order by Id
     * @param orderId
     * @return
     */
    @GetMapping("/{orderId}")
    @Operation(summary = "Find order", description = "Find order by id")
    public OrderDto findOrderBy(@PathVariable(name = "orderId") String orderId) {
        return orderService.findOrderById(orderId);
    }

    /**
     * Updates order status
     * @param orderId
     * @param orderStatus
     */
    @PatchMapping("/{orderId}")
    @Operation(summary = "Update order status", description = "Update order status")
    public void updateOrderStatus(@PathVariable("orderId") String orderId,
                                  @RequestParam(name = "orderStatus") String orderStatus) {
        orderService.updateOrderStatus(orderId, orderStatus);
    }

}
