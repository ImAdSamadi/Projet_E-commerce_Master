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

@Repository
@RequiredArgsConstructor
public class ProductCustomRepositoryImpl implements ProductCustomRepository {

    private final MongoTemplate mongoTemplate;


    @Override
    public Page<Product> filterProductsByCategoryWithVariants(ProductFilterRequest request, Pageable pageable) {
        List<AggregationOperation> operations = new ArrayList<>();

        // 1. Match categoryId
        Criteria criteria = Criteria.where("categoryId").is(request.getCategoryId());
        operations.add(Aggregation.match(criteria));

        // 2. Match priceRanges
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

        // 3. Filter sizeVariants by size
        if (request.getSizes() != null && !request.getSizes().isEmpty()) {
            Document sizeFilter = new Document("$filter",
                    new Document("input", "$sizeVariants")
                            .append("as", "sv")
                            .append("cond", new Document("$in", Arrays.asList("$$sv.size", request.getSizes()))));
            operations.add(context -> new Document("$addFields", new Document("sizeVariants", sizeFilter)));
        }

        // 4. Filter colorVariants by color
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

        // 5. If not admin, filter colorVariants by selected = true
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

        // 6. Exclude products with empty sizeVariants
        operations.add(Aggregation.match(Criteria.where("sizeVariants").not().size(0)));

        // 7. Sorting
        if (pageable.getSort().isSorted()) {
            operations.add(Aggregation.sort(pageable.getSort()));
        }

        // 8. Pagination
        operations.add(Aggregation.skip((long) pageable.getPageNumber() * pageable.getPageSize()));
        operations.add(Aggregation.limit(pageable.getPageSize()));

        // 9. Run the aggregation
        Aggregation aggregation = Aggregation.newAggregation(operations);
        List<Product> results = mongoTemplate.aggregate(aggregation, "product", Product.class).getMappedResults();

        // 10. Count total matching results
        List<AggregationOperation> countOps = new ArrayList<>(operations);
        countOps.removeIf(op -> op instanceof SkipOperation || op instanceof LimitOperation);
        Aggregation countAgg = Aggregation.newAggregation(countOps);
        long total = mongoTemplate.aggregate(countAgg, "product", Product.class).getMappedResults().size();

        return new PageImpl<>(results, pageable, total);
    }



}
