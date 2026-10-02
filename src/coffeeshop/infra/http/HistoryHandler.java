package coffeeshop.infra.http;

import coffeeshop.application.OrderService;
import coffeeshop.application.model.HistoryPage;
import coffeeshop.infra.Json;
import coffeeshop.infra.OrderJson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public class HistoryHandler implements HttpHandler {

    private static final int DEFAULT_PAGE_SIZE = 1000;

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
        Map<String, String> query = HttpSupport.query(exchange);
        int page = HttpSupport.intParam(query, "page", 1);
        int size = HttpSupport.intParam(query, "size", DEFAULT_PAGE_SIZE);
        HistoryPage history = service.getHistoryPage(page, size);

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("orders", history.orders().stream().map(OrderJson::toMap).toList());
        body.put("page", history.page());
        body.put("pageSize", history.pageSize());
        body.put("totalPages", history.totalPages());
        body.put("count", history.count());
        body.put("revenue", history.revenue());
        body.put("revenueByDate", new LinkedHashMap<>(history.revenueByDate()));
        HttpSupport.sendJson(exchange, 200, Json.write(body));
    }
}
