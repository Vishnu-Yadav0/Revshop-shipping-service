package com.revshop.shippingservice.repository;

import com.revshop.shippingservice.model.ShipperPasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ShipperPasswordResetTokenRepository extends JpaRepository<ShipperPasswordResetToken, Long> {
    Optional<ShipperPasswordResetToken> findByToken(String token);
    void deleteByEmail(String email);
}
