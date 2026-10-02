package coffeeshop.application;

import coffeeshop.application.model.OrderResult;
import java.util.ArrayList;
import java.util.List;

public class InMemoryOrderRepository implements OrderRepository {

    private final List<OrderResult> orders = new ArrayList<>();

    @Override
    public synchronized List<OrderResult> loadAll() {
        return List.copyOf(orders);
    }

    @Override
    public synchronized void save(OrderResult order) {
        orders.add(order);
    }
}
