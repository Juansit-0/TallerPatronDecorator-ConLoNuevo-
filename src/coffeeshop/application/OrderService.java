package coffeeshop.application;

import coffeeshop.application.enums.ExtraType;
import coffeeshop.application.model.OrderRequest;
import coffeeshop.application.model.OrderResult;
import coffeeshop.application.model.ReceiptLine;
import coffeeshop.domain.Beverage;
import coffeeshop.domain.decorators.SizeDecorator;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class OrderService {

    private final AtomicInteger sequence = new AtomicInteger(0);
    private final List<OrderResult> history = new ArrayList<>();

    public OrderResult place(OrderRequest request) {
        Beverage beverage = request.base().create();
        for (ExtraType extra : request.extras()) {
            beverage = extra.apply(beverage);
        }
        beverage = new SizeDecorator(beverage, request.size());

        List<ReceiptLine> lines = beverage.getLayers().stream()
                .map(layer -> new ReceiptLine(layer.code(), layer.label(), layer.price()))
                .toList();

        OrderResult result = new OrderResult(
                String.format("ORD-%04d", sequence.incrementAndGet()),
                beverage.getDescription(),
                round(beverage.getCost()),
                request.base().getCode(),
                request.base().getLabel(),
                request.size().getCode(),
                request.size().getLabel(),
                lines);

        history.add(result);
        return result;
    }

    public List<OrderResult> getHistory() {
        return List.copyOf(history);
    }

    public double getTotalRevenue() {
        return round(history.stream().mapToDouble(OrderResult::total).sum());
    }

    private static double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
