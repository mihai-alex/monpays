package com.monpays.entities.profile;

import org.springframework.data.jpa.domain.Specification;

public class ProfileSpecifications {
    // Private constructor to prevent instantiation
    private ProfileSpecifications() {
        throw new AssertionError("Utility class should not be instantiated.");
    }

    public static Specification<Profile> filterByColumn(String columnName, String filterValue) {
        return (root, query, criteriaBuilder) -> {
            if (columnName == null || columnName.isEmpty()) {
                return criteriaBuilder.isTrue(criteriaBuilder.literal(true)); // Return all entities
            } else {
                return criteriaBuilder.like(criteriaBuilder.lower(root.get(columnName)), "%" + filterValue.toLowerCase() + "%");
            }
        };
    }
}
