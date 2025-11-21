package com.bvd.java_fundamentals;

import com.bvd.java_fundamentals.model.Order;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/*
 * Implement the methods below so that the requirements are met.
 */
public class OrderUtil {

    private OrderUtil() {
    }

    // retrieve orders from csv lines
    public static List<Order> parseCsvLines(final List<String> lines) {

        if (lines == null || lines.isEmpty()) {
            return Collections.emptyList();
        }

        return lines.stream()
                .map(line -> {
                    try {
                        String[] parts = line.split(",");
                        String orderId = parts[0].trim();
                        String customerId = parts[1].trim();
                        LocalDate orderDate = LocalDate.parse(parts[2].trim());
                        String productName = parts[3].trim();
                        String category = parts[4].trim();
                        BigDecimal unitPrice = new BigDecimal(parts[5].trim());
                        int quantity = Integer.parseInt(parts[6].trim());
                        return new Order(orderId, customerId, orderDate, productName, category, unitPrice, quantity);
                    } catch (Exception e) {
                        return null; // skip malformed lines
                    }
                })
                .filter(Objects::nonNull)
                .toList();
    }

    // calculate revenue by day
    // revenue = unitPrice * quantity
    public static Map<LocalDate, BigDecimal> revenueByDay(final List<Order> orders) {

        return orders.stream()
                .collect(
                        Collectors.groupingBy(
                                Order::getOrderDate,
                                Collectors.mapping(
                                        order -> order.getUnitPrice().multiply(BigDecimal.valueOf(order.getQuantity())),
                                        Collectors.reducing(BigDecimal.ZERO, BigDecimal::add)
                                )
                        )
                );

    }

    // get top "n" products by revenue
    public static List<Map.Entry<String, BigDecimal>> topProductsByRevenue(final List<Order> orders, final int n) {

        if (orders == null || orders.isEmpty() || n < 1) {
            return Collections.emptyList();
        }

        Map<String, BigDecimal> revenuePerProd = orders.stream()
                .collect(Collectors.groupingBy(
                        Order::getProductName,
                        Collectors.reducing(
                                BigDecimal.ZERO,
                                order -> order.getUnitPrice()
                                        .multiply(BigDecimal.valueOf(order.getQuantity())),
                                BigDecimal::add
                        )
                ));

        return revenuePerProd.entrySet().stream()
                .sorted(Map.Entry.<String, BigDecimal>comparingByValue().reversed())
                .limit(n)
                .toList();
    }

    // get customers who ordered products from at least "minCategories" different categories
    public static List<String> customersWithCategoryDiversity(final List<Order> orders, final int minCategories) {

        final var categoriesByCustomer = orders.stream()
                .collect(
                        Collectors.groupingBy(
                                Order::getCustomerId,
                                Collectors.mapping(
                                        Order::getCategory,
                                        Collectors.toSet()
                                )
                        )
                );
        return categoriesByCustomer.entrySet().stream()
                .filter(entry -> entry.getValue().size() >= minCategories)
                .map(Map.Entry::getKey)
                .toList();
    }

    // find the first product containing a given substring (case-insensitive)
    public static Optional<Order> findFirstProductContaining(final List<Order> orders, final String product) {

        if (orders == null || product == null) {
            return Optional.empty();
        }

        Optional<Order> op = orders.stream()
                .filter(x -> x.getProductName().toLowerCase().contains(product.toLowerCase()))
                .findFirst();

        return op;
    }
}
