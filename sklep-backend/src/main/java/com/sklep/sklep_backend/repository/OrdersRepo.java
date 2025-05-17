package com.sklep.sklep_backend.repository;

import com.sklep.sklep_backend.entity.OrdersEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrdersRepo extends JpaRepository<OrdersEntity, Integer> {
//    OurUsers findByEmail(String email);
//    Optional<OrdersEntity> findByOrderId(int orderId);
}
