package fpl.soa.couponservice.repos;

import fpl.soa.couponservice.entities.ShipmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ShipmentRepo extends JpaRepository<ShipmentEntity, Long> {

    Optional<ShipmentEntity> findByOrderId(String orderId);

}
