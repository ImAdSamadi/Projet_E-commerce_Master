package fpl.soa.stockservice.repository;


import fpl.soa.stockservice.DTO.CategoryWithQuantity;
import fpl.soa.stockservice.entities.Category;
import fpl.soa.stockservice.projections.CategoriesProjection;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource(excerptProjection = CategoriesProjection.class)
public interface CategoryRepo extends MongoRepository<Category, String>, CategoryRepoCustom {


}
