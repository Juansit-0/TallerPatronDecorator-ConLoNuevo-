package coffeeshop.application.model;

import java.util.List;
import java.util.Map;

public record HistoryPage(
        List<OrderResult> orders,
        int page,
        int pageSize,
        int totalPages,
        int count,
        double revenue,
        Map<String, Double> revenueByDate) {
}
