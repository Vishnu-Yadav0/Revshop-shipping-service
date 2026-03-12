package com.revshop.shippingservice.service;

import com.revshop.shippingservice.dto.ShipperDTO;
import com.revshop.shippingservice.dto.TrackingDTO;
import com.revshop.shippingservice.exception.ResourceNotFoundException;
import com.revshop.shippingservice.model.Shipper;
import com.revshop.shippingservice.model.TrackingDetails;
import com.revshop.shippingservice.repository.ShipperRepository;
import com.revshop.shippingservice.repository.TrackingDetailsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ShippingServiceTest {

    @Mock
    private ShipperRepository shipperRepository;

    @Mock
    private TrackingDetailsRepository trackingDetailsRepository;

    @InjectMocks
    private ShippingService shippingService;

    private Shipper shipper;
    private TrackingDetails tracking;

    @BeforeEach
    void setUp() {
        shipper = new Shipper();
        shipper.setShipperId(1L);
        shipper.setName("John Doe");
        shipper.setIsAvailable(true);

        tracking = new TrackingDetails();
        tracking.setTrackingId(1L);
        tracking.setOrderId(100L);
        tracking.setStatus("SHIPPED");
        tracking.setDescription("Order is on the way");
    }

    @Test
    void updateAvailability_shouldChangeStatus_whenShipperExists() {
        when(shipperRepository.findById(1L)).thenReturn(Optional.of(shipper));
        when(shipperRepository.save(any(Shipper.class))).thenAnswer(i -> i.getArgument(0));

        ShipperDTO result = shippingService.updateAvailability(1L, false);

        assertThat(result.getIsAvailable()).isFalse();
        verify(shipperRepository).save(shipper);
    }

    @Test
    void updateAvailability_shouldThrowException_whenShipperNotFound() {
        when(shipperRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> shippingService.updateAvailability(999L, true))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void addTrackingDetail_shouldSaveAndReturnDTO() {
        when(trackingDetailsRepository.save(any(TrackingDetails.class))).thenReturn(tracking);

        TrackingDTO result = shippingService.addTrackingDetail(100L, "SHIPPED", "Order is on the way");

        assertThat(result.getOrderId()).isEqualTo(100);
        assertThat(result.getStatus()).isEqualTo("SHIPPED");
        verify(trackingDetailsRepository).save(any(TrackingDetails.class));
    }

    @Test
    void getTrackingByOrderId_shouldReturnList() {
        when(trackingDetailsRepository.findByOrderId(100L)).thenReturn(List.of(tracking));

        List<TrackingDTO> results = shippingService.getTrackingByOrderId(100L);

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getStatus()).isEqualTo("SHIPPED");
    }
}
