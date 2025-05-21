package com.sklep.sklep_backend.service.impl;

import com.sklep.sklep_backend.dto.OrderDto;
import com.sklep.sklep_backend.dto.ProductAndNumberDto;
import com.sklep.sklep_backend.dto.ProductDto;
import com.sklep.sklep_backend.dto.ReqRes;
import com.sklep.sklep_backend.entity.OrdersEntity;
import com.sklep.sklep_backend.entity.OurUsersEntity;
import com.sklep.sklep_backend.entity.ProductAndNumberEntity;
import com.sklep.sklep_backend.entity.ProductsEntity;
import com.sklep.sklep_backend.repository.OrdersRepo;
import com.sklep.sklep_backend.repository.ProductsRepo;
import com.sklep.sklep_backend.repository.UsersRepo;
import com.sklep.sklep_backend.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UsersRepo usersRepo;
    private final JWTUtilsImpl jwtUtils;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final ProductsRepo productsRepo;
    private final OrdersRepo ordersRepo;

    @Value("${BACKEND_URL}")
    private String BASE_URL;

    @Override
    public ReqRes register(ReqRes registrationRequest) {
        ReqRes resp = new ReqRes();

        try {
            OurUsersEntity ourUser = new OurUsersEntity();
            ourUser.setEmail(registrationRequest.getEmail());
            ourUser.setCity(registrationRequest.getCity());
//            ourUser.setRole(registrationRequest.getRole());
            ourUser.setRole("USER");
            ourUser.setName(registrationRequest.getName());
            ourUser.setPassword(passwordEncoder.encode(registrationRequest.getPassword()));
            OurUsersEntity ourUsersEntityResult = usersRepo.save(ourUser);
            if (ourUsersEntityResult.getId() > 0) {
                resp.setOurUsersEntity((ourUsersEntityResult));
                resp.setMessage("User Saved Successfully");
                resp.setStatusCode(200);
            }

        } catch (Exception e) {
            resp.setStatusCode(500);
            resp.setError(e.getMessage());
        }
        return resp;
    }


    @Override
    public ReqRes login(ReqRes loginRequest) {
        ReqRes response = new ReqRes();
        try {
            authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(),
                            loginRequest.getPassword()));
            var user = usersRepo.findByEmail(loginRequest.getEmail()).orElseThrow();
            var jwt = jwtUtils.generateToken(user);
            var refreshToken = jwtUtils.generateRefreshToken(new HashMap<>(), user);
            response.setStatusCode(200);
            response.setToken(jwt);
            response.setRole(user.getRole());
            response.setRefreshToken(refreshToken);
            response.setExpirationTime("24Hrs");
            response.setMessage("Successfully Logged In");

        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage(e.getMessage());
        }
        return response;
    }

    @Override
    public ReqRes refreshToken(ReqRes refreshTokenRequest) {
        ReqRes response = new ReqRes();
        try {
            String ourEmail = jwtUtils.extractUsername(refreshTokenRequest.getToken());
            OurUsersEntity users = usersRepo.findByEmail(ourEmail).orElseThrow();
            if (jwtUtils.isTokenValid(refreshTokenRequest.getToken(), users)) {
                var jwt = jwtUtils.generateToken(users);
                response.setStatusCode(200);
                response.setToken(jwt);
                response.setRefreshToken(refreshTokenRequest.getToken());
                response.setExpirationTime("24Hr");
                response.setMessage("Successfully Refreshed Token");
            }
            response.setStatusCode(200);
            return response;

        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage(e.getMessage());
            return response;
        }
    }


    @Override
    public ProductDto get_all_products() {
        ProductDto reqRes = new ProductDto();

        try {
            List<ProductsEntity> result = productsRepo.findAll();
            if (!result.isEmpty()) {
                reqRes.setProductsEntityList(result);
                reqRes.setStatusCode(200);
                reqRes.setMessage("Successful");
            } else {
                reqRes.setStatusCode(404);
                reqRes.setMessage("No products found");
            }
            return reqRes;
        } catch (Exception e) {
            reqRes.setStatusCode(500);
            reqRes.setMessage("Error occurred: " + e.getMessage());
            return reqRes;
        }
    }
    @Override
    public ProductDto get_product_by_id(int id) {
        ProductDto reqRes = new ProductDto();

        try {
            Optional<ProductsEntity> resultOptional = productsRepo.findById(id);

            reqRes.setProductsEntity(resultOptional.get());
            return reqRes;
        } catch (Exception e) {
            reqRes.setStatusCode(500);
            reqRes.setMessage("Error occurred: " + e.getMessage());
            return reqRes;
        }
    }









