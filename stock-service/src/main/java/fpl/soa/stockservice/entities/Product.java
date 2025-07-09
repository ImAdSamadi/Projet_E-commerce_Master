package fpl.soa.stockservice.entities;

import fpl.soa.stockservice.enums.ProductStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document
public class Product {
    @Id
    private String productId ;
    private String name;
    private Date addingDate;
    private String brand;
    private String originLocation;
    @Field
    private String categoryId;
    @Field
    private ProductStatus status;

    // sizes are the variants that affect price, description, dimension
    private List<SizeVariant> sizeVariants;

    // other general product fields as needed
}
