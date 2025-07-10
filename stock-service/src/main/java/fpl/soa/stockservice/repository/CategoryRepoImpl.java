// CategoryRepoImpl.java (important: match name to interface + "Impl")
package fpl.soa.stockservice.repository;

import fpl.soa.stockservice.DTO.CategoryWithQuantity;
import fpl.soa.stockservice.filters.CustomAggregationOperation;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.stereotype.Repository;


import java.util.*;


@Repository
public class CategoryRepoImpl implements CategoryRepoCustom {

    @Autowired
    private MongoTemplate mongoTemplate;



//    @Override
//    public List<CategoryWithQuantity> getCategoriesWithProductsQuantity(boolean isAdmin) {
//        List<AggregationOperation> pipeline = new ArrayList<>();
//
//        // 1. Lookup products
//        pipeline.add(Aggregation.lookup("product", "_id", "categoryId", "products"));
//
//        // 2. If not admin, filter out unselected colorVariants AND prune sizeVariants that have no selected colors
//        if (!isAdmin) {
//            Document filterSelectedColors = new Document("$map", new Document()
//                    .append("input", "$products")
//                    .append("as", "product")
//                    .append("in", new Document("$mergeObjects", Arrays.asList(
//                            "$$product",
//                            new Document("sizeVariants", new Document("$filter", new Document()
//                                    .append("input", new Document("$map", new Document()
//                                            .append("input", "$$product.sizeVariants")
//                                            .append("as", "sv")
//                                            .append("in", new Document("$mergeObjects", Arrays.asList(
//                                                    "$$sv",
//                                                    new Document("colorVariants", new Document("$filter", new Document()
//                                                            .append("input", "$$sv.colorVariants")
//                                                            .append("as", "cv")
//                                                            .append("cond", new Document("$eq", Arrays.asList("$$cv.selected", true)))
//                                                    ))
//                                            )))
//                                    ))
//                                    .append("as", "filteredSV")
//                                    .append("cond", new Document("$gt", Arrays.asList(
//                                            new Document("$size", "$$filteredSV.colorVariants"), 0
//                                    )))
//                            ))
//                    )))
//            );
//
//            pipeline.add((AggregationOperation) context ->
//                    new Document("$addFields", new Document("products", filterSelectedColors))
//            );
//        }
//
//        // 3. Add productsQuantity (after filtering)
//        pipeline.add(new CustomAggregationOperation(
//                new Document("$set", new Document("productsQuantity", new Document("$size", "$products")))
//        ));
//
//        // 4. Unwind products and sizeVariants only (we don't care about colorVariants for counts)
//        pipeline.add(Aggregation.unwind("products", true));
//        pipeline.add(Aggregation.unwind("products.sizeVariants", true));
//
//        // 5. Group per category
//        pipeline.add(Aggregation.group("_id")
//                .first("categoryName").as("categoryName")
//                .first("categoryImageBase64").as("categoryImageBase64")
//                .first("categoryColors").as("categoryColors")
//                .first("categorySizes").as("categorySizes")
//                .first("productsQuantity").as("productsQuantity")
//                .push("products.sizeVariants.size").as("allSizes")
//                .push("products.sizeVariants.productPrice.price").as("allPrices")
//                .push("products.sizeVariants").as("allSizeVariants")
//        );
//
//        // 6. Count value occurrences at sizeVariant level
//        pipeline.add(new CustomAggregationOperation(
//                new Document("$set", new Document()
//                        .append("categoryProductsSizesWithCount", new Document("$map", new Document()
//                                .append("input", new Document("$setUnion", "$allSizes"))
//                                .append("as", "size")
//                                .append("in", new Document()
//                                        .append("value", "$$size")
//                                        .append("count", new Document("$size", new Document("$filter", new Document()
//                                                .append("input", "$allSizes")
//                                                .append("cond", new Document("$eq", Arrays.asList("$$this", "$$size")))
//                                        )))
//                                )
//                        ))
//                        .append("categoryPricesWithCount", new Document("$map", new Document()
//                                .append("input", new Document("$setUnion", "$allPrices"))
//                                .append("as", "price")
//                                .append("in", new Document()
//                                        .append("value", "$$price")
//                                        .append("count", new Document("$size", new Document("$filter", new Document()
//                                                .append("input", "$allPrices")
//                                                .append("cond", new Document("$eq", Arrays.asList("$$this", "$$price")))
//                                        )))
//                                )
//                        ))
//                        .append("categoryProductsColorsWithCount", new Document("$map", new Document()
//                                .append("input", new Document("$setUnion", new Document("$reduce", new Document()
//                                        .append("input", "$allSizeVariants")
//                                        .append("initialValue", new ArrayList<>())
//                                        .append("in", new Document("$concatArrays", Arrays.asList("$$value", "$$this.colorVariants.color")))
//                                )))
//                                .append("as", "color")
//                                .append("in", new Document()
//                                        .append("value", "$$color")
//                                        .append("count", new Document("$size", new Document("$filter", new Document()
//                                                .append("input", new Document("$reduce", new Document()
//                                                        .append("input", "$allSizeVariants")
//                                                        .append("initialValue", new ArrayList<>())
//                                                        .append("in", new Document("$concatArrays", Arrays.asList("$$value", "$$this.colorVariants.color")))
//                                                ))
//                                                .append("cond", new Document("$eq", Arrays.asList("$$this", "$$color")))
//                                        )))
//                                )
//                        ))
//                        .append("categoryPrices", new Document("$setUnion", "$allPrices"))  // deduplicated raw list
//                )
//        ));
//
//        // 7. Final projection
//        pipeline.add(new CustomAggregationOperation(
//                new Document("$project", new Document()
//                        .append("categoryId", "$_id")
//                        .append("categoryName", 1)
//                        .append("categoryImageBase64", 1)
//                        .append("categoryColors", 1)
//                        .append("categorySizes", 1)
//                        .append("productsQuantity", 1)
//                        .append("categoryPrices", 1)
//                        .append("categoryProductsSizesWithCount", 1)
//                        .append("categoryProductsColorsWithCount", 1)
//                        .append("categoryPricesWithCount", 1)
//                )
//        ));
//
//        Aggregation aggregation = Aggregation.newAggregation(pipeline);
//        AggregationResults<CategoryWithQuantity> results = mongoTemplate.aggregate(
//                aggregation,
//                "category",
//                CategoryWithQuantity.class
//        );
//
//        return results.getMappedResults();
//    }



