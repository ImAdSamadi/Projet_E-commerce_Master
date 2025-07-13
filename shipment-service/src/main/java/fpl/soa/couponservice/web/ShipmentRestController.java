package fpl.soa.couponservice.web;


import fpl.soa.couponservice.entities.ShipmentEntity;
import fpl.soa.couponservice.service.ShipmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/v1/shipments")
@RequiredArgsConstructor
public class ShipmentRestController {

    private final ShipmentService shipmentService;

    @GetMapping("/order/{orderId}")
    public ResponseEntity<ShipmentEntity> getShipmentByOrderId(@PathVariable String orderId) {
        ShipmentEntity shipment = shipmentService.getShipmentByOrderId(orderId);
        return ResponseEntity.ok(shipment);
    }
}
