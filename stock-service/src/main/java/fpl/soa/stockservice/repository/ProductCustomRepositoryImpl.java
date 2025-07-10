package fpl.soa.stockservice.repository;

import fpl.soa.stockservice.entities.Product;
import fpl.soa.stockservice.filters.ProductFilterRequest;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.*;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.regex.Pattern;

@Repository
@RequiredArgsConstructor
public class ProductCustomRepositoryImpl implements ProductCustomRepository {

    private final MongoTemplate mongoTemplate;


    @Override
    public Page<Product> filterAllProductsWithVariants(ProductFilterRequest request, Pageable pageable) {
        List<AggregationOperation> operations = new ArrayList<>();

        // Step 1: If user is not an admin, filter colorVariants by selected == true
        if (!request.isAdmin()) {
            Document selectedFilter = new Document("$map",
                    new Document("input", "$sizeVariants")
                            .append("as", "sv")
                            .append("in", new Document("$mergeObjects", Arrays.asList(
                                    "$$sv",
                                    new Document("colorVariants",
                                            new Document("$filter",
                                                    new Document("input", "$$sv.colorVariants")
                                                            .append("as", "cv")
                                                            .append("cond", new Document("$eq", Arrays.asList("$$cv.selected", true)))
                                            ))
                            ))));
            operations.add(context -> new Document("$addFields", new Document("sizeVariants", selectedFilter)));
        }

        // Step 2: Filter by price ranges
        if (request.getPriceRanges() != null && !request.getPriceRanges().isEmpty()) {
            List<Criteria> priceCriteria = new ArrayList<>();
            for (String range : request.getPriceRanges()) {
                String[] parts = range.split("-");
                if (parts.length == 2) {
                    double min = Double.parseDouble(parts[0].trim());
                    double max = Double.parseDouble(parts[1].trim());
                    priceCriteria.add(Criteria.where("sizeVariants.productPrice.price").gte(min).lte(max));
                }
            }
            if (!priceCriteria.isEmpty()) {
                operations.add(Aggregation.match(new Criteria().orOperator(priceCriteria.toArray(new Criteria[0]))));
            }
        }

        // Step 3: Remove sizeVariants that have no colorVariants left
        Document filterSizeVariantsWithColors = new Document("$filter",
                new Document("input", "$sizeVariants")
                        .append("as", "sv")
                        .append("cond", new Document("$gt", Arrays.asList(
                                new Document("$size", "$$sv.colorVariants"), 0
                        )))
        );
        operations.add(context -> new Document("$addFields", new Document("sizeVariants", filterSizeVariantsWithColors)));

        // Step 4: Remove products that now have no sizeVariants left
        operations.add(Aggregation.match(Criteria.where("sizeVariants").not().size(0)));

        // Step 5: Sorting
        if (pageable.getSort().isSorted()) {
            operations.add(Aggregation.sort(pageable.getSort()));
        }

        // Step 6: Pagination
        operations.add(Aggregation.skip((long) pageable.getPageNumber() * pageable.getPageSize()));
        operations.add(Aggregation.limit(pageable.getPageSize()));

        // Step 7: Execute aggregation
        Aggregation aggregation = Aggregation.newAggregation(operations);
        List<Product> results = mongoTemplate.aggregate(aggregation, "product", Product.class).getMappedResults();

        // Step 8: Count total
        List<AggregationOperation> countOps = new ArrayList<>(operations);
        countOps.removeIf(op -> op instanceof SkipOperation || op instanceof LimitOperation);
        Aggregation countAgg = Aggregation.newAggregation(countOps);
        long total = mongoTemplate.aggregate(countAgg, "product", Product.class).getMappedResults().size();

        return new PageImpl<>(results, pageable, total);
    }



