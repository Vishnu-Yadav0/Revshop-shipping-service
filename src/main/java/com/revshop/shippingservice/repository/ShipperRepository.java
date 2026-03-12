package com.revshop.shippingservice.repository;

import com.revshop.shippingservice.model.Shipper;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ShipperRepository extends JpaRepository<Shipper, Long> {
    Optional<Shipper> findByEmail(String email);
    List<Shipper> findByIsAvailable(Boolean isAvailable);
}
