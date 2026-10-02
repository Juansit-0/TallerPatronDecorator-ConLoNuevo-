package coffeeshop.infra.http;

import coffeeshop.application.OrderService;
import coffeeshop.application.enums.DrinkType;
import coffeeshop.application.enums.ExtraType;
import coffeeshop.application.model.OrderRequest;
import coffeeshop.application.model.OrderResult;
import coffeeshop.application.model.ReceiptLine;
import coffeeshop.domain.Size;
import coffeeshop.infra.Json;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class OrderHandler implements HttpHandler {

    private final OrderService service;

    public OrderHandler(OrderService service) {
        this.service = service;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            HttpSupport.methodNotAllowed(exchange);
            return;
        }
        try {
            Object parsed = Json.parse(HttpSupport.readBody(exchange));
            if (!(parsed instanceof Map<?, ?> body)) {
                HttpSupport.sendError(exchange, 400, "Se esperaba un objeto JSON");
                return;
            }
            OrderRequest request = toRequest(body);
            OrderResult result = service.place(request);
            HttpSupport.sendJson(exchange, 200, Json.write(toMap(result)));
        } catch (IllegalArgumentException error) {
            HttpSupport.sendError(exchange, 400, error.getMessage());
        }
    }

    private static OrderRequest toRequest(Map<?, ?> body) {
        String base = stringValue(body.get("base"), "base");
        String size = stringValue(body.get("size"), "size");
        List<ExtraType> extras = new ArrayList<>();
        Object rawExtras = body.get("extras");
        if (rawExtras instanceof List<?> list) {
            for (Object item : list) {
                extras.add(ExtraType.fromCode(String.valueOf(item)));
            }
        }
        return new OrderRequest(
                DrinkType.fromCode(base),
                Size.valueOf(size.toUpperCase()),
                extras);
    }

    private static String stringValue(Object value, String field) {
        if (value == null) {
            throw new IllegalArgumentException("Falta el campo '" + field + "'");
        }
        return String.valueOf(value);
    }

    public static Map<String, Object> toMap(OrderResult result) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("orderId", result.orderId());
        map.put("description", result.description());
        map.put("total", result.total());
        map.put("baseCode", result.baseCode());
        map.put("baseLabel", result.baseLabel());
        map.put("sizeCode", result.sizeCode());
        map.put("sizeLabel", result.sizeLabel());
        List<Map<String, Object>> lines = result.lines().stream().map(OrderHandler::lineToMap).toList();
        map.put("lines", lines);
        return map;
    }

    private static Map<String, Object> lineToMap(ReceiptLine line) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("code", line.code());
        map.put("label", line.label());
        map.put("price", line.price());
        return map;
    }
}
