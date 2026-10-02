package coffeeshop;

import coffeeshop.application.OrderService;
import coffeeshop.application.enums.DrinkType;
import coffeeshop.application.enums.ExtraType;
import coffeeshop.application.model.OrderRequest;
import coffeeshop.application.model.OrderResult;
import coffeeshop.application.model.ReceiptLine;
import coffeeshop.domain.Size;
import java.util.List;

public class DemoMain {

    public static void main(String[] args) {
        OrderService service = new OrderService();

        print(service.place(new OrderRequest(DrinkType.LATTE, Size.LARGE, List.of(ExtraType.SHOT, ExtraType.CARAMEL))));
        print(service.place(new OrderRequest(DrinkType.ESPRESSO, Size.SMALL, List.of())));
        print(service.place(new OrderRequest(DrinkType.AMERICANO, Size.MEDIUM, List.of(ExtraType.OAT, ExtraType.DECAF, ExtraType.HAPPYHOUR))));
        print(service.place(new OrderRequest(DrinkType.TEA, Size.LARGE, List.of(ExtraType.HONEY, ExtraType.ICED))));

        System.out.printf("Pedidos: %d | Recaudo total: %.2f%n",
                service.getHistory().size(), service.getTotalRevenue());
    }

    private static void print(OrderResult result) {
        System.out.printf("Pedido %s: %s%n", result.orderId(), result.description());
        for (ReceiptLine line : result.lines()) {
            System.out.printf("   %-10s %-18s %6.2f%n", line.code(), line.label(), line.price());
        }
        System.out.printf("   %-29s %6.2f%n%n", "TOTAL", result.total());
    }
}
