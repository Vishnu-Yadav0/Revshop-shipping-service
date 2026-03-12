package com.revshop.shippingservice.controller;

import com.revshop.shippingservice.dto.ApiResponse;
import com.revshop.shippingservice.dto.TrackingDTO;
import com.revshop.shippingservice.service.ShippingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/tracking")
public class TrackingController {

    private final ShippingService shippingService;

    public TrackingController(ShippingService shippingService) {
        this.shippingService = shippingService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<TrackingDTO>> addTrackingDetail(
            @RequestParam Long orderId,
            @RequestBody TrackingDTO trackingDTO) {

        TrackingDTO savedByService = shippingService.addTrackingDetail(orderId, trackingDTO.getStatus(), trackingDTO.getDescription());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>("Tracking detail added successfully", savedByService));
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<ApiResponse<List<TrackingDTO>>> getTrackingByOrderId(@PathVariable Long orderId) {
        List<TrackingDTO> list = shippingService.getTrackingByOrderId(orderId);
        return ResponseEntity.ok(new ApiResponse<>("Tracking details fetched successfully", list));
    }
}
