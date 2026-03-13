package com.revshop.shippingservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import com.revshop.shippingservice.dto.ApiResponse;

@FeignClient(name = "sales-service")
public interface SalesServiceClient {

    @GetMapping("/api/orders/{orderId}")
    ApiResponse<Object> getOrderById(@PathVariable("orderId") Long orderId);

    @org.springframework.web.bind.annotation.PutMapping("/api/orders/{orderId}/status")
    ApiResponse<Object> updateOrderStatus(@PathVariable("orderId") Long orderId, @org.springframework.web.bind.annotation.RequestParam("status") String status);
}
