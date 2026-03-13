package com.revshop.shippingservice.service;

import com.revshop.shippingservice.dto.ApiResponse;

import com.revshop.shippingservice.dto.ShipperDTO;
import com.revshop.shippingservice.dto.TrackingDTO;
import com.revshop.shippingservice.exception.ResourceNotFoundException;
import com.revshop.shippingservice.model.Shipper;
import com.revshop.shippingservice.model.TrackingDetails;
import com.revshop.shippingservice.repository.ShipperRepository;
import com.revshop.shippingservice.repository.TrackingDetailsRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ShippingService {

    private final ShipperRepository shipperRepository;
    private final TrackingDetailsRepository trackingDetailsRepository;
    private final PasswordEncoder passwordEncoder;
    private final com.revshop.shippingservice.client.SalesServiceClient salesServiceClient;

    public ShippingService(ShipperRepository shipperRepository, 
                          TrackingDetailsRepository trackingDetailsRepository,
                          PasswordEncoder passwordEncoder,
                          com.revshop.shippingservice.client.SalesServiceClient salesServiceClient) {
        this.shipperRepository = shipperRepository;
        this.trackingDetailsRepository = trackingDetailsRepository;
        this.passwordEncoder = passwordEncoder;
        this.salesServiceClient = salesServiceClient;
    }

    public List<ShipperDTO> getAllShippers() {
        return shipperRepository.findAll().stream().map(this::convertToDTO).toList();
    }

    public List<ShipperDTO> getAvailableShippers() {
        return shipperRepository.findByIsAvailable(true).stream().map(this::convertToDTO).toList();
    }

    public ShipperDTO createShipper(Shipper shipper) {
        if (shipper.getPassword() != null) {
            shipper.setPassword(passwordEncoder.encode(shipper.getPassword()));
        }
        return convertToDTO(shipperRepository.save(shipper));
    }

    public Optional<ShipperDTO> login(String email, String password) {
        return shipperRepository.findByEmail(email)
                .filter(s -> passwordEncoder.matches(password, s.getPassword()))
                .map(this::convertToDTO);
    }

    public void deleteShipper(Long id) {
        shipperRepository.deleteById(id);
    }

    public ShipperDTO updateAvailability(Long shipperId, Boolean available) {
        Shipper shipper = shipperRepository.findById(shipperId)
                .orElseThrow(() -> new ResourceNotFoundException("Shipper not found with id: " + shipperId));
        shipper.setIsAvailable(available);
        return convertToDTO(shipperRepository.save(shipper));
    }

    public void assignShipper(Long shipperId, Long orderId) {
        Shipper shipper = shipperRepository.findById(shipperId)
                .orElseThrow(() -> new ResourceNotFoundException("Shipper not found with id: " + shipperId));

        TrackingDetails tracking = new TrackingDetails();
        tracking.setOrderId(orderId);
        tracking.setShipperId(shipperId);
        tracking.setStatus("PROCESSING");
        tracking.setDescription("Order assigned to shipper: " + shipper.getName());
        trackingDetailsRepository.save(tracking);

        // Advance status in Sales Service
        try {
            salesServiceClient.updateOrderStatus(orderId, "PROCESSING");
        } catch (Exception e) {
            // Log error but continue
        }
    }

    public List<Object> getOrdersByShipper(Long shipperId) {
        return trackingDetailsRepository.findByShipperId(shipperId).stream()
                .map(TrackingDetails::getOrderId)
                .distinct()
                .<Object>map(orderId -> {
                    try {
                        ApiResponse<Object> response = salesServiceClient.getOrderById(orderId);
                        return response != null ? response.getData() : null;
                    } catch (Exception e) {
                        return null;
                    }
                })
                .filter(java.util.Objects::nonNull)
                .toList();
    }

    public void updateOrderStatus(Long shipperId, Long orderId, String status) {
        TrackingDetails tracking = new TrackingDetails();
        tracking.setOrderId(orderId);
        tracking.setShipperId(shipperId);
        tracking.setStatus(status);
        tracking.setDescription("Order status updated to: " + status);
        trackingDetailsRepository.save(tracking);

        // Sync with Sales Service
        try {
            salesServiceClient.updateOrderStatus(orderId, status);
        } catch (Exception e) {
            throw new RuntimeException("Failed to sync status with Sales Service");
        }
    }

    public TrackingDTO addTrackingDetail(Long orderId, String status, String description) {
        TrackingDetails tracking = new TrackingDetails();
        tracking.setOrderId(orderId);
        tracking.setStatus(status);
        tracking.setDescription(description);
        return convertTrackingToDTO(trackingDetailsRepository.save(tracking));
    }

    public List<TrackingDTO> getTrackingByOrderId(Long orderId) {
        return trackingDetailsRepository.findByOrderId(orderId).stream()
                .map(this::convertTrackingToDTO)
                .toList();
    }

    public Optional<ShipperDTO> getShipperByOrder(Long orderId) {
        return trackingDetailsRepository.findByOrderId(orderId).stream()
                .filter(t -> t.getShipperId() != null)
                .findFirst()
                .flatMap(t -> shipperRepository.findById(t.getShipperId()))
                .map(this::convertToDTO);
    }

    private ShipperDTO convertToDTO(Shipper shipper) {
        ShipperDTO dto = new ShipperDTO();
        dto.setShipperId(shipper.getShipperId());
        dto.setName(shipper.getName());
        dto.setEmail(shipper.getEmail());
        dto.setPhone(shipper.getPhone());
        dto.setVehicleNumber(shipper.getVehicleNumber());
        dto.setIsAvailable(shipper.getIsAvailable());
        return dto;
    }

    private TrackingDTO convertTrackingToDTO(TrackingDetails tracking) {
        TrackingDTO dto = new TrackingDTO();
        dto.setTrackingId(tracking.getTrackingId());
        dto.setOrderId(tracking.getOrderId().intValue());
        dto.setStatus(tracking.getStatus());
        dto.setDescription(tracking.getDescription());
        dto.setCreatedAt(tracking.getCreatedAt());
        dto.setUpdatedAt(tracking.getUpdatedAt());
        return dto;
    }
}
