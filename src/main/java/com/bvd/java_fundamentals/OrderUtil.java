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

        List<Order> result = new ArrayList<>();

        if (lines == null || lines.isEmpty()) {
            return result;
        }

        for (String row :
                lines) {
            if (row == null || row.isBlank()) {
                continue;  // daca e gol skip la next , check better if the line is malformed
            }
            if (!(row.matches("^O-\\d+$"))) {   // Good: O-1001 , O-1 , O-9999
                continue;
            }

            String[] splitRow = row.split(",");

            if (splitRow.length < 7) {    // daca are sub 7 coloane --> malformed
                continue;
            }

            System.out.println("ex1---------------" + Arrays.toString(splitRow));

            Order order = new Order(
                    splitRow[0].trim(),
                    splitRow[1].trim(),
                    LocalDate.parse(splitRow[2].trim()),
                    splitRow[3].trim(),
                    splitRow[4].trim(),
                    new BigDecimal(splitRow[5].trim()),
                    Integer.parseInt(splitRow[6].trim())
            );

            result.add(order);
        }

        return result;
    }

    // calculate revenue by day
    // revenue = unitPrice * quantity
    public static Map<LocalDate, BigDecimal> revenueByDay(final List<Order> orders) {

        Map<LocalDate, BigDecimal> result = new HashMap<>();

        if (orders == null || orders.isEmpty()) {
            return result;
        }

        result = orders.stream()
                .collect(Collectors.groupingBy(
                        Order::getOrderDate,
                        Collectors.reducing(
                                BigDecimal.ZERO,
                                order -> order.getUnitPrice()
                                        .multiply(BigDecimal.valueOf(order.getQuantity())),
                                BigDecimal::add
                        )
                ));

        System.out.println("ex2---------------------------" + result.entrySet());

        return result;
    }

    // get top "n" products by revenue
    public static List<Map.Entry<String, BigDecimal>> topProductsByRevenue(final List<Order> orders, final int n) {

        List<Map.Entry<String, BigDecimal>> result = new ArrayList<>();

        if (orders == null || orders.isEmpty() || n < 1) {
            return result;
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

        System.out.println("ex3-----------------------------------" + revenuePerProd);

        result = revenuePerProd.entrySet().stream()
                .sorted(Map.Entry.<String, BigDecimal>comparingByValue().reversed())
                .limit(n)
                .collect(Collectors.toList());

        return result;
    }

    // get customers who ordered products from at least "minCategories" different categories
    public static List<String> customersWithCategoryDiversity(final List<Order> orders, final int minCategories) {

        List<String> result = new ArrayList<>();

        if (orders == null || orders.isEmpty() || minCategories < 1) {
            return result;
        }

        Map<String, Set<String>> intermediateMap = orders.stream()
                .collect(Collectors.groupingBy(
                                Order::getCustomerId,
                                Collectors.mapping(Order::getCategory, Collectors.toSet())
                        )
                );

        System.out.println("ex 4----------------------------------------" + intermediateMap);

        result = intermediateMap.entrySet().stream()
                .filter(x -> x.getValue().size() >= minCategories)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        System.out.println("ex 4----------------------------------------" + result);

        return result;
    }

    // find the first product containing a given substring (case-insensitive)
    public static Optional<Order> findFirstProductContaining(final List<Order> orders, final String product) {

        if (orders == null || product == null) {
            return Optional.empty();
        }

        Optional<Order> op = orders.stream()
                .filter(x -> x.getProductName().toLowerCase().contains(product.toLowerCase()))
                .findFirst();

        System.out.println("ex 5 ----------------------------------" + op);

        return op;
    }
}
