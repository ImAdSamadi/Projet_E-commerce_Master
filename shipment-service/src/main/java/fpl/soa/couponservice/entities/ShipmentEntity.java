package fpl.soa.couponservice.entities;

import fpl.soa.couponservice.enums.ShipmentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;

@Entity @Data @AllArgsConstructor @NoArgsConstructor @Builder
public class ShipmentEntity {
    @Id
    private String shipmentId ;
    private String orderId ;
    @Enumerated(EnumType.STRING)
    private ShipmentStatus status ;
    private LocalDate shippedDate ;
    private LocalDate estimatedDeliveryDate;
    private LocalDate deliveredDate ;
    private String trackingNumber ;
    private String origin ;
    private String destination ;

}
