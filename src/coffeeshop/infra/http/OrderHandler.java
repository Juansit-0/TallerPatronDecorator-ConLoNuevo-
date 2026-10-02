package coffeeshop.infra.http;

import coffeeshop.application.OrderRequestBuilder;
import coffeeshop.application.OrderService;
import coffeeshop.application.model.OrderRequest;
import coffeeshop.application.model.OrderResult;
import coffeeshop.infra.Json;
import coffeeshop.infra.OrderJson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
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
            HttpSupport.sendJson(exchange, 200, Json.write(OrderJson.toMap(result)));
        } catch (IllegalArgumentException error) {
            HttpSupport.sendError(exchange, 400, error.getMessage());
        }
    }

    private static OrderRequest toRequest(Map<?, ?> body) {
        OrderRequestBuilder builder = new OrderRequestBuilder()
                .base(stringValue(body.get("base"), "base"))
                .size(stringValue(body.get("size"), "size"));
        if (body.get("extras") instanceof List<?> list) {
            for (Object item : list) {
                builder.extra(String.valueOf(item));
            }
        }
        return builder.build();
    }

    private static String stringValue(Object value, String field) {
        if (value == null) {
            throw new IllegalArgumentException("Falta el campo '" + field + "'");
        }
        return String.valueOf(value);
    }
}
