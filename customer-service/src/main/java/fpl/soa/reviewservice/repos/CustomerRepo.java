package fpl.soa.reviewservice.repos;


import fpl.soa.reviewservice.entities.Customer;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
public interface CustomerRepo extends MongoRepository<Customer, String> {

    Customer findByEmail(String email);

}
