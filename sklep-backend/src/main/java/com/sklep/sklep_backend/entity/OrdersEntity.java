package com.sklep.sklep_backend.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Table(name = "orders")
@Data
public class OrdersEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private OurUsersEntity ourUser;


    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "order_ProductAndNumber",
            joinColumns = @JoinColumn(name = "order_id"),
            inverseJoinColumns = @JoinColumn(name = "ProductAndNumber_id")
    )
    private List<ProductAndNumberEntity> productsAndNumbers;

}
