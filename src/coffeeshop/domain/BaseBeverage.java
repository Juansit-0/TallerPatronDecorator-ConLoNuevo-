package coffeeshop.domain;

import java.util.List;

public abstract class BaseBeverage implements Beverage {

    private final String code;
    private final String label;
    private final double basePrice;

    protected BaseBeverage(String code, String label, double basePrice) {
        this.code = code;
        this.label = label;
        this.basePrice = basePrice;
    }

    public String getCode() {
        return code;
    }

    @Override
    public String getDescription() {
        return label;
    }

    @Override
    public double getCost() {
        return basePrice;
    }

    @Override
    public List<BeverageLayer> getLayers() {
        return List.of(new BeverageLayer(code, label, basePrice));
    }
}
