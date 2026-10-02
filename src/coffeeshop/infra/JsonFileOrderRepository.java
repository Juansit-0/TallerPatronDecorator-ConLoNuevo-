package coffeeshop.infra;

import coffeeshop.application.OrderRepository;
import coffeeshop.application.model.OrderResult;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class JsonFileOrderRepository implements OrderRepository {

    private final Path file;
    private final List<OrderResult> orders;

    public JsonFileOrderRepository(Path file) {
        this.file = file.toAbsolutePath().normalize();
        this.orders = new ArrayList<>(read());
    }

    @Override
    public synchronized List<OrderResult> loadAll() {
        return List.copyOf(orders);
    }

    @Override
    public synchronized void save(OrderResult order) {
        orders.add(order);
        write();
    }

    public Path getFile() {
        return file;
    }

    private List<OrderResult> read() {
        if (!Files.isRegularFile(file)) {
            return List.of();
        }
        try {
            String content = Files.readString(file, StandardCharsets.UTF_8);
            if (content.isBlank()) {
                return List.of();
            }
            Object parsed = Json.parse(content);
            List<OrderResult> loaded = new ArrayList<>();
            if (parsed instanceof List<?> list) {
                for (Object item : list) {
                    if (item instanceof Map<?, ?> map) {
                        loaded.add(OrderJson.fromMap(map));
                    }
                }
            }
            return loaded;
        } catch (IOException error) {
            throw new UncheckedIOException("No se pudo leer " + file, error);
        } catch (IllegalArgumentException error) {
            throw new IllegalStateException("El archivo de pedidos esta corrupto: " + file, error);
        }
    }

    private void write() {
        try {
            Path parent = file.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            Path temp = file.resolveSibling(file.getFileName() + ".tmp");
            String json = Json.write(orders.stream().map(OrderJson::toMap).toList());
            Files.writeString(temp, json, StandardCharsets.UTF_8);
            Files.move(temp, file, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (IOException error) {
            throw new UncheckedIOException("No se pudo guardar " + file, error);
        }
    }
}
