package com.sklep.sklep_backend.controller;

import com.sklep.sklep_backend.dto.OrderDto;
import com.sklep.sklep_backend.dto.ProductAndNumberDto;
import com.sklep.sklep_backend.dto.ProductDto;
import com.sklep.sklep_backend.dto.ReqRes;
import com.sklep.sklep_backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static com.sklep.sklep_backend.constant.Constant.PHOTO_DIR;
import static org.springframework.http.MediaType.IMAGE_PNG_VALUE;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/auth/register")
    public ResponseEntity<ReqRes> register(@RequestBody ReqRes reg){
        return ResponseEntity.ok(userService.register(reg));
    }


    @PostMapping("/auth/login")
    public ResponseEntity<ReqRes> login(@RequestBody ReqRes req){
        return ResponseEntity.ok(userService.login(req));
    }

    @PostMapping("/auth/refresh")
    public ResponseEntity<ReqRes> refreshToken(@RequestBody ReqRes req){
        return ResponseEntity.ok(userService.refreshToken(req));
    }

    @GetMapping("/public/get-all-products")
    public ResponseEntity<ProductDto> get_all_product(){
        return ResponseEntity.ok(userService.get_all_products());

    }

    @GetMapping("/public/get-product-by-Id/{productId}")
    public ResponseEntity<ProductDto> get_product_by_id(@PathVariable Integer productId){
        return ResponseEntity.ok(userService.get_product_by_id(productId));

    }

    //zwraca jsona ze szczegółami danego zamówienia
    @GetMapping("/public/order-products/{orderId}")
    public ResponseEntity<ProductAndNumberDto> getProductsAndNumbersByOrderId(@PathVariable Integer orderId) {
        return ResponseEntity.ok(userService.getProductsAndNumbersByOrderId(orderId));
    }



    //poniżej są 2 getmapppingi które zwracają to samo
    //zwraca jsona z id zamówień danego użytkownika (rozpoznaje go na podstawie id w urlu)
    @GetMapping("/public/orders/{userId}")
    public ResponseEntity<OrderDto> getOrdersByUserId(@PathVariable Integer userId) {
        return ResponseEntity.ok(userService.getOrdersByUserId(userId));
    }

    @GetMapping(path="/public/product/image/{filename}", produces=IMAGE_PNG_VALUE)
    public byte[] getPhoto(@PathVariable("filename") String fileName) throws IOException {
        return Files.readAllBytes(Paths.get(PHOTO_DIR + fileName));
    }

    @GetMapping("/admin/get-all-users")
    public ResponseEntity<ReqRes> getAllUsers(){
        return ResponseEntity.ok(userService.getAllUsers());

    }

    @GetMapping("/admin/get-users/{userId}")
    public ResponseEntity<ReqRes> getUserById(@PathVariable Integer userId){
        return ResponseEntity.ok(userService.getUsersById(userId));

    }

    @PostMapping("/admin/update-user/{userId}")
    public ResponseEntity<ReqRes> updateUser(@PathVariable Integer userId, @RequestBody ReqRes reqres){
        return ResponseEntity.ok(userService.updateUser(userId, reqres));
    }

    @DeleteMapping("/admin/delete-user/{userId}")
    public ResponseEntity<ReqRes> deleteUser(@PathVariable Integer userId){
        return ResponseEntity.ok(userService.deleteUser(userId));
    }

}