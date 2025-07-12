package fpl.soa.shipmentservice.repos;

import fpl.soa.shipmentservice.entities.ShipmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ShipmentRepo extends JpaRepository<ShipmentEntity, Long> {

    Optional<ShipmentEntity> findByOrderId(String orderId);

}
