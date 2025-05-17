package com.sklep.sklep_backend.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.sklep.sklep_backend.entity.ProductsEntity;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProductDto {

    private int statusCode;
    private String error;
    private String message;
    private String token;
    private String refreshToken;
    private String expirationTime;
    private String name;
    private String category;
    private String price;
    private String description;
    private ProductsEntity productsEntity;
    private List<ProductsEntity> productsEntityList;
//    private MultipartFile image;

}