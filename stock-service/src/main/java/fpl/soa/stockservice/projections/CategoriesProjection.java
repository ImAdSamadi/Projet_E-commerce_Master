package fpl.soa.stockservice.projections;

import fpl.soa.stockservice.entities.Category;
import org.springframework.data.rest.core.config.Projection;

import java.util.List;

@Projection(name = "withCategory", types = { Category.class })
public interface CategoriesProjection {

    String getCategoryId();
    String getCategoryName();
    String getCategoryImageBase64();
    List<String> getCategoryColors();
    List<String> getCategorySizes();

}
