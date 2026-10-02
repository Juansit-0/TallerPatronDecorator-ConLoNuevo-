package coffeeshop.application.model;

import java.util.List;

public record OrderResult(
        String orderId,
        String description,
        double total,
        String baseCode,
        String baseLabel,
        String sizeCode,
        String sizeLabel,
        List<ReceiptLine> lines) {
}
