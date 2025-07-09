package fpl.soa.stockservice.repository;

import fpl.soa.stockservice.DTO.CategoryWithQuantity;
import fpl.soa.stockservice.entities.Category;

import java.util.List;

public interface CategoryRepoCustom {

    List<CategoryWithQuantity> getCategoriesWithProductsQuantity();

}
