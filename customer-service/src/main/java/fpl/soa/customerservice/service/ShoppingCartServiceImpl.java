package fpl.soa.customerservice.service;


import fpl.soa.customerservice.RestClients.ProductRestClient;
import fpl.soa.customerservice.entities.Customer;
import fpl.soa.customerservice.entities.ShoppingCart;
import fpl.soa.customerservice.entities.ShoppingCartItem;
import fpl.soa.customerservice.exceptions.CustomerNotFoundException;
import fpl.soa.customerservice.model.AddItemRequest;
import fpl.soa.customerservice.model.Product;
import fpl.soa.customerservice.repos.CustomerRepo;
import fpl.soa.customerservice.repos.ShoppingCartRepo;
import org.keycloak.KeycloakSecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ShoppingCartServiceImpl implements ShoppingCartService {
    private CustomerRepo customerRepo ;
    private ShoppingCartRepo shoppingCartRepo ;
    private ProductRestClient productRestClient ;

    public ShoppingCartServiceImpl(CustomerRepo customerRepo, ShoppingCartRepo shoppingCartRepo, ProductRestClient productRestClient) {
        this.customerRepo = customerRepo;
        this.shoppingCartRepo = shoppingCartRepo;
        this.productRestClient = productRestClient;
    }

    @Override
    public ShoppingCartItem createItem(AddItemRequest addItemRequest) {

        Product p = this.productRestClient.getProduct(addItemRequest.getProductId()
                ,addItemRequest.getPickedSize(), addItemRequest.getPickedColor()) ;

        p.setPickedSize(addItemRequest.getPickedSize());
        p.setPickedColor(addItemRequest.getPickedColor());

        return ShoppingCartItem.builder()
                .product(p)
                .quantity(addItemRequest.getQuantity())
                .build();
    }

    @Override
    public ShoppingCart addItemToCart(AddItemRequest addItemRequest) throws CustomerNotFoundException {
        Customer customer = this.customerRepo.findById(addItemRequest.getCustomerId()).orElseThrow(()
                -> new CustomerNotFoundException("customer fo id *" + addItemRequest.getCustomerId() + "not found"));
        ShoppingCart shoppingCart = customer.getShoppingCart();
        ShoppingCartItem item = checkProductInCart(shoppingCart , addItemRequest.getProductId(), addItemRequest.getPickedSize(), addItemRequest.getPickedColor()) ;
        if(item != null){
            return updateItemInCart(item , addItemRequest , shoppingCart);
        }else{
            item = createItem(addItemRequest) ;
            shoppingCart.getItems().add(item) ;
            ShoppingCart savedCard = this.shoppingCartRepo.save(shoppingCart);
            customer.setShoppingCart(savedCard);
            this.customerRepo.save(customer);
            return savedCard ;
        }
    }

    @Override
    public ShoppingCart removeItemFromCart(String customerId, String productId, String size, String color) throws CustomerNotFoundException {
        Customer customer = this.customerRepo.findById(customerId).orElseThrow(()
                -> new CustomerNotFoundException("customer fo id *" + customerId + "not found"));
        ShoppingCart shoppingCart = customer.getShoppingCart();
        ShoppingCartItem item = checkProductInCart(shoppingCart , productId, size, color) ;
        if(item !=null){
            shoppingCart.getItems().remove(item) ;
            ShoppingCart savedCard = this.shoppingCartRepo.save(shoppingCart);
            customer.setShoppingCart(savedCard);
            this.customerRepo.save(customer);
            return savedCard ;
        }else throw  new RuntimeException("product not found in shoppingCart to delete") ;

    }

    @Override
    public ShoppingCart updateItemInCart(ShoppingCartItem item, AddItemRequest addItemRequest, ShoppingCart cart)   {
        int index = cart.getItems().indexOf(item) ;
        cart.getItems().get(index).setQuantity(item.getQuantity() + addItemRequest.getQuantity());
//        cart.getItems().get(index).getProduct().setPickedColor(addItemRequest.getPickedColor());
        return this.shoppingCartRepo.save(cart);

    }

    private ShoppingCartItem checkProductInCart(ShoppingCart cart, String productId, String selectedSize, String selectedColor) {
        for (ShoppingCartItem item : cart.getItems()) {
            Product product = item.getProduct();
            if (
                    productId.equals(product.getProductId()) &&
                            selectedSize.equalsIgnoreCase(product.getPickedSize()) &&
                            selectedColor.equalsIgnoreCase(product.getPickedColor())
            ) {
                return item;
            }
        }
        return null;
    }


    private String getToken(){
        KeycloakSecurityContext context = (KeycloakSecurityContext) SecurityContextHolder.getContext().getAuthentication().getCredentials();
        String token ="bearer "+ context.getTokenString();
        return token;
    }


}
