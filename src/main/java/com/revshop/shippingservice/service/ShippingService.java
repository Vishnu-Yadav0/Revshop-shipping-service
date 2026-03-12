package com.revshop.shippingservice.service;

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

    public ShippingService(ShipperRepository shipperRepository, 
                          TrackingDetailsRepository trackingDetailsRepository,
                          PasswordEncoder passwordEncoder) {
        this.shipperRepository = shipperRepository;
        this.trackingDetailsRepository = trackingDetailsRepository;
        this.passwordEncoder = passwordEncoder;
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

    public List<Long> getOrdersByShipper(Long shipperId) {
        // Placeholder as this requires Sales Service interaction
        return List.of();
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