    @Override
    public Page<Product> filterProductsByCategoryWithVariants(ProductFilterRequest request, Pageable pageable) {
        List<AggregationOperation> operations = new ArrayList<>();

        // Match products by categoryId
        operations.add(Aggregation.match(Criteria.where("categoryId").is(request.getCategoryId())));

        // If user is not an admin, filter colorVariants by selected == true
        if (!request.isAdmin()) {
            Document selectedFilter = new Document("$map",
                    new Document("input", "$sizeVariants")
                            .append("as", "sv")
                            .append("in", new Document("$mergeObjects", Arrays.asList(
                                    "$$sv",
                                    new Document("colorVariants",
                                            new Document("$filter",
                                                    new Document("input", "$$sv.colorVariants")
                                                            .append("as", "cv")
                                                            .append("cond", new Document("$eq", Arrays.asList("$$cv.selected", true)))
                                            ))
                            ))));
            operations.add(context -> new Document("$addFields", new Document("sizeVariants", selectedFilter)));
        }

        // Filter by price ranges (if provided)
        if (request.getPriceRanges() != null && !request.getPriceRanges().isEmpty()) {
            List<Criteria> priceCriteria = new ArrayList<>();
            for (String range : request.getPriceRanges()) {
                String[] parts = range.split("-");
                if (parts.length == 2) {
                    double min = Double.parseDouble(parts[0].trim());
                    double max = Double.parseDouble(parts[1].trim());
                    priceCriteria.add(Criteria.where("sizeVariants.productPrice.price").gte(min).lte(max));
                }
            }
            if (!priceCriteria.isEmpty()) {
                operations.add(Aggregation.match(new Criteria().orOperator(priceCriteria.toArray(new Criteria[0]))));
            }
        }

        // Filter sizeVariants by size (if provided)
        if (request.getSizes() != null && !request.getSizes().isEmpty()) {
            Document sizeFilter = new Document("$filter",
                    new Document("input", "$sizeVariants")
                            .append("as", "sv")
                            .append("cond", new Document("$in", Arrays.asList("$$sv.size", request.getSizes()))));
            operations.add(context -> new Document("$addFields", new Document("sizeVariants", sizeFilter)));
        }

        // Filter colorVariants by color (if provided)
        if (request.getColors() != null && !request.getColors().isEmpty()) {
            Document colorFilter = new Document("$map",
                    new Document("input", "$sizeVariants")
                            .append("as", "sv")
                            .append("in", new Document("$mergeObjects", Arrays.asList(
                                    "$$sv",
                                    new Document("colorVariants",
                                            new Document("$filter",
                                                    new Document("input", "$$sv.colorVariants")
                                                            .append("as", "cv")
                                                            .append("cond", new Document("$in", Arrays.asList("$$cv.color", request.getColors())))
                                            ))
                            ))));
            operations.add(context -> new Document("$addFields", new Document("sizeVariants", colorFilter)));
        }

        // Remove sizeVariants that have no colorVariants left
        Document filterSizeVariantsWithColors = new Document("$filter",
                new Document("input", "$sizeVariants")
                        .append("as", "sv")
                        .append("cond", new Document("$gt", Arrays.asList(
                                new Document("$size", "$$sv.colorVariants"), 0
                        )))
        );
        operations.add(context -> new Document("$addFields", new Document("sizeVariants", filterSizeVariantsWithColors)));

        // Remove products that now have no sizeVariants left
        operations.add(Aggregation.match(Criteria.where("sizeVariants").not().size(0)));

        // Apply sorting (if any)
        if (pageable.getSort().isSorted()) {
            operations.add(Aggregation.sort(pageable.getSort()));
        }

        // Apply pagination (skip + limit)
        operations.add(Aggregation.skip((long) pageable.getPageNumber() * pageable.getPageSize()));
        operations.add(Aggregation.limit(pageable.getPageSize()));

        // Execute aggregation pipeline
        Aggregation aggregation = Aggregation.newAggregation(operations);
        List<Product> results = mongoTemplate.aggregate(aggregation, "product", Product.class).getMappedResults();

        // Compute total number of matching products (excluding pagination)
        List<AggregationOperation> countOps = new ArrayList<>(operations);
        countOps.removeIf(op -> op instanceof SkipOperation || op instanceof LimitOperation);
        Aggregation countAgg = Aggregation.newAggregation(countOps);
        long total = mongoTemplate.aggregate(countAgg, "product", Product.class).getMappedResults().size();

        // Return paginated product list with total count
        return new PageImpl<>(results, pageable, total);
    }


