package fpl.soa.reviewservice.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document
public class Review {

    @Id
    private String reviewId;
    private String productId;
    private String customerName;
    private String customerEmail;
    private String customerProfileImageBase64   ;
    private String reviewText;
    private List<String> reviewImagesBase64;
    private int rating;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd MMM yyyy", locale = "en")
    private LocalDate reviewDate;


}
