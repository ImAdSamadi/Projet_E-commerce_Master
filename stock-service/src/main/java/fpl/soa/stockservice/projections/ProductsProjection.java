package fpl.soa.stockservice.projections;

import fpl.soa.stockservice.entities.Product;
import fpl.soa.stockservice.entities.SizeVariant;
import fpl.soa.stockservice.enums.ProductStatus;
import org.springframework.data.rest.core.config.Projection;

import java.util.Date;
import java.util.List;

@Projection(name = "withPrice", types = { Product.class })
public interface ProductsProjection {

    String getProductId();
    String getName();
    Date getAddingDate();
    String getCategoryId();
    ProductStatus getStatus();
    String getBrand();
    String getOriginLocation() ;

    List<SizeVariant> getSizeVariants();

}
