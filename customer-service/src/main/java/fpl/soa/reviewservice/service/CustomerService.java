package fpl.soa.reviewservice.service;


import fpl.soa.reviewservice.entities.Customer;

import java.util.List;

public interface CustomerService {

    Customer createCustomer(Customer customer) ;
    Customer getCustomer(String customerId);
    List<Customer> getAllCustomers();
    void syncKeycloakUsers();

}
