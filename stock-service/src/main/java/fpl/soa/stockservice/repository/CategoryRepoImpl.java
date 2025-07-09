// CategoryRepoImpl.java (important: match name to interface + "Impl")
package fpl.soa.stockservice.repository;

import fpl.soa.stockservice.DTO.CategoryWithQuantity;
import fpl.soa.stockservice.entities.Category;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;
import org.bson.Document;
import java.util.Collections;

import java.util.List;


@Repository
public class CategoryRepoImpl implements CategoryRepoCustom {

    @Autowired
    private MongoTemplate mongoTemplate;


//    @Override
//    public List<CategoryWithQuantity> getCategoriesWithProductsQuantity() {
//        Aggregation aggregation = Aggregation.newAggregation(
//                // Lookup products array for each category
//                Aggregation.lookup(
//                        "product",     // from collection
//                        "_id",         // localField in category
//                        "categoryId",  // foreignField in product
//                        "products"     // as products array
//                ),
//                // Add field 'productsQuantity' as size of products array
//                Aggregation.addFields()
//                        .addFieldWithValue("productsQuantity", new Document("$size", "$products"))
//                        .build(),
//                // Project required fields and rename _id to categoryId
//                Aggregation.project()
//                        .and("_id").as("categoryId")
//                        .andInclude("categoryName", "categoryImageBase64", "categoryColors", "categorySizes", "productsQuantity")
//        );
//
//        AggregationResults<CategoryWithQuantity> results =
//                mongoTemplate.aggregate(aggregation, "category", CategoryWithQuantity.class);
//
//        return results.getMappedResults();
//    }


    @Override
    public List<CategoryWithQuantity> getCategoriesWithProductsQuantity() {
        Aggregation aggregation = Aggregation.newAggregation(
                // Step 1: Lookup products for each category
                Aggregation.lookup("product", "_id", "categoryId", "products"),

                // Step 2: Add field categoryPrices as distinct prices (no sorting here)
                context -> new Document("$addFields",
                        new Document("categoryPrices",
                                new Document("$setUnion", List.of("$products.price", List.of()))
                        )
                ),

                // Step 3: Unwind products for counting, preserve empty categories
                Aggregation.unwind("products", true),

                // Step 4: Group by category _id, aggregate fields + product count
                Aggregation.group("_id")
                        .first("categoryName").as("categoryName")
                        .first("categoryImageBase64").as("categoryImageBase64")
                        .first("categoryColors").as("categoryColors")
                        .first("categorySizes").as("categorySizes")
                        .first("categoryPrices").as("categoryPrices")
                        .count().as("productsQuantity"),

                // Step 5: Project fields and rename _id to categoryId
                Aggregation.project()
                        .and("_id").as("categoryId")
                        .and("categoryName").as("categoryName")
                        .and("categoryImageBase64").as("categoryImageBase64")
                        .and("categoryColors").as("categoryColors")
                        .and("categorySizes").as("categorySizes")
                        .and("categoryPrices").as("categoryPrices")
                        .and("productsQuantity").as("productsQuantity")
        );

        AggregationResults<CategoryWithQuantity> results =
                mongoTemplate.aggregate(aggregation, "category", CategoryWithQuantity.class);

        List<CategoryWithQuantity> categories = results.getMappedResults();

        // Sort categoryPrices in Java since MongoDB aggregation sorting is not available here
        for (CategoryWithQuantity category : categories) {
            if (category.getCategoryPrices() != null) {
                Collections.sort(category.getCategoryPrices());
            }
        }

        return categories;
    }




}
