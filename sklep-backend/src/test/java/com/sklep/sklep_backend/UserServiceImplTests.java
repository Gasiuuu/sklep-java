package com.sklep.sklep_backend.service.impl;

import com.sklep.sklep_backend.ProductsAndNumber;
import com.sklep.sklep_backend.dto.OrderDto;
import com.sklep.sklep_backend.dto.ProductAndNumberDto;
import com.sklep.sklep_backend.dto.ProductDto;
import com.sklep.sklep_backend.dto.ReqRes;
import com.sklep.sklep_backend.entity.OrdersEntity;
import com.sklep.sklep_backend.entity.ProductAndNumberEntity;
import com.sklep.sklep_backend.entity.OurUsersEntity;
import com.sklep.sklep_backend.entity.ProductsEntity;
import com.sklep.sklep_backend.repository.OrdersRepo;
import com.sklep.sklep_backend.repository.ProductsRepo;
import com.sklep.sklep_backend.repository.UsersRepo;
import com.sklep.sklep_backend.service.MailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import jakarta.persistence.EntityNotFoundException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTests {
    @Mock
    private UsersRepo usersRepo;
    @Mock
    private ProductsRepo productsRepo;
    @Mock
    private OrdersRepo ordersRepo;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JWTUtilsImpl jwtUtils;
    @Mock
    private MailService mailService;

    @InjectMocks
    private UserServiceImpl userService;

    private ReqRes userReq;
    private ProductDto productDto;

    @BeforeEach
    void setup() {
        ReflectionTestUtils.setField(userService, "BASE_URL", "http://localhost");
        userReq = new ReqRes();
        userReq.setName("test");
        userReq.setPassword("test");
        userReq.setEmail("test@test.com");
        userReq.setCity("test");

        productDto = new ProductDto();
        productDto.setName("iphone 16 pro");
        productDto.setPrice("5000");
        productDto.setCategory("test");
        productDto.setDescription("abc123");
    }

    @Test
    void registerSuccess() {
        OurUsersEntity saved = new OurUsersEntity();
        saved.setId(1);
        when(passwordEncoder.encode(eq("test"))).thenReturn("encodedTest");
        when(usersRepo.save(any(OurUsersEntity.class))).thenReturn(saved);

        ReqRes resp = userService.register(userReq);

        assertEquals(200, resp.getStatusCode());
        assertEquals("User Saved Successfully", resp.getMessage());
        assertEquals(saved, resp.getOurUsersEntity());
        verify(mailService).sendPlainText(
                eq("test@test.com"),
                eq("Potwierdzenie założenia konta"),
                eq("Witaj, dziękujemy za założenie konta! 🎉")
        );
    }

    @Test
    void registerFailure() {
        when(usersRepo.save(any())).thenThrow(new RuntimeException("db error"));

        ReqRes resp = userService.register(userReq);

        assertEquals(500, resp.getStatusCode());
        assertTrue(resp.getError().contains("db error"));
    }

    @Test
    void loginFailure() {
        when(authenticationManager.authenticate(any())).thenThrow(new RuntimeException("auth fail"));
        ReqRes req = new ReqRes();
        req.setEmail("test@test.com");
        req.setPassword("test");

        ReqRes resp = userService.login(req);
        assertEquals(500, resp.getStatusCode());
        assertTrue(resp.getMessage().contains("auth fail"));
    }

    @Test
    void refreshTokenSuccess() {
        ReqRes tokenReq = new ReqRes();
        tokenReq.setToken("oldToken");
        when(jwtUtils.extractUsername("oldToken")).thenReturn("test@test.com");
        OurUsersEntity user = new OurUsersEntity();
        when(usersRepo.findByEmail("test@test.com")).thenReturn(Optional.of(user));
        when(jwtUtils.isTokenValid("oldToken", user)).thenReturn(true);
        when(jwtUtils.generateToken(user)).thenReturn("newJwt");

        ReqRes resp = userService.refreshToken(tokenReq);
        assertEquals(200, resp.getStatusCode());
        assertEquals("newJwt", resp.getToken());
        assertEquals("oldToken", resp.getRefreshToken());
        assertEquals("Successfully Refreshed Token", resp.getMessage());
    }

    @Test
    void refreshTokenFailure() {
        ReqRes tokenReq = new ReqRes();
        tokenReq.setToken("bad");
        when(jwtUtils.extractUsername("bad")).thenThrow(new RuntimeException("invalid token"));

        ReqRes resp = userService.refreshToken(tokenReq);
        assertEquals(500, resp.getStatusCode());
        assertTrue(resp.getMessage().contains("invalid token"));
    }

    @Test
    void getAllProductsNotEmpty() {
        ProductsEntity prod = new ProductsEntity();
        when(productsRepo.findAll()).thenReturn(List.of(prod));

        ProductDto resp = userService.get_all_products();
        assertEquals(200, resp.getStatusCode());
        assertEquals("Successful", resp.getMessage());
        assertEquals(1, resp.getProductsEntityList().size());
    }

    @Test
    void getAllProductsEmpty() {
        when(productsRepo.findAll()).thenReturn(Collections.emptyList());

        ProductDto resp = userService.get_all_products();
        assertEquals(404, resp.getStatusCode());
        assertEquals("No products found", resp.getMessage());
    }

    @Test
    void getProductByIdSuccess() {
        ProductsEntity prod = new ProductsEntity();
        when(productsRepo.findById(1)).thenReturn(Optional.of(prod));

        ProductDto resp = userService.get_product_by_id(1);
        assertEquals(prod, resp.getProductsEntity());
    }

    @Test
    void getProductByIdFailure() {
        when(productsRepo.findById(1)).thenReturn(Optional.empty());

        ProductDto resp = userService.get_product_by_id(1);
        assertEquals(500, resp.getStatusCode());
        assertTrue(resp.getMessage().contains("Error occurred"));
    }

    @Test
    void getAllUsersNotEmpty() {
        OurUsersEntity user = new OurUsersEntity();
        when(usersRepo.findAll()).thenReturn(List.of(user));

        ReqRes resp = userService.getAllUsers();
        assertEquals(200, resp.getStatusCode());
        assertEquals(1, resp.getOurUsersEntityList().size());
        assertEquals("Successful", resp.getMessage());
    }

    @Test
    void getAllUsersEmpty() {
        when(usersRepo.findAll()).thenReturn(Collections.emptyList());

        ReqRes resp = userService.getAllUsers();
        assertEquals(404, resp.getStatusCode());
        assertEquals("No users found", resp.getMessage());
    }

    @Test
    void getUsersByIdSuccess() {
        OurUsersEntity user = new OurUsersEntity();
        when(usersRepo.findById(1)).thenReturn(Optional.of(user));

        ReqRes resp = userService.getUsersById(1);
        assertEquals(200, resp.getStatusCode());
        assertEquals(user, resp.getOurUsersEntity());
    }

    @Test
    void getUsersByIdFailure() {
        when(usersRepo.findById(1)).thenThrow(new RuntimeException("not found"));

        ReqRes resp = userService.getUsersById(1);
        assertEquals(500, resp.getStatusCode());
        assertTrue(resp.getMessage().contains("not found"));
    }

    @Test
    void getMyInfoSuccess() {
        OurUsersEntity user = new OurUsersEntity();
        when(usersRepo.findByEmail("test@test.com")).thenReturn(Optional.of(user));
        ReqRes resp = userService.getMyInfo("test@test.com");
        assertEquals(200, resp.getStatusCode());
        assertEquals(user, resp.getOurUsersEntity());
    }

    @Test
    void getMyInfoNotFound() {
        when(usersRepo.findByEmail("test@test.com")).thenReturn(Optional.empty());
        ReqRes resp = userService.getMyInfo("test@test.com");
        assertEquals(404, resp.getStatusCode());
    }

    @Test
    void getIdByEmailFound() {
        OurUsersEntity user = new OurUsersEntity();
        user.setId(42);
        when(usersRepo.findByEmail("test@test.com")).thenReturn(Optional.of(user));
        int id = userService.getIdByEmail("test@test.com");
        assertEquals(42, id);
    }

    @Test
    void getIdByEmailNotFound() {
        when(usersRepo.findByEmail("test@test.com")).thenReturn(Optional.empty());
        int id = userService.getIdByEmail("test@test.com");
        assertEquals(0, id);
    }

    @Test
    void addProductSuccess() {
        ProductsEntity saved = ProductsEntity.builder()
                .id(1)
                .name(productDto.getName())
                .category(productDto.getCategory())
                .price(productDto.getPrice())
                .description(productDto.getDescription())
                .build();
        when(productsRepo.save(any())).thenReturn(saved);

        ProductDto resp = userService.add_product("img.png", productDto);
        assertEquals(200, resp.getStatusCode());
        assertEquals(saved, resp.getProductsEntity());
    }

    @Test
    void addProductFailure() {
        when(productsRepo.save(any())).thenThrow(new RuntimeException("error"));
        ProductDto resp = userService.add_product("img.png", productDto);
        assertEquals(500, resp.getStatusCode());
        assertTrue(resp.getError().contains("error"));
    }

    @Test
    void getOrdersByUserIdSuccess() {
        OurUsersEntity user = new OurUsersEntity(); user.setId(1);
        OrdersEntity order = new OrdersEntity(); order.setId(10); order.setOurUser(user);
        when(ordersRepo.findAll()).thenReturn(List.of(order));
        when(ordersRepo.findById(10)).thenReturn(Optional.of(order));

        OrderDto resp = userService.getOrdersByUserId(1);
        assertEquals(200, resp.getStatusCode());
        assertEquals(1, resp.getOrdersEntityList().size());
        assertEquals(order, resp.getOrdersEntityList().get(0));
        assertEquals("Orders fetched successfully", resp.getMessage());
    }

    @Test
    void getOrdersByUserIdNoOrders() {
        when(ordersRepo.findAll()).thenReturn(Collections.emptyList());

        OrderDto resp = userService.getOrdersByUserId(1);
        assertEquals(404, resp.getStatusCode());
        assertTrue(resp.getMessage().contains("No orders found for user ID: 1"));
    }

    @Test
    void getProductsAndNumbersByOrderIdSuccess() {
        ProductAndNumberEntity pan = ProductAndNumberEntity.builder().number(2).build();
        OrdersEntity order = new OrdersEntity(); order.setProductsAndNumbers(List.of(pan));
        when(ordersRepo.findById(5)).thenReturn(Optional.of(order));

        ProductAndNumberDto resp = userService.getProductsAndNumbersByOrderId(5);
        assertEquals(200, resp.getStatusCode());
        assertEquals(1, resp.getProductsAndNumbersList().size());
        assertEquals(pan, resp.getProductsAndNumbersList().get(0));
    }

    @Test
    void getProductsAndNumbersByOrderIdNotFound() {
        when(ordersRepo.findById(5)).thenReturn(Optional.empty());

        ProductAndNumberDto resp = userService.getProductsAndNumbersByOrderId(5);
        assertEquals(404, resp.getStatusCode());
        assertTrue(resp.getMessage().contains("Order not found for ID: 5"));
    }

    @Test
    void updateProductWithNewImage() {
        ProductsEntity existing = ProductsEntity.builder().id(3).imageUrl("oldUrl").build();
        when(productsRepo.findById(3)).thenReturn(Optional.of(existing));
        ProductDto dto = new ProductDto();
        dto.setName("n"); dto.setCategory("c"); dto.setPrice("123"); dto.setDescription("d");

        ProductDto resp = userService.updateProduct(dto, 3, "new.png");
        assertEquals(dto, resp);
        verify(productsRepo).save(argThat(p -> p.getImageUrl().endsWith("new.png")));
    }

    @Test
    void updateProductWithoutNewImage() {
        ProductsEntity existing = ProductsEntity.builder().id(3).imageUrl("http://localhost/public/product/image/old.png").build();
        when(productsRepo.findById(3)).thenReturn(Optional.of(existing));
        ProductDto dto = new ProductDto();
        dto.setName("n"); dto.setCategory("c"); dto.setPrice("123"); dto.setDescription("d");

        ProductDto resp = userService.updateProduct(dto, 3, "");
        assertEquals(dto, resp);
        verify(productsRepo).save(argThat(p -> p.getImageUrl().equals(existing.getImageUrl())));
    }

    @Test
    void deleteProductSuccess() {
        ProductsEntity existing = new ProductsEntity();
        when(productsRepo.findById(4)).thenReturn(Optional.of(existing));

        ProductDto resp = userService.deleteProduct(4);
        assertEquals(200, resp.getStatusCode());
        assertEquals("User deleted successfully", resp.getMessage());
        verify(productsRepo).deleteById(4);
    }

    @Test
    void deleteProductNotFound() {
        when(productsRepo.findById(5)).thenReturn(Optional.empty());

        ProductDto resp = userService.deleteProduct(5);
        assertEquals(404, resp.getStatusCode());
        assertEquals("User not found for deletion", resp.getMessage());
    }

    @Test
    void deleteOrderSuccess() {
        OrdersEntity order = new OrdersEntity();
        when(ordersRepo.findById(6)).thenReturn(Optional.of(order));

        OrderDto resp = userService.delete_order(6);
        assertEquals(200, resp.getStatusCode());
        assertEquals("order deleted successfully", resp.getMessage());
        verify(ordersRepo).deleteById(6);
    }

    @Test
    void deleteOrderNotFound() {
        when(ordersRepo.findById(7)).thenReturn(Optional.empty());

        OrderDto resp = userService.delete_order(7);
        assertEquals(404, resp.getStatusCode());
        assertEquals("User not found for deletion", resp.getMessage());
    }

    @Test
    void getAllOrdersSuccess() {
        OrdersEntity o1 = new OrdersEntity();
        when(ordersRepo.findAll()).thenReturn(List.of(o1));

        OrderDto resp = userService.getAllOrders();
        assertEquals(200, resp.getStatusCode());
        assertEquals(1, resp.getOrdersEntityList().size());
        assertEquals("Orders fetched successfully", resp.getMessage());
    }

    @Test
    void getAllOrdersException() {
        when(ordersRepo.findAll()).thenThrow(new RuntimeException("db err"));

        OrderDto resp = userService.getAllOrders();
        assertEquals(500, resp.getStatusCode());
        assertTrue(resp.getError().contains("db err"));
    }
}
