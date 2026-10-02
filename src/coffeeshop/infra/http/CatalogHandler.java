package coffeeshop.infra.http;

import coffeeshop.application.BeverageCatalog;
import coffeeshop.application.model.Catalog;
import coffeeshop.application.model.CatalogItem;
import coffeeshop.infra.Json;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CatalogHandler implements HttpHandler {

    private final BeverageCatalog catalog = new BeverageCatalog();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            HttpSupport.methodNotAllowed(exchange);
            return;
        }
        Catalog data = catalog.getCatalog();
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("drinks", toItems(data.drinks()));
        body.put("extras", toItems(data.extras()));
        body.put("sizes", toItems(data.sizes()));
        HttpSupport.sendJson(exchange, 200, Json.write(body));
    }

    private static List<Map<String, Object>> toItems(List<CatalogItem> items) {
        return items.stream().map(item -> {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("code", item.code());
            map.put("label", item.label());
            map.put("price", item.price());
            return map;
        }).toList();
    }
}
