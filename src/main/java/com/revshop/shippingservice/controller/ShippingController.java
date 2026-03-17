package com.revshop.shippingservice.controller;

import com.revshop.shippingservice.dto.ApiResponse;
import com.revshop.shippingservice.dto.ShipperDTO;
import com.revshop.shippingservice.model.Shipper;
import com.revshop.shippingservice.service.ShippingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/shippers")
public class ShippingController {

    private final ShippingService shippingService;

    public ShippingController(ShippingService shippingService) {
        this.shippingService = shippingService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ShipperDTO>>> getAllShippers() {
        return ResponseEntity.ok(new ApiResponse<>("Shippers fetched successfully", shippingService.getAllShippers()));
    }

    @GetMapping("/available")
    public ResponseEntity<ApiResponse<List<ShipperDTO>>> getAvailableShippers() {
        return ResponseEntity.ok(new ApiResponse<>("Available shippers fetched", shippingService.getAvailableShippers()));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ShipperDTO>> createShipper(@RequestBody Shipper shipper) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>("Shipper created successfully", shippingService.createShipper(shipper)));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<ShipperDTO>> login(@RequestBody java.util.Map<String, String> credentials) {
        return shippingService.login(credentials.get("email"), credentials.get("password"))
                .map(dto -> ResponseEntity.ok(new ApiResponse<>("Login successful", dto)))
                .orElse(ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse<>("Invalid credentials", null)));
    }

    @PostMapping("/{shipperId}/assign/{orderId}")
    public ResponseEntity<ApiResponse<Void>> assignShipper(@PathVariable Long shipperId, @PathVariable Long orderId) {
        shippingService.assignShipper(shipperId, orderId);
        return ResponseEntity.ok(new ApiResponse<>("Shipper assigned successfully", null));
    }

    @PatchMapping("/{shipperId}/availability")
    public ResponseEntity<ApiResponse<ShipperDTO>> updateAvailability(
            @PathVariable Long shipperId,
            @RequestParam Boolean available) {
        return ResponseEntity.ok(new ApiResponse<>("Availability updated", shippingService.updateAvailability(shipperId, available)));
    }

    @GetMapping("/{shipperId}/orders")
    public ResponseEntity<ApiResponse<List<Object>>> getOrders(@PathVariable Long shipperId) {
        return ResponseEntity.ok(new ApiResponse<>("Orders fetched for shipper", shippingService.getOrdersByShipper(shipperId)));
    }

    @PatchMapping("/{shipperId}/orders/{orderId}/status")
    public ResponseEntity<ApiResponse<Void>> updateOrderStatus(
            @PathVariable Long shipperId,
            @PathVariable Long orderId,
            @RequestParam String status) {
        shippingService.updateOrderStatus(shipperId, orderId, status);
        return ResponseEntity.ok(new ApiResponse<>("Order status updated successfully", null));
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<ApiResponse<ShipperDTO>> getShipperByOrder(@PathVariable Long orderId) {
        return shippingService.getShipperByOrder(orderId)
                .map(dto -> ResponseEntity.ok(new ApiResponse<>("Shipper fetched", dto)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<String>> forgotPassword(@RequestBody java.util.Map<String, String> body) {
        String email = body.get("email");
        try {
            shippingService.generatePasswordResetToken(email);
            return ResponseEntity.ok(new ApiResponse<>("Password reset link sent to your email.", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(e.getMessage(), null));
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<String>> resetPassword(@RequestBody java.util.Map<String, String> body) {
        String token = body.get("token");
        String newPassword = body.get("newPassword");
        try {
            shippingService.resetPasswordWithToken(token, newPassword);
            return ResponseEntity.ok(new ApiResponse<>("Password reset successfully.", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(e.getMessage(), null));
        }
    }
}

