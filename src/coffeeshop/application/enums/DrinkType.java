package coffeeshop.application.enums;

import coffeeshop.domain.Beverage;
import coffeeshop.domain.drinks.Americano;
import coffeeshop.domain.drinks.Espresso;
import coffeeshop.domain.drinks.Latte;
import coffeeshop.domain.drinks.Tea;
import java.util.function.Supplier;

public enum DrinkType {

    ESPRESSO("ESPRESSO", "Espresso", 2.00, Espresso::new),
    AMERICANO("AMERICANO", "Americano", 2.50, Americano::new),
    LATTE("LATTE", "Latte", 3.50, Latte::new),
    TEA("TEA", "Té", 2.00, Tea::new);

    private final String code;
    private final String label;
    private final double basePrice;
    private final Supplier<Beverage> factory;

    DrinkType(String code, String label, double basePrice, Supplier<Beverage> factory) {
        this.code = code;
        this.label = label;
        this.basePrice = basePrice;
        this.factory = factory;
    }

    public Beverage create() {
        return factory.get();
    }

    public String getCode() {
        return code;
    }

    public String getLabel() {
        return label;
    }

    public double getBasePrice() {
        return basePrice;
    }

    public static DrinkType fromCode(String code) {
        for (DrinkType type : values()) {
            if (type.code.equalsIgnoreCase(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown drink: " + code);
    }
}
