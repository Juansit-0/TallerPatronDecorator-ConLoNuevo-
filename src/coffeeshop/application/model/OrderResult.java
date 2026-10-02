package coffeeshop.application.model;

import java.util.List;

public record OrderResult(
        String orderId,
        String createdAt,
        String description,
        double total,
        String baseCode,
        String baseLabel,
        String sizeCode,
        String sizeLabel,
        List<ReceiptLine> lines) {

    public String createdDate() {
        return createdAt.length() >= 10 ? createdAt.substring(0, 10) : createdAt;
    }
}
