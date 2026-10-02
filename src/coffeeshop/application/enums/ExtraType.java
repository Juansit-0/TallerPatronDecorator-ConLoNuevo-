package coffeeshop.application.enums;

import coffeeshop.domain.Beverage;
import coffeeshop.domain.decorators.CaramelDecorator;
import coffeeshop.domain.decorators.DecafDecorator;
import coffeeshop.domain.decorators.ExtraShotDecorator;
import coffeeshop.domain.decorators.HappyHourDecorator;
import coffeeshop.domain.decorators.HoneyDecorator;
import coffeeshop.domain.decorators.IcedDecorator;
import coffeeshop.domain.decorators.MilkDecorator;
import coffeeshop.domain.decorators.OatMilkDecorator;
import coffeeshop.domain.decorators.VanillaDecorator;
import coffeeshop.domain.decorators.WhippedCreamDecorator;
import java.util.function.UnaryOperator;

public enum ExtraType {

    SHOT("SHOT", "Shot extra", 0.80, ExtraShotDecorator::new),
    MILK("MILK", "Leche", 0.50, MilkDecorator::new),
    CARAMEL("CARAMEL", "Caramelo", 0.60, CaramelDecorator::new),
    VANILLA("VANILLA", "Vainilla", 0.55, VanillaDecorator::new),
    WHIP("WHIP", "Crema batida", 0.70, WhippedCreamDecorator::new),
    HONEY("HONEY", "Miel", 0.45, HoneyDecorator::new),
    OAT("OAT", "Leche de avena", 0.75, OatMilkDecorator::new),
    DECAF("DECAF", "Descafeinado", 0.00, DecafDecorator::new),
    ICED("ICED", "Helado", 0.30, IcedDecorator::new),
    HAPPYHOUR("HAPPYHOUR", "Happy Hour", -1.00, HappyHourDecorator::new);

    private final String code;
    private final String label;
    private final double price;
    private final UnaryOperator<Beverage> wrapper;

    ExtraType(String code, String label, double price, UnaryOperator<Beverage> wrapper) {
        this.code = code;
        this.label = label;
        this.price = price;
        this.wrapper = wrapper;
    }

    public Beverage apply(Beverage inner) {
        return wrapper.apply(inner);
    }

    public String getCode() {
        return code;
    }

    public String getLabel() {
        return label;
    }

    public double getPrice() {
        return price;
    }

    public static ExtraType fromCode(String code) {
        for (ExtraType type : values()) {
            if (type.code.equalsIgnoreCase(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown extra: " + code);
    }
}
