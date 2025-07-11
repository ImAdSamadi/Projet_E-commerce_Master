package fpl.soa.reviewservice.repos;


import fpl.soa.reviewservice.entities.ShoppingCart;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
public interface ShoppingCartRepo extends MongoRepository<ShoppingCart, String> {
}
