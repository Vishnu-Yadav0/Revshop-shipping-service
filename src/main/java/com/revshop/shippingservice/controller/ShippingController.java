package com.revshop.shippingservice.controller;

import com.revshop.shippingservice.dto.ApiResponse;
import com.revshop.shippingservice.dto.ShipperDTO;
import com.revshop.shippingservice.model.Shipper;
import java.util.Optional;
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

    @PatchMapping("/{shipperId}/availability")
    public ResponseEntity<ApiResponse<ShipperDTO>> updateAvailability(
            @PathVariable Long shipperId,
            @RequestParam Boolean available) {
        return ResponseEntity.ok(new ApiResponse<>("Availability updated", shippingService.updateAvailability(shipperId, available)));
    }

    @GetMapping("/{shipperId}/orders")
    public ResponseEntity<ApiResponse<List<Long>>> getOrders(@PathVariable Long shipperId) {
        return ResponseEntity.ok(new ApiResponse<>("Orders fetched for shipper", shippingService.getOrdersByShipper(shipperId)));
    }
}

