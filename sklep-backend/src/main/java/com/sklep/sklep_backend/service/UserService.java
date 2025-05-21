package com.sklep.sklep_backend.service;

import com.sklep.sklep_backend.dto.OrderDto;
import com.sklep.sklep_backend.dto.ProductAndNumberDto;
import com.sklep.sklep_backend.dto.ProductDto;
import com.sklep.sklep_backend.dto.ReqRes;


public interface UserService{
    ReqRes register(ReqRes registrationRequest);
    ReqRes login(ReqRes loginRequest);
    ReqRes refreshToken(ReqRes refreshTokenRequest);
    ProductDto get_all_products();
    ProductDto get_product_by_id(int id);
    OrderDto getOrdersByUserId(Integer userId);
    ProductAndNumberDto getProductsAndNumbersByOrderId(Integer orderId);
    ReqRes getAllUsers();
    ReqRes getUsersById(Integer id);
    ReqRes updateUser(Integer id, ReqRes updatedUser);
    ReqRes deleteUser(Integer id);
}
