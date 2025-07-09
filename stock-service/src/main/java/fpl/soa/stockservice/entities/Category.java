package fpl.soa.stockservice.entities;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document
public class Category {

    @Id
    private String categoryId;
    private String categoryName;
    private String categoryImageBase64;

    private List<String> categoryColors;
    private List<String> categorySizes;


}
