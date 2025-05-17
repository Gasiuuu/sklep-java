package com.sklep.sklep_backend.repository;

import com.sklep.sklep_backend.entity.ProductsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductsRepo extends JpaRepository<ProductsEntity, Integer> {
    Optional<ProductsEntity> findByName(String name);
    Optional<ProductsEntity> findById(Integer id);
}
