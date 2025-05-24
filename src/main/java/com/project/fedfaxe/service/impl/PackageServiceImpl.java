package com.project.fedfaxe.service.impl;

import com.project.fedfaxe.model.PackageProduct;
import com.project.fedfaxe.model.PriceAlert;
import com.project.fedfaxe.model.enums.PackageType;
import com.project.fedfaxe.repository.PackageProductRepository;
import com.project.fedfaxe.repository.PriceAlertRepository;
import com.project.fedfaxe.service.PackageService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PackageServiceImpl implements PackageService {


    private final PackageProductRepository packageProductRepository;
    private final PriceAlertRepository priceAlertRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    public PackageServiceImpl(PackageProductRepository packageProductRepository, PriceAlertRepository priceAlertRepository) {
        this.packageProductRepository = packageProductRepository;
        this.priceAlertRepository = priceAlertRepository;
    }

    @Override
    public Page<PackageProduct> searchPackages(
            String destination,
            LocalDate checkInDate,
            LocalDate checkOutDate,
            Integer numberOfTravelers,
            Integer minNights,
            Integer maxNights,
            Boolean withFlights,
            BigDecimal minBudget,
            BigDecimal maxBudget,
            List<PackageType> packageTypes,
            PageRequest pageRequest) {

        Criteria criteria = new Criteria();

        if (destination != null && !destination.isEmpty()) {
            criteria = criteria.and("location").regex(destination, "i");
        }

        if (minNights != null) {
            criteria = criteria.and("duration").gte(minNights);
        }

        if (maxNights != null) {
            criteria = criteria.and("duration").lte(maxNights);
        }

        if (withFlights != null) {
            if (withFlights) {
                criteria = criteria.and("priceWithFlights").gt(0);
            } else {
                criteria = criteria.and("priceWithoutFlights").gt(0);
            }
        }

        if (minBudget != null) {
            String priceField = withFlights != null && withFlights ? "priceWithFlights" : "priceWithoutFlights";
            criteria = criteria.and(priceField).gte(minBudget.doubleValue());
        }

        if (maxBudget != null) {
            String priceField = withFlights != null && withFlights ? "priceWithFlights" : "priceWithoutFlights";
            criteria = criteria.and(priceField).lte(maxBudget.doubleValue());
        }

        if (packageTypes != null && !packageTypes.isEmpty() && !packageTypes.contains(PackageType.ALL)) {
            criteria = criteria.and("packageType").in(packageTypes);
        }

        Query query = new Query(criteria).with(pageRequest);

        List<PackageProduct> packages = mongoTemplate.find(query, PackageProduct.class);
        long count = mongoTemplate.count(Query.of(query).limit(-1).skip(-1), PackageProduct.class);

        return new PageImpl<>(packages, pageRequest, count);
    }

    @Override
    public Map<PackageType, Long> getPackageTypeCounts(
            String destination,
            LocalDate checkInDate,
            LocalDate checkOutDate,
            Integer numberOfTravelers,
            Integer minNights,
            Integer maxNights,
            Boolean withFlights,
            BigDecimal minBudget,
            BigDecimal maxBudget) {

        Criteria criteria = new Criteria();

        if (destination != null && !destination.isEmpty()) {
            criteria = criteria.and("location").regex(destination, "i");
        }

        if (minNights != null) {
            criteria = criteria.and("duration").gte(minNights);
        }

        if (maxNights != null) {
            criteria = criteria.and("duration").lte(maxNights);
        }

        if (withFlights != null) {
            if (withFlights) {
                criteria = criteria.and("priceWithFlights").gt(0);
            } else {
                criteria = criteria.and("priceWithoutFlights").gt(0);
            }
        }

        if (minBudget != null) {
            String priceField = withFlights != null && withFlights ? "priceWithFlights" : "priceWithoutFlights";
            criteria = criteria.and(priceField).gte(minBudget.doubleValue());
        }

        if (maxBudget != null) {
            String priceField = withFlights != null && withFlights ? "priceWithFlights" : "priceWithoutFlights";
            criteria = criteria.and(priceField).lte(maxBudget.doubleValue());
        }

        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(criteria),
                Aggregation.group("packageType").count().as("count"),
                Aggregation.project("count").and("_id").as("packageType")
        );

        AggregationResults<TypeCount> results = mongoTemplate.aggregate(
                aggregation, "package_products", TypeCount.class);

        Map<PackageType, Long> typeCounts = new HashMap<>();
        for (TypeCount typeCount : results.getMappedResults()) {
            typeCounts.put(typeCount.getPackageType(), typeCount.getCount());
        }

        // Ensure we have entries for all package types
        for (PackageType type : PackageType.values()) {
            if (type != PackageType.ALL && !typeCounts.containsKey(type)) {
                typeCounts.put(type, 0L);
            }
        }

        return typeCounts;
    }

    @Override
    public PriceAlert createPriceAlert(String packageId, String userId, BigDecimal targetPrice, String email) {
        PriceAlert alert = PriceAlert.builder()
                .packageId(packageId)
                .userId(userId)
                .targetPrice(targetPrice)
                .email(email)
                .created(LocalDateTime.now())
                .build();

        return priceAlertRepository.save(alert);
    }

    @Data
    private static class TypeCount {
        private PackageType packageType;
        private Long count;
    }
}
