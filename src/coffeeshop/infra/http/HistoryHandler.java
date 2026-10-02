package coffeeshop.infra.http;

import coffeeshop.application.OrderService;
import coffeeshop.application.model.OrderResult;
import coffeeshop.infra.Json;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class HistoryHandler implements HttpHandler {

    private final OrderService service;

    public HistoryHandler(OrderService service) {
        this.service = service;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            HttpSupport.methodNotAllowed(exchange);
            return;
        }
        List<Map<String, Object>> orders = service.getHistory().stream()
                .map(OrderHandler::toMap)
                .toList();
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("orders", orders);
        body.put("count", orders.size());
        body.put("revenue", service.getTotalRevenue());
        HttpSupport.sendJson(exchange, 200, Json.write(body));
    }
}
