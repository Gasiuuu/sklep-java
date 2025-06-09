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
import org.springframework.transaction.annotation.Transactional;

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
import java.util.logging.Logger;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true) // <<< klucz do błędu “LogicalConnection… is closed”
public class UserServiceImpl implements UserService {
    private static final Logger log = Logger.getLogger(UserServiceImpl.class.getName());
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
        log.info("Rejestracja użytkownika " + registrationRequest.getEmail());
        ReqRes resp = new ReqRes();

        try {
            OurUsersEntity ourUser = new OurUsersEntity();
            ourUser.setEmail(registrationRequest.getEmail());
            ourUser.setCity(registrationRequest.getCity());
//            ourUser.setRole(registrationRequest.getRole());
            ourUser.setRole("USER");
            ourUser.setName(registrationRequest.getName());
            ourUser.setPassword(passwordEncoder.encode(registrationRequest.getPassword()));

            log.info("Zapisywanie użytkownika do bazy danych");
            OurUsersEntity ourUsersEntityResult = usersRepo.save(ourUser);
            if (ourUsersEntityResult.getId() > 0) {
                resp.setOurUsersEntity((ourUsersEntityResult));
                resp.setMessage("User Saved Successfully");
                resp.setStatusCode(200);
                log.info("User o ID " + ourUsersEntityResult.getId() + " zapisany pomyślnie");
            }
            mailService.sendPlainText(ourUser.getEmail(), "Potwierdzenie założenia konta", "Witaj, dziękujemy za założenie konta! 🎉");
            log.info("Wysłano email potwierdzający na adres: " + ourUser.getEmail());


        } catch (Exception e) {
            log.severe("Wystąpił błąd podczas rejestracji: " + e.getMessage());
            resp.setStatusCode(500);
            resp.setError(e.getMessage());
        }
        return resp;
    }


    @Override
    public ReqRes login(ReqRes loginRequest) {
        log.info("Logowanie użytkownika " + loginRequest.getEmail());
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

            log.info("Użytkownik zalogował się pomyślnie");

        } catch (Exception e) {
            log.severe("Wystąpił błąd podczas logowania użytkownika: " + e.getMessage());
            response.setStatusCode(500);
            response.setMessage(e.getMessage());
        }
        return response;
    }

    @Override
    public ReqRes refreshToken(ReqRes refreshTokenRequest) {
        log.info("Odświeżanie tokenu dla żądania");
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

                log.info("Token odświeżony dla " + ourEmail);
            }
            response.setStatusCode(200);
            return response;

        } catch (Exception e) {
            log.severe("Błąd podczas odświeżania tokenu: " + e.getMessage());
            response.setStatusCode(500);
            response.setMessage(e.getMessage());
            return response;
        }
    }


    @Override
    public ProductDto get_all_products() {
        log.info("Pobieranie wszystkich produktów");
        ProductDto reqRes = new ProductDto();

        try {
            List<ProductsEntity> result = productsRepo.findAll();
            if (!result.isEmpty()) {
                reqRes.setProductsEntityList(result);
                reqRes.setStatusCode(200);
                reqRes.setMessage("Successful");
                log.info("Znaleziono " + result.size() + " produktów");
            } else {
                reqRes.setStatusCode(404);
                reqRes.setMessage("No products found");
            }
            return reqRes;
        } catch (Exception e) {
            log.severe("Wystąpił błąd podczas pobierania produtów: " + e.getMessage());
            reqRes.setStatusCode(500);
            reqRes.setMessage("Error occurred: " + e.getMessage());
            return reqRes;
        }
    }
    @Override
    public ProductDto get_product_by_id(int id) {
        log.info("Pobieranie produktu o id " + id);
        ProductDto reqRes = new ProductDto();

        try {
            Optional<ProductsEntity> resultOptional = productsRepo.findById(id);

            reqRes.setProductsEntity(resultOptional.get());
            log.info("Produkt o id " + id + " pobrany");
            return reqRes;
        } catch (Exception e) {
            log.severe("Wystąpił bład podczas pobierania produktu o id " + id);
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
        log.info("Pobieranie zamówień dla użytkownika id " + userId);
        OrderDto orderDto = new OrderDto();

        try {
            List<Integer> orderIds = ordersRepo.findAll().stream()
                    .filter(order -> order.getOurUser().getId().equals(userId))
                    .map(OrdersEntity::getId)
                    .toList();

            log.info("Znalezniono id zamówień: " + orderIds);
            System.out.println(orderIds);

            if (!orderIds.isEmpty()) {

                List<OrdersEntity> ordersEntityList=new ArrayList<>();
                for (int i = 0; i < orderIds.size(); i++) {
                    OrdersEntity ordersEntity = ordersRepo.findById(orderIds.get(i)).get();
                    ordersEntity.setOurUser(null);
                    ordersEntityList.add(ordersEntity);
                }
                orderDto.setOrdersEntityList(ordersEntityList);
                orderDto.setMessage("Orders fetched successfully");
                orderDto.setStatusCode(200);
                log.info("Znaleziono zamówienia dla użytkownika id " + userId);
            } else {
                orderDto.setMessage("No orders found for user ID: " + userId);
                orderDto.setStatusCode(404);
            }
        } catch (Exception e) {
            log.severe("Wystąpił błąd podczas pobierania zamówień dla użytkownika " + userId + ": " + e.getMessage());
            orderDto.setStatusCode(500);
            orderDto.setError("Error occurred: " + e.getMessage());
        }

        return orderDto;
    }







    @Override
    public ProductAndNumberDto getProductsAndNumbersByOrderId(Integer orderId) {
        log.info("Pobieram produkty i ilości dla zamówień o id " + orderId);
        ProductAndNumberDto productAndNumberDto = new ProductAndNumberDto();

        try {
            Optional<OrdersEntity> orderOptional = ordersRepo.findById(orderId);
            if (orderOptional.isPresent()) {
                List<ProductAndNumberEntity> productsAndNumbers = orderOptional.get().getProductsAndNumbers();
                productAndNumberDto.setProductsAndNumbersList(productsAndNumbers);
                productAndNumberDto.setMessage("Products fetched successfully for order ID: " + orderId);
                productAndNumberDto.setStatusCode(200);
                log.info("Pomyślnie pobrano produkty i ilości dla zamówienia o id " + orderId);
            } else {
                productAndNumberDto.setMessage("Order not found for ID: " + orderId);
                productAndNumberDto.setStatusCode(404);
                log.info("Nie znaleziono zamówienia o id " + orderId);
            }
        } catch (Exception e) {
            log.severe("Wystąpił błąd podczas pobierania produktów dla zamówienia o id " + orderId + ": " + e.getMessage());
            productAndNumberDto.setStatusCode(500);
            productAndNumberDto.setError("Error occurred: " + e.getMessage());
        }

        return productAndNumberDto;
    }


    @Override
    @Transactional
    public ReqRes getAllUsers() {
        log.info("Pobieranie wszystkich użytkowników");
        ReqRes reqRes = new ReqRes();

        try {
            List<OurUsersEntity> result = usersRepo.findAll();
            if (!result.isEmpty()) {
                reqRes.setOurUsersEntityList(result);
                reqRes.setStatusCode(200);
                reqRes.setMessage("Successful");
                log.info("Znaleziono " + result.size() + " użytkowników");
            } else {
                reqRes.setStatusCode(404);
                reqRes.setMessage("No users found");
                log.info("Nie znaleziono żadnych użytkowników");
            }
            return reqRes;
        } catch (Exception e) {
            log.severe("Wystąpił błąd podczas pobierania użytkowników: " + e.getMessage());
            reqRes.setStatusCode(500);
            reqRes.setMessage("Error occurred: " + e.getMessage());
            return reqRes;
        }
    }

    @Override
    public ReqRes getUsersById(Integer id) {
        log.info("Pobieranie użytkownika o id " + id);
        ReqRes reqRes = new ReqRes();
        try {
            OurUsersEntity usersById = usersRepo.findById(id).orElseThrow(() -> new RuntimeException("User Not found"));
            reqRes.setOurUsersEntity(usersById);
            reqRes.setStatusCode(200);
            reqRes.setMessage("Users with id '" + id + "' found successfully");
            log.info("Znaleziono użytkownika o id " + id);
        } catch (Exception e) {
            log.severe("Wystąpił bład podczas pobierania użytkownika o id " + id + ": " + e.getMessage());
            reqRes.setStatusCode(500);
            reqRes.setMessage("Error occurred: " + e.getMessage());
        }
        return reqRes;
    }

    @Override
    @Transactional
    public ReqRes updateUser(Integer userId, ReqRes updatedUser) {
        log.info("Edytowanie użytkownika o id " + userId);
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
                log.info("Dane użytkownika nadpisane");
            } else {
                log.info("Nie znaleziono danego użytkownika");
                reqRes.setStatusCode(404);
                reqRes.setMessage("User not found for update");
            }
        } catch (Exception e) {
            log.severe("Wystąpił błąd podczas edycji użytkownika o id " + userId + ": " + e.getMessage());
            reqRes.setStatusCode(500);
            reqRes.setMessage("Error occurred while updating user: " + e.getMessage());
        }
        return reqRes;
    }

    @Override
    @Transactional
    public ReqRes deleteUser(Integer userId) {
        log.info("Usuwanie użytkownika o id " + userId);
        ReqRes reqRes = new ReqRes();
        try {
            Optional<OurUsersEntity> userOptional = usersRepo.findById(userId);
            if (userOptional.isPresent()) {
                usersRepo.deleteById(userId);
                reqRes.setStatusCode(200);
                reqRes.setMessage("User deleted successfully");
                log.info("Użytkownik pomyślnie usunięty");
            } else {
                log.info("Nie znaleziono danego użytkownika");
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
        log.info("Pobieranie informacji o użytkowniku " + email);
        ReqRes reqRes = new ReqRes();
        try {
            Optional<OurUsersEntity> userOptional = usersRepo.findByEmail(email);
            if (userOptional.isPresent()) {
                reqRes.setOurUsersEntity(userOptional.get());
                reqRes.setStatusCode(200);
                reqRes.setMessage("successful");
                log.info("Pobrano informacje dla konta o emailu " + email);
            } else {
                log.info("Nie znaleziono użytkownika o emailu " + email);
                reqRes.setStatusCode(404);
                reqRes.setMessage("User not found for update");
            }

        } catch (Exception e) {
            log.severe("Wystąpił błąd podczas pobierania danych dla konta o emailu " + email + ": " + e.getMessage());
            reqRes.setStatusCode(500);
            reqRes.setMessage("Error occurred while getting user info: " + e.getMessage());
        }
        return reqRes;

    }

    @Override
    @Transactional
    public OrderDto add_order(String email, OrderDto orderDto) {
        log.info("Dodawanie zamówienia dla dla konta " + email);
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
                log.info("Zamówienie pomyślnie złożone");

            }
        }catch(Exception e){
            log.severe("Wystąpił błąd podczas dodawania zamówienia " + e.getMessage());
            resp.setStatusCode(500);
            resp.setError(e.getMessage());
        }
        return resp;


    }

    @Override
    public int getIdByEmail(String email) {
        log.info("Pobieranie id konta " + email);
        var user = usersRepo.findByEmail(email);
        if (user.isPresent()) {
            log.info("Pobrano dla konta " + email + " id");
            return user.get().getId();
        }
        return 0;
    }

    @Override
    @Transactional
    public ProductDto add_product(String imagePath,ProductDto productDto) {
        log.info("Dodawanie nowego produktu");
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
                log.info("Pomyślnie dodano nowy produkt");
            }

        } catch (Exception e) {
            log.severe("Wystąpił błąd podczas dodawania nowego produktu: " + e.getMessage());
            resp.setStatusCode(500);
            resp.setError(e.getMessage());
        }
        return resp;
    }

    @Override
    @Transactional
    public ProductDto updateProduct(ProductDto productDto, Integer productId, String imagePath){
        log.info("Aktualizacja produktu nr " + productId);
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
        log.info("Produkt nr " + productId + " zaaktualizowany pomyślnie");
        return productDto;
    }

    @Override
    @Transactional
    public ProductDto deleteProduct(Integer productId) {
        log.info("Usuwanie produktu nr " + productId);
        ProductDto reqRes = new ProductDto();
        try {
            Optional<ProductsEntity> userOptional = productsRepo.findById(productId);
            if (userOptional.isPresent()) {
                productsRepo.deleteById(productId);
                reqRes.setStatusCode(200);
                reqRes.setMessage("Product deleted successfully");
                log.info("Produkt nr " + productId + " usunięty pomyślnie");

            } else {
                log.info("Nie znaleziono produktu nr " + productId);
                reqRes.setStatusCode(404);
                reqRes.setMessage("Product not found for deletion");
            }
        } catch (Exception e) {
            log.severe("Wystąpił błąd podczas usuwania produktu nr" + productId + " : " + e.getMessage());
            reqRes.setStatusCode(500);
            reqRes.setMessage("Error occurred while deleting product: " + e.getMessage());
        }
        return reqRes;
    }

    @Override
    @Transactional
    public OrderDto delete_order(Integer orderId) {
        log.info("Usuwanie zamówienia o id" + orderId);
        OrderDto reqRes = new OrderDto();
        try {
            Optional<OrdersEntity> userOptional = ordersRepo.findById(orderId);
            if (userOptional.isPresent()) {
                ordersRepo.deleteById(orderId);
                reqRes.setStatusCode(200);
                reqRes.setMessage("order deleted successfully");
                log.info("Zamówienie " + orderId + " usunięte pomyślnie");
            } else {
                log.info("Nie znaleziono zamówienia nr " + orderId);
                reqRes.setStatusCode(404);
                reqRes.setMessage("User not found for deletion");
            }
        } catch (Exception e) {
            log.severe("Wystąpił błąd poczas usuwania zamówienia nr " + orderId);
            reqRes.setStatusCode(500);
            reqRes.setMessage("Error occurred while deleting user: " + e.getMessage());
        }
        return reqRes;
    }

    @Override
    public OrderDto getAllOrders() {
        log.info("Pobieranie wszystkich zamówień");
        OrderDto orderDto = new OrderDto();

        try {
            List<OrdersEntity> ordersEntityList = ordersRepo.findAll();
            orderDto.setOrdersEntityList(ordersEntityList);
            orderDto.setMessage("Orders fetched successfully");
            orderDto.setStatusCode(200);
            log.info("Pomyślnie pobrano wszystkie zamówienia");

        } catch (Exception e) {
            log.severe("Wystąpił błąd podczas pobierania wszystkich zamówień: " + e.getMessage());
            orderDto.setStatusCode(500);
            orderDto.setError("Error occurred: " + e.getMessage());
        }

        return orderDto;
    }





}
