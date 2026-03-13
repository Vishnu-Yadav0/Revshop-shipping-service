package com.revshop.shippingservice.repository;

import com.revshop.shippingservice.model.TrackingDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TrackingDetailsRepository extends JpaRepository<TrackingDetails, Long> {
    List<TrackingDetails> findByOrderId(Long orderId);
    List<TrackingDetails> findByShipperId(Long shipperId);
}