    @Override
    public Page<Product> filterProductsByKeywordWithVariants(ProductFilterRequest request, Pageable pageable) {
        List<AggregationOperation> operations = new ArrayList<>();

        // Step 1: Match products where name or brand contains the keyword (case-insensitive)
        String regex = ".*" + Pattern.quote(request.getKeyword()) + ".*";
        Criteria keywordCriteria = new Criteria().orOperator(
                Criteria.where("name").regex(regex, "i"),
                Criteria.where("brand").regex(regex, "i")
        );
        operations.add(Aggregation.match(keywordCriteria));

        // Step 2: If user is not an admin, filter colorVariants by selected == true
        if (!request.isAdmin()) {
            Document selectedFilter = new Document("$map",
                    new Document("input", "$sizeVariants")
                            .append("as", "sv")
                            .append("in", new Document("$mergeObjects", Arrays.asList(
                                    "$$sv",
                                    new Document("colorVariants",
                                            new Document("$filter",
                                                    new Document("input", "$$sv.colorVariants")
                                                            .append("as", "cv")
                                                            .append("cond", new Document("$eq", Arrays.asList("$$cv.selected", true)))
                                            ))
                            )))
            );
            operations.add(context -> new Document("$addFields", new Document("sizeVariants", selectedFilter)));
        }

        // Step 3: Filter by price ranges
        if (request.getPriceRanges() != null && !request.getPriceRanges().isEmpty()) {
            List<Criteria> priceCriteria = new ArrayList<>();
            for (String range : request.getPriceRanges()) {
                String[] parts = range.split("-");
                if (parts.length == 2) {
                    double min = Double.parseDouble(parts[0].trim());
                    double max = Double.parseDouble(parts[1].trim());
                    priceCriteria.add(Criteria.where("sizeVariants.productPrice.price").gte(min).lte(max));
                }
            }
            if (!priceCriteria.isEmpty()) {
                operations.add(Aggregation.match(new Criteria().orOperator(priceCriteria.toArray(new Criteria[0]))));
            }
        }

        // Step 4: Filter sizeVariants by size
        if (request.getSizes() != null && !request.getSizes().isEmpty()) {
            Document sizeFilter = new Document("$filter",
                    new Document("input", "$sizeVariants")
                            .append("as", "sv")
                            .append("cond", new Document("$in", Arrays.asList("$$sv.size", request.getSizes())))
            );
            operations.add(context -> new Document("$addFields", new Document("sizeVariants", sizeFilter)));
        }

        // Step 5: Filter colorVariants by color
        if (request.getColors() != null && !request.getColors().isEmpty()) {
            Document colorFilter = new Document("$map",
                    new Document("input", "$sizeVariants")
                            .append("as", "sv")
                            .append("in", new Document("$mergeObjects", Arrays.asList(
                                    "$$sv",
                                    new Document("colorVariants",
                                            new Document("$filter",
                                                    new Document("input", "$$sv.colorVariants")
                                                            .append("as", "cv")
                                                            .append("cond", new Document("$in", Arrays.asList("$$cv.color", request.getColors())))
                                            ))
                            )))
            );
            operations.add(context -> new Document("$addFields", new Document("sizeVariants", colorFilter)));
        }

        // Step 6: Remove sizeVariants with no colorVariants
        Document removeEmptySizeVariants = new Document("$filter",
                new Document("input", "$sizeVariants")
                        .append("as", "sv")
                        .append("cond", new Document("$gt", Arrays.asList(new Document("$size", "$$sv.colorVariants"), 0)))
        );
        operations.add(context -> new Document("$addFields", new Document("sizeVariants", removeEmptySizeVariants)));

        // Step 7: Remove products with no sizeVariants
        operations.add(Aggregation.match(Criteria.where("sizeVariants").not().size(0)));

        // Step 8: Sorting
        if (pageable.getSort().isSorted()) {
            operations.add(Aggregation.sort(pageable.getSort()));
        }

        // Step 9: Pagination
        operations.add(Aggregation.skip((long) pageable.getPageNumber() * pageable.getPageSize()));
        operations.add(Aggregation.limit(pageable.getPageSize()));

        // Step 10: Run pipeline
        Aggregation aggregation = Aggregation.newAggregation(operations);
        List<Product> results = mongoTemplate.aggregate(aggregation, "product", Product.class).getMappedResults();

        // Step 11: Count total
        List<AggregationOperation> countOps = new ArrayList<>(operations);
        countOps.removeIf(op -> op instanceof SkipOperation || op instanceof LimitOperation);
        long total = mongoTemplate.aggregate(Aggregation.newAggregation(countOps), "product", Product.class).getMappedResults().size();

        // Step 12: Return
        return new PageImpl<>(results, pageable, total);
    }



}
