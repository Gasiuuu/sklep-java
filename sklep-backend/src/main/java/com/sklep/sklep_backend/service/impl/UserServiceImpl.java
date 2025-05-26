package com.sklep.sklep_backend.service.impl;

import com.sklep.sklep_backend.ProductsAndNumber;
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
import com.sklep.sklep_backend.service.MailService;
import com.sklep.sklep_backend.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
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
    private final MailService mailService;


    @Value("${BACKEND_URL}")
    private String BASE_URL;

    @Override
    @Transactional
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
            mailService.sendPlainText(ourUser.getEmail(), "Potwierdzenie założenia konta", "Witaj, dziękujemy za założenie konta! 🎉");



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
    @Transactional
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
    @Transactional
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

    @Override
    public ReqRes getMyInfo(String email) {
        ReqRes reqRes = new ReqRes();
        try {
            Optional<OurUsersEntity> userOptional = usersRepo.findByEmail(email);
            if (userOptional.isPresent()) {
                reqRes.setOurUsersEntity(userOptional.get());
                reqRes.setStatusCode(200);
                reqRes.setMessage("successful");
            } else {
                reqRes.setStatusCode(404);
                reqRes.setMessage("User not found for update");
            }

        } catch (Exception e) {
            reqRes.setStatusCode(500);
            reqRes.setMessage("Error occurred while getting user info: " + e.getMessage());
        }
        return reqRes;

    }

    @Override
    @Transactional
    public OrderDto add_order(String email, OrderDto orderDto) {
        OrderDto resp = new OrderDto();

        try {
            OrdersEntity order = new OrdersEntity();
            System.out.println("11111111111111");
            Optional<OurUsersEntity> userOptional = usersRepo.findByEmail(email);
            OurUsersEntity user = userOptional.orElse(null);
            order.setOurUser(user);
            System.out.println("2222222222222");
            List<ProductsAndNumber> productsAndNumbers = orderDto.getProductsAndNumbersList();
//            List<Products> all_products_list=productsRepo.findById();
            System.out.println("33333333333333");
            List<ProductAndNumberEntity> currentProductAndNumberEntityList = new ArrayList<ProductAndNumberEntity>();
            for (ProductsAndNumber productsAndNumber : productsAndNumbers) {

                Optional<ProductsEntity> productOptional = productsRepo.findById(productsAndNumber.getProductId());
                if (productOptional.isEmpty()) {
                    throw new EntityNotFoundException("Product with ID " + productsAndNumber.getProductId() + " not found");
                }

                ProductsEntity product = productOptional.get();
                ProductAndNumberEntity productAndNumberEntity=ProductAndNumberEntity.builder()
                        .product(product)
                        .number(productsAndNumber.getProductNumber())
                        .build();
                currentProductAndNumberEntityList.add(productAndNumberEntity);
            }
            System.out.println("4444444444444");

            order.setProductsAndNumbers(currentProductAndNumberEntityList);
            System.out.println("55555555555555");
            OrdersEntity orderResult = ordersRepo.save(order);
            System.out.println("66666666666666");
            if (orderResult.getId() > 0) {
                resp.setOrdersEntity(orderResult);
                resp.setMessage("Order succesfully added");
                resp.setStatusCode(200);

            }
        }catch(Exception e){
            resp.setStatusCode(500);
            resp.setError(e.getMessage());
        }
        return resp;


    }

    @Override
    public int getIdByEmail(String email) {
        var user = usersRepo.findByEmail(email);
        if (user.isPresent()) {
            return user.get().getId();
        }
        return 0;
    }

    @Override
    @Transactional
    public ProductDto add_product(String imagePath,ProductDto productDto) {
        ProductDto resp = new ProductDto();

        try {
            ProductsEntity product = ProductsEntity.builder()
                    .name(productDto.getName())
                    .category(productDto.getCategory())
                    .price(productDto.getPrice())
                    .description(productDto.getDescription())
                    .imageUrl(BASE_URL+"/public/product/image/"+imagePath)
                    .build();

//            Products product = new Products();
//            product.setName(productDto.getName());
//            product.setCategory(productDto.getCategory());
//            product.setPrice(productDto.getPrice());
            ProductsEntity productResult = productsRepo.save(product);
            if (productResult.getId() > 0) {
                resp.setProductsEntity((productResult));
                resp.setMessage("Product succesfully added");
                resp.setStatusCode(200);
            }

        } catch (Exception e) {
            resp.setStatusCode(500);
            resp.setError(e.getMessage());
        }
        return resp;
    }

    @Override
    @Transactional
    public ProductDto updateProduct(ProductDto productDto, Integer productId, String imagePath){
        System.out.println("11111");
        System.out.println(productDto);
        Optional<ProductsEntity> oldProductEntityOptional=productsRepo.findById(productId);
        ProductsEntity oldProductEntity=oldProductEntityOptional.get();

        String currentImageUrl=oldProductEntity.getImageUrl();
        if(imagePath!=""){
            currentImageUrl=BASE_URL+"/public/product/image/"+imagePath;
        }
        System.out.println(productDto.getName());
        ProductsEntity product = ProductsEntity.builder()
                .id(productId)
                .name(productDto.getName())
                .category(productDto.getCategory())
                .price(productDto.getPrice())
                .description(productDto.getDescription())
                .imageUrl(currentImageUrl)
                .build();

        productsRepo.save(product);
        return productDto;
    }

    @Override
    @Transactional
    public ProductDto deleteProduct(Integer productId) {
        ProductDto reqRes = new ProductDto();
        try {
            Optional<ProductsEntity> userOptional = productsRepo.findById(productId);
            if (userOptional.isPresent()) {
                productsRepo.deleteById(productId);
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

    @Override
    @Transactional
    public OrderDto delete_order(Integer orderId) {
        OrderDto reqRes = new OrderDto();
        try {
            Optional<OrdersEntity> userOptional = ordersRepo.findById(orderId);
            if (userOptional.isPresent()) {
                ordersRepo.deleteById(orderId);
                reqRes.setStatusCode(200);
                reqRes.setMessage("order deleted successfully");
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

    @Override
    public OrderDto getAllOrders() {
        OrderDto orderDto = new OrderDto();

        try {
            List<OrdersEntity> ordersEntityList = ordersRepo.findAll();
            orderDto.setOrdersEntityList(ordersEntityList);
            orderDto.setMessage("Orders fetched successfully");
            orderDto.setStatusCode(200);

        } catch (Exception e) {
            orderDto.setStatusCode(500);
            orderDto.setError("Error occurred: " + e.getMessage());
        }

        return orderDto;
    }





}
