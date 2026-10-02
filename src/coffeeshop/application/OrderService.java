package coffeeshop.application;

import coffeeshop.application.enums.ExtraType;
import coffeeshop.application.model.HistoryPage;
import coffeeshop.application.model.OrderRequest;
import coffeeshop.application.model.OrderResult;
import coffeeshop.application.model.ReceiptLine;
import coffeeshop.domain.Beverage;
import coffeeshop.domain.decorators.SizeDecorator;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;

public class OrderService {

    private final OrderRepository repository;
    private final Clock clock;
    private final AtomicInteger sequence;
    private final List<OrderResult> history;

    public OrderService() {
        this(new InMemoryOrderRepository(), Clock.systemDefaultZone());
    }

    public OrderService(OrderRepository repository) {
        this(repository, Clock.systemDefaultZone());
    }

    public OrderService(OrderRepository repository, Clock clock) {
        this.repository = repository;
        this.clock = clock;
        this.history = new ArrayList<>(repository.loadAll());
        this.sequence = new AtomicInteger(highestSequence(history));
    }

    public synchronized OrderResult place(OrderRequest request) {
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
                LocalDateTime.now(clock).truncatedTo(ChronoUnit.SECONDS).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                beverage.getDescription(),
                round(beverage.getCost()),
                request.base().getCode(),
                request.base().getLabel(),
                request.size().getCode(),
                request.size().getLabel(),
                lines);

        repository.save(result);
        history.add(result);
        return result;
    }

    public synchronized List<OrderResult> getHistory() {
        return List.copyOf(history);
    }

    public synchronized double getTotalRevenue() {
        return round(history.stream().mapToDouble(OrderResult::total).sum());
    }

    public synchronized Map<String, Double> getRevenueByDate() {
        Map<String, Double> totals = new TreeMap<>();
        for (OrderResult order : history) {
            totals.merge(order.createdDate(), order.total(), Double::sum);
        }
        totals.replaceAll((date, value) -> round(value));
        return totals;
    }

    public synchronized HistoryPage getHistoryPage(int page, int pageSize) {
        List<OrderResult> newestFirst = new ArrayList<>(history);
        Collections.reverse(newestFirst);
        int size = Math.max(1, pageSize);
        int totalPages = Math.max(1, (int) Math.ceil(newestFirst.size() / (double) size));
        int current = Math.min(Math.max(1, page), totalPages);
        int from = Math.min((current - 1) * size, newestFirst.size());
        int to = Math.min(from + size, newestFirst.size());
        return new HistoryPage(
                List.copyOf(newestFirst.subList(from, to)),
                current,
                size,
                totalPages,
                newestFirst.size(),
                getTotalRevenue(),
                getRevenueByDate());
    }

    private static int highestSequence(List<OrderResult> orders) {
        int highest = 0;
        for (OrderResult order : orders) {
            String digits = order.orderId().replaceAll("\\D", "");
            if (!digits.isEmpty()) {
                highest = Math.max(highest, Integer.parseInt(digits));
            }
        }
        return highest;
    }

    private static double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
