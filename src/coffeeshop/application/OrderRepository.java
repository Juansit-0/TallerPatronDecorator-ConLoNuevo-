package coffeeshop.application;

import coffeeshop.application.model.OrderResult;
import java.util.List;

public interface OrderRepository {

    List<OrderResult> loadAll();

    void save(OrderResult order);
}
