package fpl.soa.reviewservice.service;


import fpl.soa.reviewservice.entities.ShoppingCart;
import fpl.soa.reviewservice.entities.ShoppingCartItem;
import fpl.soa.reviewservice.exceptions.CustomerNotFoundException;
import fpl.soa.reviewservice.model.AddItemRequest;

public interface ShoppingCartService {

    ShoppingCartItem createItem(AddItemRequest addItemRequest ) ;
    ShoppingCart addItemToCart(AddItemRequest addItemRequest) throws CustomerNotFoundException;
    ShoppingCart removeItemFromCart(String customerId , String productId, String size, String color) throws CustomerNotFoundException;
    ShoppingCart updateItemInCart(ShoppingCartItem item, AddItemRequest addItemRequest, ShoppingCart cart) ;

}
