package com.sklep.sklep_backend.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import com.sklep.sklep_backend.ProductsAndNumber;
import com.sklep.sklep_backend.entity.OrdersEntity;
import com.sklep.sklep_backend.entity.OurUsersEntity;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderDto {
    private int statusCode;
    private String error;
    private String message;
    private String token;
    private String refreshToken;
    private String expirationTime;
    private OurUsersEntity user_id;
    private List<ProductsAndNumber> productsAndNumbersList;
    private OrdersEntity ordersEntity;
    private List<OrdersEntity> ordersEntityList;
}
