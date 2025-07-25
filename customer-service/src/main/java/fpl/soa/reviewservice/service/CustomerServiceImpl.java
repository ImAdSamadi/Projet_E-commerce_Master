package fpl.soa.reviewservice.service;

import fpl.soa.reviewservice.entities.Customer;
import fpl.soa.reviewservice.entities.ShoppingCart;
import fpl.soa.reviewservice.entities.ShoppingCartItem;
import fpl.soa.reviewservice.repos.CustomerRepo;
import fpl.soa.reviewservice.repos.ShoppingCartRepo;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CustomerServiceImpl implements CustomerService {

    private CustomerRepo customerRepo ;
    private ShoppingCartRepo shoppingCartRepo ;

    private  String ADMIN_PASSWORD ;
    private  String ADMIN_USERNAME ;
    private  String KEYCLOAK_URL ;
    private String keycloakRealm ;
    private Keycloak keycloak ;

    public CustomerServiceImpl(CustomerRepo customerRepo, ShoppingCartRepo shoppingCartRepo ,
                               @Value("${keycloak.auth-server-url}") String KEYCLOAK_URL ,
                               @Value("${admin.password}") String ADMIN_PASSWORD ,
                               @Value("${admin.username}") String ADMIN_USERNAME  ,
                               @Value("${keycloak.realm}") String keycloakRealm) {
        this.customerRepo = customerRepo;
        this.shoppingCartRepo = shoppingCartRepo;
        this.KEYCLOAK_URL = KEYCLOAK_URL ;
        this.ADMIN_PASSWORD = ADMIN_PASSWORD ;
        this.ADMIN_USERNAME = ADMIN_USERNAME  ;
        this.keycloakRealm = keycloakRealm ;
        this.keycloak = KeycloakBuilder.builder()
                .serverUrl(this.KEYCLOAK_URL)
                .realm("master")
                .clientId("admin-cli")
                .grantType("password")
                .username(this.ADMIN_USERNAME)
                .password(this.ADMIN_PASSWORD)
                .build();
    }



    @Override
    public Customer createCustomer(Customer customer) {
        ShoppingCart cart = ShoppingCart.builder()
                .id(UUID.randomUUID().toString())
                .items(new ArrayList<>())
                .customerId(customer.getCustomerId()).build();
        ShoppingCart insertedCart = this.shoppingCartRepo.insert(cart);
        customer.setShoppingCart(insertedCart);
        return  this.customerRepo.insert(customer);
    }

    @Override
    public void syncKeycloakUsers() {
        List<UserRepresentation> keycloakUsers = keycloak.realm(keycloakRealm).users().list();
        for (UserRepresentation keycloakUser : keycloakUsers) {
            Optional<Customer> optionalUser = customerRepo.findById(keycloakUser.getId());
            Customer customer ;
            if(optionalUser.isPresent()){
                // updating the user
                customer = optionalUser.get();
            }else {
                // initializing user info
                customer = new  Customer() ;
                ShoppingCart cart = ShoppingCart.builder()
                        .id(UUID.randomUUID().toString())
                        .items(new ArrayList<>())
                        .customerId(keycloakUser.getId()).build();
                ShoppingCart insertedCart = this.shoppingCartRepo.insert(cart);
                customer.setShoppingCart(insertedCart);
            }
            customer.setEmail(keycloakUser.getEmail());
            customer.setCustomerId(keycloakUser.getId());
            customer.setFirstName(keycloakUser.getFirstName());
            customer.setLastName(keycloakUser.getLastName());
            customerRepo.save(customer);
        }
    }


    @Override
    public Customer getCustomer(String customerId) {
        System.out.println(customerId);
        Customer customer = customerRepo.findById(customerId).orElse(null);
//        System.out.println(customer);
        return customer;
    }

    @Override
    public void clearSelectedItems(String customerId) {
        Customer customer = customerRepo.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found"));

        ShoppingCart cart = customer.getShoppingCart();
        if (cart != null && cart.getItems() != null) {
            List<ShoppingCartItem> filteredItems = cart.getItems().stream()
                    .filter(item -> !item.isSelected()) // Keep only unselected
                    .collect(Collectors.toList());

            cart.setItems(filteredItems);
            shoppingCartRepo.save(cart); // Save the updated cart directly
        }
    }



    public void clearUnselectedItems(String customerId) {
        Customer customer = customerRepo.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found"));

        ShoppingCart cart = customer.getShoppingCart();
        if (cart != null && cart.getItems() != null) {
            List<ShoppingCartItem> filteredItems = cart.getItems().stream()
                    .filter(ShoppingCartItem::isSelected) // Keep only selected
                    .collect(Collectors.toList());

            cart.setItems(filteredItems);
        }

        customerRepo.save(customer);
    }

    public void removeCartItemByProductId(String customerId, String productId) {
        Customer customer = customerRepo.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found"));

        ShoppingCart cart = customer.getShoppingCart();
        if (cart != null && cart.getItems() != null) {
            List<ShoppingCartItem> updatedItems = cart.getItems().stream()
                    .filter(item -> !item.getProduct().getProductId().equals(productId)) // Keep everything except this product
                    .collect(Collectors.toList());

            cart.setItems(updatedItems);
        }

        customerRepo.save(customer);
    }


    @Override
    public List<Customer> getAllCustomers() {
        return customerRepo.findAll();
    }

    @Override
    public String getCustomerProfilePictureBase64(String email) {
//        Customer customer = customerRepo.findByEmail(email);

        return customerRepo.findByEmail(email).getProfilePictureBase64();
    }

}
