package fpl.soa.couponservice.service;

import fpl.soa.couponservice.entities.ShipmentEntity;

import java.util.List;

public interface ShipmentService {
    ShipmentEntity createShipment(ShipmentEntity shipmentEntity);
    ShipmentEntity trackShipment(String trackingNumber);
    List<ShipmentEntity> getShipments();
    ShipmentEntity getShipmentById(Long id);

    ShipmentEntity getShipmentByOrderId(String orderId);

}
