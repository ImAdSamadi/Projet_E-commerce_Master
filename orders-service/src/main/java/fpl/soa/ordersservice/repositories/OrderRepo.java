package fpl.soa.ordersservice.repositories;


import fpl.soa.common.types.OrderStatus;
import fpl.soa.ordersservice.entities.OrderEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.UUID;

public interface OrderRepo extends MongoRepository<OrderEntity, String> {

    Page<OrderEntity> findByCustomerId(String customerId, Pageable pageable);

    Page<OrderEntity> findByCustomerIdAndStatus(String customerId, OrderStatus status, Pageable pageable); // ✅ fixed

}
