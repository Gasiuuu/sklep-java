package com.sklep.sklep_backend.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import com.sklep.sklep_backend.ProductsAndNumber;
import com.sklep.sklep_backend.entity.ProductAndNumberEntity;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProductAndNumberDto {

    private int statusCode;
    private String error;
    private String message;
    private List<ProductAndNumberEntity> productsAndNumbersList;
    private ProductsAndNumber productsAndNumber;


}
