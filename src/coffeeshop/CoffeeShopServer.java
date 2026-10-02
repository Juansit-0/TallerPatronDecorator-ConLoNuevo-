package coffeeshop;

import coffeeshop.application.OrderService;
import coffeeshop.infra.JsonFileOrderRepository;
import coffeeshop.infra.http.CatalogHandler;
import coffeeshop.infra.http.HistoryHandler;
import coffeeshop.infra.http.OrderHandler;
import coffeeshop.infra.http.StaticHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.Path;
import java.util.concurrent.Executors;

public class CoffeeShopServer {

    public static void main(String[] args) throws IOException {
        int port = args.length > 0 ? Integer.parseInt(args[0]) : 8080;
        JsonFileOrderRepository repository = new JsonFileOrderRepository(Path.of("data", "orders.json"));
        OrderService service = new OrderService(repository);

        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/api/catalog", new CatalogHandler());
        server.createContext("/api/order", new OrderHandler(service));
        server.createContext("/api/orders", new HistoryHandler(service));
        server.createContext("/", new StaticHandler(Path.of("frontend")));
        server.setExecutor(Executors.newFixedThreadPool(8));
        server.start();

        System.out.println("CoffeeShopServer escuchando en http://localhost:" + port);
        System.out.println("Pedidos guardados en " + repository.getFile() + " (" + service.getHistory().size() + " cargados)");
    }
}