    @Override
    public List<CategoryWithQuantity> getCategoriesWithProductsQuantity(boolean isAdmin) {
        List<AggregationOperation> pipeline = new ArrayList<>();

        // 1. Lookup products
        pipeline.add(Aggregation.lookup("product", "_id", "categoryId", "products"));

        // 2. Filter selected colorVariants and prune sizeVariants with none selected
        if (!isAdmin) {
            Document filterSelectedColors = new Document("$map", new Document()
                    .append("input", "$products")
                    .append("as", "product")
                    .append("in", new Document("$mergeObjects", Arrays.asList(
                            "$$product",
                            new Document("sizeVariants", new Document("$filter", new Document()
                                    .append("input", new Document("$map", new Document()
                                            .append("input", "$$product.sizeVariants")
                                            .append("as", "sv")
                                            .append("in", new Document("$mergeObjects", Arrays.asList(
                                                    "$$sv",
                                                    new Document("colorVariants", new Document("$filter", new Document()
                                                            .append("input", "$$sv.colorVariants")
                                                            .append("as", "cv")
                                                            .append("cond", new Document("$eq", Arrays.asList("$$cv.selected", true)))
                                                    ))
                                            )))
                                    ))
                                    .append("as", "filteredSV")
                                    .append("cond", new Document("$gt", Arrays.asList(
                                            new Document("$size", "$$filteredSV.colorVariants"), 0
                                    )))
                            ))
                    )))
            );

            pipeline.add((AggregationOperation) context ->
                    new Document("$addFields", new Document("products", filterSelectedColors))
            );
        }

        // 3. Add productsQuantity
        pipeline.add(new CustomAggregationOperation(
                new Document("$set", new Document("productsQuantity", new Document("$size", "$products")))
        ));

        // 4. Unwind products and sizeVariants
        pipeline.add(Aggregation.unwind("products", true));
        pipeline.add(Aggregation.unwind("products.sizeVariants", true));

        // 5. Group to gather necessary fields
        pipeline.add(Aggregation.group("_id")
                .first("categoryName").as("categoryName")
                .first("categoryImageBase64").as("categoryImageBase64")
                .first("categoryColors").as("categoryColors")
                .first("categorySizes").as("categorySizes")
                .first("productsQuantity").as("productsQuantity")
                .push("products.sizeVariants.size").as("allSizes")
                .push("products.sizeVariants.productPrice.price").as("allPrices")
                .push("products.sizeVariants").as("allSizeVariants")
                .push(new Document("productId", "$products._id")
                        .append("colors", "$products.sizeVariants.colorVariants.color"))
                .as("productColorPairs")
        );

        // 6. Map counts for sizes, prices, and distinct color-product match
        pipeline.add(new CustomAggregationOperation(
                new Document("$set", new Document()
                        .append("categoryProductsSizesWithCount", new Document("$map", new Document()
                                .append("input", new Document("$setUnion", "$allSizes"))
                                .append("as", "size")
                                .append("in", new Document()
                                        .append("value", "$$size")
                                        .append("count", new Document("$size", new Document("$filter", new Document()
                                                .append("input", "$allSizes")
                                                .append("cond", new Document("$eq", Arrays.asList("$$this", "$$size")))
                                        )))
                                )
                        ))
                        .append("categoryPricesWithCount", new Document("$map", new Document()
                                .append("input", new Document("$setUnion", "$allPrices"))
                                .append("as", "price")
                                .append("in", new Document()
                                        .append("value", "$$price")
                                        .append("count", new Document("$size", new Document("$filter", new Document()
                                                .append("input", "$allPrices")
                                                .append("cond", new Document("$eq", Arrays.asList("$$this", "$$price")))
                                        )))
                                )
                        ))
                        .append("categoryProductsColorsWithCount", new Document("$map", new Document()
                                .append("input", new Document("$setUnion", new Document("$reduce", new Document()
                                        .append("input", "$productColorPairs")
                                        .append("initialValue", new ArrayList<>())
                                        .append("in", new Document("$concatArrays", Arrays.asList("$$value", "$$this.colors")))
                                )))
                                .append("as", "color")
                                .append("in", new Document()
                                        .append("value", "$$color")
                                        .append("count", new Document("$size", new Document("$setUnion", new Document("$map", new Document()
                                                .append("input", new Document("$filter", new Document()
                                                        .append("input", "$productColorPairs")
                                                        .append("as", "pair")
                                                        .append("cond", new Document("$in", Arrays.asList("$$color", "$$pair.colors")))
                                                ))
                                                .append("as", "match")
                                                .append("in", "$$match.productId")
                                        ))))
                                )
                        ))
                        .append("categoryPrices", new Document("$setUnion", "$allPrices"))
                )
        ));

        // 7. Final projection
        pipeline.add(new CustomAggregationOperation(
                new Document("$project", new Document()
                        .append("categoryId", "$_id")
                        .append("categoryName", 1)
                        .append("categoryImageBase64", 1)
                        .append("categoryColors", 1)
                        .append("categorySizes", 1)
                        .append("productsQuantity", 1)
                        .append("categoryPrices", 1)
                        .append("categoryProductsSizesWithCount", 1)
                        .append("categoryProductsColorsWithCount", 1)
                        .append("categoryPricesWithCount", 1)
                )
        ));

        Aggregation aggregation = Aggregation.newAggregation(pipeline);
        AggregationResults<CategoryWithQuantity> results = mongoTemplate.aggregate(
                aggregation,
                "category",
                CategoryWithQuantity.class
        );

        return results.getMappedResults();
    }



}
