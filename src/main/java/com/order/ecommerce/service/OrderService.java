package com.order.ecommerce.service;

import com.order.ecommerce.dto.*;
import com.order.ecommerce.entity.*;
import com.order.ecommerce.enums.OrderStatus;
import com.order.ecommerce.enums.PaymentStatus;
import com.order.ecommerce.exception.ErrorInfo;
import com.order.ecommerce.exception.OrderNotFoundException;
import com.order.ecommerce.mapper.OrderDetailsMapper;
import com.order.ecommerce.repository.IAddressRepository;
import com.order.ecommerce.repository.IOrderItemRepository;
import com.order.ecommerce.repository.IOrderRepository;
import com.order.ecommerce.repository.IPaymentRepository;
import com.order.ecommerce.validation.OrderControllerValidation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService implements IOrderService {

    private final IOrderRepository orderRepository;
    private final IOrderItemRepository orderItemRepository;
    private final IPaymentRepository paymentRepository;
    private final IAddressRepository addressRepository;

    private final IProductService productService;
    private final OrderDetailsMapper orderDetailsMapper = Mappers.getMapper(OrderDetailsMapper.class);
    private final OrderControllerValidation validator;

    @Override
    @Transactional
    public OrderResponseDto createOrder(OrderDto orderDto) {
        log.info("Creating Order for customer = {}", orderDto.getCustomerId());
        validator.validateArgument(orderDto);
        log.info("Verifying all products exists before generating order");
        List<String> productIds = orderDto.getOrderItems().stream().map(orderItemDto -> orderItemDto.getProductId()).distinct().collect(Collectors.toList());
        List<ProductDto> products = productService.findAllById(productIds);
        if (products == null || products.isEmpty() || products.size() != productIds.size()) {
            log.info("Not all product(s) exist, failed to create order!");
            return null;
        }

        Order order = generateOrder(orderDto);
        log.info("Generated order for orderId = {}", order.getOrderId());

        Order savedOrder = orderRepository.save(order);
        String savedOrderId = savedOrder.getOrderId();
        List<OrderItem> orderItemList = buildOrderItems(orderDto.getOrderItems(), savedOrderId);
        orderItemRepository.saveAll(orderItemList);

        log.info("Successfully saved order & order items with id = {} for customer = {} on {}", savedOrder.getOrderId(),  savedOrder.getCustomerId(), savedOrder.getCreatedAt());

        return OrderResponseDto.builder()
                .orderId(savedOrderId)
                .orderStatus(savedOrder.getOrderStatus())
                .build();
    }

    @Override
    public OrderDto findOrderById(String orderId) {
        validator.validateArgument(orderId == null || orderId.isEmpty(), "order id cannot be null or empty");

        log.info("Finding order for orderId = {}", orderId);
        Optional<Order> order = orderRepository.findById(orderId);
        if (order.isEmpty()) {
            log.info("Cannot find order with id = {}", orderId);
            throw new OrderNotFoundException(ErrorInfo.builder().message("Cannot find order").field(orderId).build());
        }

        log.info("Successfully found order for orderId = {}", orderId);
        return orderDetailsMapper.toOrderDto(order.get());
    }

    @Override
    public void updateOrderStatus(String orderId, String status) {
        validator.validateArgument(orderId == null || orderId.isEmpty(), "order id cannot be null or empty");
        validator.validateArgument(status == null || status.isEmpty(), "order status cannot be null or empty");
        OrderDto orderDto = findOrderById(orderId);

        if (orderDto == null) {
            log.info("Cannot update status for orderId = {}", orderId);
            return;
        }

        List<OrderStatus> orderStatusList = Arrays.stream(OrderStatus.values()).filter(orderStatus -> orderStatus.toString().equalsIgnoreCase(status)).collect(Collectors.toList());
        if (orderStatusList.isEmpty()) {
            log.error("Invalid status = {}, failed to update order status for id = {}", status, orderId);
            return;
        }

        Order order = orderRepository.findById(orderId).get();
        order.setOrderStatus(status.toUpperCase());
        orderRepository.save(order);
        log.info("Successfully updated order status to = {} for order id = {}", status.toUpperCase(), orderId);
    }

    private Order generateOrder(OrderDto orderDto) {
        Order order = orderDetailsMapper.toOrderEntity(orderDto);
        order.setOrderId(UUID.randomUUID().toString());
        order.setCreatedAt(LocalDate.now());

        order.setOrderStatus(OrderStatus.PROCESSING.name());

        Payment payment = buildAndSavePayment(orderDto.getAmount(), orderDto.getPaymentMode());
        order.setPayment(payment);

        Address billingAddress = buildAndLoadAddress(orderDto.getBillingAddress());
        Address shippingAddress = buildAndLoadAddress(orderDto.getShippingAddress());
        order.setBillingAddress(billingAddress);
        order.setShippingAddress(shippingAddress);
        return order;
    }

    private List<OrderItem> buildOrderItems(List<OrderItemDto> orderItemsDtoList, String orderId) {
        List<OrderItem> orderItemList = orderItemsDtoList
                .stream()
                .map(orderItemDto -> new OrderItem(new OrderItemPk(orderItemDto.getProductId(), orderId), null, null, orderItemDto.getQuantity()))
                .collect(Collectors.toList());
        log.info("Saving order item list for order id = {}", orderId);
        return (List<OrderItem>) orderItemRepository.saveAll(orderItemList);
    }

    private Payment buildAndSavePayment(BigDecimal amount, String paymentMode) {
        Payment payment = Payment.builder().paymentId(UUID.randomUUID().toString())
                .amount(amount)
                .paymentMode(paymentMode)
                .paymentStatus(PaymentStatus.PROCESSING.name())
                .createdAt(LocalDate.now())
                .confirmationNumber(UUID.randomUUID().toString())
                .build();

        log.info("Saving payment details for payment id = {}", payment.getPaymentId());
        return paymentRepository.save(payment);
    }

    private Address buildAndLoadAddress(AddressDto addressDto) {
        Address addressEntity = orderDetailsMapper.toAddressEntity(addressDto);
        addressEntity.setAddressId(UUID.randomUUID().toString());
        addressEntity.setCreatedAt(LocalDate.now());
        log.info("Saving billing/shipping address for address id = {}", addressEntity.getAddressId());
        return addressRepository.save(addressEntity);
    }
}
