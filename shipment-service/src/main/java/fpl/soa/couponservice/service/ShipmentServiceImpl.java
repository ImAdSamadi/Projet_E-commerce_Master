package fpl.soa.couponservice.service;

import fpl.soa.couponservice.entities.ShipmentEntity;
import fpl.soa.couponservice.repos.ShipmentRepo;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class ShipmentServiceImpl implements ShipmentService {

    private ShipmentRepo shipmentRepo ;

    public ShipmentServiceImpl(ShipmentRepo shipmentRepo) {
        this.shipmentRepo = shipmentRepo;
    }

    @Override
    public ShipmentEntity createShipment(ShipmentEntity shipmentEntity) {
        return shipmentRepo.save(shipmentEntity);
    }

    @Override
    public ShipmentEntity trackShipment(String trackingNumber) {
        return null;
    }

    @Override
    public List<ShipmentEntity> getShipments() {
        return List.of();
    }

    @Override
    public ShipmentEntity getShipmentById(Long id) {
        return null;
    }

    @Override
    public ShipmentEntity getShipmentByOrderId(String orderId) {
        ShipmentEntity shipment = shipmentRepo.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Shipment not found for orderId: " + orderId));
        return shipment;
    }

}
