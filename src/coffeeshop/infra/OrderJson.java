package coffeeshop.infra;

import coffeeshop.application.model.OrderResult;
import coffeeshop.application.model.ReceiptLine;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class OrderJson {

    private OrderJson() {
    }

    public static Map<String, Object> toMap(OrderResult result) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("orderId", result.orderId());
        map.put("createdAt", result.createdAt());
        map.put("description", result.description());
        map.put("total", result.total());
        map.put("baseCode", result.baseCode());
        map.put("baseLabel", result.baseLabel());
        map.put("sizeCode", result.sizeCode());
        map.put("sizeLabel", result.sizeLabel());
        map.put("lines", result.lines().stream().map(OrderJson::lineToMap).toList());
        return map;
    }

    public static OrderResult fromMap(Map<?, ?> map) {
        List<ReceiptLine> lines = new ArrayList<>();
        if (map.get("lines") instanceof List<?> rawLines) {
            for (Object raw : rawLines) {
                if (raw instanceof Map<?, ?> line) {
                    lines.add(new ReceiptLine(text(line, "code"), text(line, "label"), number(line, "price")));
                }
            }
        }
        return new OrderResult(
                text(map, "orderId"),
                text(map, "createdAt"),
                text(map, "description"),
                number(map, "total"),
                text(map, "baseCode"),
                text(map, "baseLabel"),
                text(map, "sizeCode"),
                text(map, "sizeLabel"),
                List.copyOf(lines));
    }

    private static Map<String, Object> lineToMap(ReceiptLine line) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("code", line.code());
        map.put("label", line.label());
        map.put("price", line.price());
        return map;
    }

    private static String text(Map<?, ?> map, String key) {
        Object value = map.get(key);
        return value == null ? "" : String.valueOf(value);
    }

    private static double number(Map<?, ?> map, String key) {
        Object value = map.get(key);
        return value instanceof Number number ? number.doubleValue() : 0.0;
    }
}
