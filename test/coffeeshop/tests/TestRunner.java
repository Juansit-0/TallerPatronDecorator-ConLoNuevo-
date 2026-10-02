package coffeeshop.tests;

import coffeeshop.application.InMemoryOrderRepository;
import coffeeshop.application.OrderRequestBuilder;
import coffeeshop.application.OrderService;
import coffeeshop.application.enums.DrinkType;
import coffeeshop.application.enums.ExtraType;
import coffeeshop.application.model.OrderRequest;
import coffeeshop.application.model.OrderResult;
import coffeeshop.application.model.ReceiptLine;
import coffeeshop.domain.Beverage;
import coffeeshop.domain.Size;
import coffeeshop.domain.decorators.CaramelDecorator;
import coffeeshop.domain.decorators.ExtraShotDecorator;
import coffeeshop.domain.decorators.SizeDecorator;
import coffeeshop.domain.drinks.Espresso;
import coffeeshop.domain.drinks.Latte;
import coffeeshop.infra.JsonFileOrderRepository;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;

public class TestRunner {

    private static int passed = 0;
    private static int failed = 0;

    public static void main(String[] args) {
        run("espresso base cost is 2.00", () -> assertEquals(2.00, new Espresso().getCost()));
        run("espresso description", () -> assertEquals("Espresso", new Espresso().getDescription()));
        run("latte base cost is 3.50", () -> assertEquals(3.50, new Latte().getCost()));

        run("extra shot adds 0.80", () ->
                assertEquals(4.30, new ExtraShotDecorator(new Latte()).getCost()));

        run("caramel adds 0.60", () ->
                assertEquals(4.10, new CaramelDecorator(new Latte()).getCost()));

        run("large size adds 1.00", () ->
                assertEquals(4.50, new SizeDecorator(new Latte(), Size.LARGE).getCost()));

        run("small size subtracts 0.50", () ->
                assertEquals(3.00, new SizeDecorator(new Latte(), Size.SMALL).getCost()));

        run("case study: latte + shot + caramel + large = 5.90", () -> {
            OrderService service = new OrderService();
            OrderResult result = service.place(new OrderRequest(
                    DrinkType.LATTE, Size.LARGE, List.of(ExtraType.SHOT, ExtraType.CARAMEL)));
            assertEquals(5.90, result.total());
        });

        run("decorator chain is recursive and order independent in cost", () -> {
            Beverage a = new CaramelDecorator(new ExtraShotDecorator(new Latte()));
            Beverage b = new ExtraShotDecorator(new CaramelDecorator(new Latte()));
            assertEquals(a.getCost(), b.getCost());
        });

        run("description grows with the chain", () -> {
            OrderService service = new OrderService();
            OrderResult result = service.place(new OrderRequest(
                    DrinkType.LATTE, Size.MEDIUM, List.of(ExtraType.MILK, ExtraType.VANILLA)));
            assertEquals("Latte + leche + vainilla (Mediano)", result.description());
        });

        run("layers include base, extras and size", () -> {
            OrderService service = new OrderService();
            OrderResult result = service.place(new OrderRequest(
                    DrinkType.ESPRESSO, Size.LARGE, List.of(ExtraType.SHOT)));
            assertEquals(3, result.lines().size());
            assertEquals("ESPRESSO", result.lines().get(0).code());
            assertEquals("SHOT", result.lines().get(1).code());
            assertEquals("LARGE", result.lines().get(2).code());
        });

        run("happy hour subtracts 1.00", () -> {
            OrderService service = new OrderService();
            OrderResult result = service.place(new OrderRequest(
                    DrinkType.ESPRESSO, Size.LARGE, List.of(ExtraType.HAPPYHOUR)));
            assertEquals(2.00, result.total());
        });

        run("decaf changes description but costs nothing", () -> {
            OrderService service = new OrderService();
            OrderResult result = service.place(new OrderRequest(
                    DrinkType.AMERICANO, Size.MEDIUM, List.of(ExtraType.DECAF)));
            assertEquals(2.50, result.total());
            assertEquals("Americano + descafeinado (Mediano)", result.description());
        });

        run("history tracks every order", () -> {
            OrderService service = new OrderService();
            service.place(new OrderRequest(DrinkType.TEA, Size.MEDIUM, List.of()));
            service.place(new OrderRequest(DrinkType.TEA, Size.LARGE, List.of(ExtraType.HONEY)));
            assertEquals(2, service.getHistory().size());
        });

        run("layers prices sum to total", () -> {
            OrderService service = new OrderService();
            OrderResult result = service.place(new OrderRequest(
                    DrinkType.LATTE, Size.LARGE,
                    List.of(ExtraType.SHOT, ExtraType.CARAMEL, ExtraType.WHIP)));
            double sum = result.lines().stream().mapToDouble(ReceiptLine::price).sum();
            assertEquals(Math.round(sum * 100.0) / 100.0, result.total());
        });

        run("orders survive a restart through the json file", () -> {
            Path file = Files.createTempDirectory("coffeeshop").resolve("orders.json");
            OrderService first = new OrderService(new JsonFileOrderRepository(file));
            first.place(new OrderRequest(DrinkType.LATTE, Size.LARGE, List.of(ExtraType.SHOT, ExtraType.CARAMEL)));
            first.place(new OrderRequest(DrinkType.TEA, Size.SMALL, List.of()));
            OrderService second = new OrderService(new JsonFileOrderRepository(file));
            assertEquals(2, second.getHistory().size());
            assertEquals(7.40, second.getTotalRevenue());
            assertEquals("SHOT", second.getHistory().get(0).lines().get(1).code());
        });

        run("order numbers continue after a restart", () -> {
            Path file = Files.createTempDirectory("coffeeshop").resolve("orders.json");
            new OrderService(new JsonFileOrderRepository(file))
                    .place(new OrderRequest(DrinkType.ESPRESSO, Size.MEDIUM, List.of()));
            OrderResult next = new OrderService(new JsonFileOrderRepository(file))
                    .place(new OrderRequest(DrinkType.ESPRESSO, Size.MEDIUM, List.of()));
            assertEquals("ORD-0002", next.orderId());
        });

        run("history is paginated newest first", () -> {
            OrderService service = new OrderService();
            for (int i = 0; i < 5; i++) {
                service.place(new OrderRequest(DrinkType.ESPRESSO, Size.MEDIUM, List.of()));
            }
            assertEquals(3, service.getHistoryPage(1, 2).totalPages());
            assertEquals("ORD-0005", service.getHistoryPage(1, 2).orders().get(0).orderId());
            assertEquals(1, service.getHistoryPage(3, 2).orders().size());
        });

        run("revenue is grouped by date", () -> {
            Clock clock = Clock.fixed(Instant.parse("2026-10-02T15:00:00Z"), ZoneOffset.UTC);
            OrderService service = new OrderService(new InMemoryOrderRepository(), clock);
            service.place(new OrderRequest(DrinkType.ESPRESSO, Size.MEDIUM, List.of()));
            service.place(new OrderRequest(DrinkType.LATTE, Size.MEDIUM, List.of()));
            assertEquals(5.50, service.getRevenueByDate().get("2026-10-02"));
        });

        run("builder assembles a request step by step", () -> {
            OrderRequest request = new OrderRequestBuilder()
                    .base("latte")
                    .size("large")
                    .extra(ExtraType.SHOT)
                    .extra("CARAMEL")
                    .build();
            assertEquals(5.90, new OrderService().place(request).total());
        });

        run("builder defaults to medium and rejects a missing base", () -> {
            assertEquals("MEDIUM", new OrderRequestBuilder().base(DrinkType.TEA).build().size().getCode());
            try {
                new OrderRequestBuilder().extra(ExtraType.MILK).build();
                throw new AssertionError("se esperaba un error por falta de base");
            } catch (IllegalArgumentException expected) {
                assertEquals("Falta la bebida base", expected.getMessage());
            }
        });

        System.out.printf("%nResultado: %d pruebas OK, %d fallidas%n", passed, failed);
        if (failed > 0) {
            System.exit(1);
        }
    }

    private static void run(String name, TestCase test) {
        try {
            test.execute();
            passed++;
            System.out.println("PASS  " + name);
        } catch (Throwable error) {
            failed++;
            System.out.println("FAIL  " + name + " -> " + error.getMessage());
        }
    }

    private static void assertEquals(double expected, double actual) {
        if (Math.abs(expected - actual) > 0.0001) {
            throw new AssertionError("esperado " + expected + " pero fue " + actual);
        }
    }

    private static void assertEquals(String expected, String actual) {
        if (!expected.equals(actual)) {
            throw new AssertionError("esperado \"" + expected + "\" pero fue \"" + actual + "\"");
        }
    }

    private static void assertEquals(int expected, int actual) {
        if (expected != actual) {
            throw new AssertionError("esperado " + expected + " pero fue " + actual);
        }
    }

    @FunctionalInterface
    private interface TestCase {
        void execute() throws Exception;
    }
}
