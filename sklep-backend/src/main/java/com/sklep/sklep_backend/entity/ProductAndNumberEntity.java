package com.sklep.sklep_backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="productsAndNumberEntity")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductAndNumberEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @ManyToOne
    private ProductsEntity product;
    private Integer number;
}