//    public ResponseEntity<String> saveImage(@RequestParam("file") MultipartFile file) throws IOException {
//        String imagePath = fileStorageService.storeFile(file);
//        return new ResponseEntity<>(imagePath, HttpStatus.CREATED);
//    }





    @Override
    public OrderDto getOrdersByUserId(Integer userId) {
        OrderDto orderDto = new OrderDto();

        try {
            // Pobieramy listę zamówień użytkownika
            List<Integer> orderIds = ordersRepo.findAll().stream()
                    .filter(order -> order.getOurUser().getId().equals(userId))
                    .map(OrdersEntity::getId)
                    .toList();

            System.out.println(orderIds);

            if (!orderIds.isEmpty()) {
                // Dodajemy listę zamówień do odpowiedzi
//                orderDto.setProducts_id_list(orderIds);
                List<OrdersEntity> ordersEntityList=new ArrayList<>();
                for (int i = 0; i < orderIds.size(); i++) {
                    OrdersEntity ordersEntity = ordersRepo.findById(orderIds.get(i)).get();
                    ordersEntity.setOurUser(null);
                    ordersEntityList.add(ordersEntity);
                }
                orderDto.setOrdersEntityList(ordersEntityList);
                orderDto.setMessage("Orders fetched successfully");
                orderDto.setStatusCode(200);
            } else {
                orderDto.setMessage("No orders found for user ID: " + userId);
                orderDto.setStatusCode(404);
            }
        } catch (Exception e) {
            orderDto.setStatusCode(500);
            orderDto.setError("Error occurred: " + e.getMessage());
        }

        return orderDto;
    }







    @Override
    public ProductAndNumberDto getProductsAndNumbersByOrderId(Integer orderId) {
        ProductAndNumberDto productAndNumberDto = new ProductAndNumberDto();

        try {
            Optional<OrdersEntity> orderOptional = ordersRepo.findById(orderId);
            if (orderOptional.isPresent()) {
                List<ProductAndNumberEntity> productsAndNumbers = orderOptional.get().getProductsAndNumbers();
                productAndNumberDto.setProductsAndNumbersList(productsAndNumbers);
                productAndNumberDto.setMessage("Products fetched successfully for order ID: " + orderId);
                productAndNumberDto.setStatusCode(200);
            } else {
                productAndNumberDto.setMessage("Order not found for ID: " + orderId);
                productAndNumberDto.setStatusCode(404);
            }
        } catch (Exception e) {
            productAndNumberDto.setStatusCode(500);
            productAndNumberDto.setError("Error occurred: " + e.getMessage());
        }

        return productAndNumberDto;
    }


    @Override
    public ReqRes getAllUsers() {
        ReqRes reqRes = new ReqRes();

        try {
            List<OurUsersEntity> result = usersRepo.findAll();
            if (!result.isEmpty()) {
                reqRes.setOurUsersEntityList(result);
                reqRes.setStatusCode(200);
                reqRes.setMessage("Successful");
            } else {
                reqRes.setStatusCode(404);
                reqRes.setMessage("No users found");
            }
            return reqRes;
        } catch (Exception e) {
            reqRes.setStatusCode(500);
            reqRes.setMessage("Error occurred: " + e.getMessage());
            return reqRes;
        }
    }

    @Override
    public ReqRes getUsersById(Integer id) {
        ReqRes reqRes = new ReqRes();
        try {
            OurUsersEntity usersById = usersRepo.findById(id).orElseThrow(() -> new RuntimeException("User Not found"));
            reqRes.setOurUsersEntity(usersById);
            reqRes.setStatusCode(200);
            reqRes.setMessage("Users with id '" + id + "' found successfully");
        } catch (Exception e) {
            reqRes.setStatusCode(500);
            reqRes.setMessage("Error occurred: " + e.getMessage());
        }
        return reqRes;
    }

    @Override
    public ReqRes updateUser(Integer userId, ReqRes updatedUser) {
        ReqRes reqRes = new ReqRes();
        try {
            Optional<OurUsersEntity> userOptional = usersRepo.findById(userId);
            if (userOptional.isPresent()) {
                OurUsersEntity existingUser = userOptional.get();
                existingUser.setEmail(updatedUser.getEmail());
                existingUser.setName(updatedUser.getName());
                existingUser.setCity(updatedUser.getCity());
                existingUser.setRole(updatedUser.getRole());

                if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
                    existingUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
                }

                OurUsersEntity savedUser = usersRepo.save(existingUser);
                reqRes.setOurUsersEntity(savedUser);
                reqRes.setStatusCode(200);
                reqRes.setMessage("User updated successfully");
            } else {
                reqRes.setStatusCode(404);
                reqRes.setMessage("User not found for update");
            }
        } catch (Exception e) {
            reqRes.setStatusCode(500);
            reqRes.setMessage("Error occurred while updating user: " + e.getMessage());
        }
        return reqRes;
    }

    @Override
    public ReqRes deleteUser(Integer userId) {
        ReqRes reqRes = new ReqRes();
        try {
            Optional<OurUsersEntity> userOptional = usersRepo.findById(userId);
            if (userOptional.isPresent()) {
                usersRepo.deleteById(userId);
                reqRes.setStatusCode(200);
                reqRes.setMessage("User deleted successfully");
            } else {
                reqRes.setStatusCode(404);
                reqRes.setMessage("User not found for deletion");
            }
        } catch (Exception e) {
            reqRes.setStatusCode(500);
            reqRes.setMessage("Error occurred while deleting user: " + e.getMessage());
        }
        return reqRes;
    }

}
