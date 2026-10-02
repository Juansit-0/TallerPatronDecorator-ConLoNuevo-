package coffeeshop.domain.decorators;

import coffeeshop.domain.Beverage;
import coffeeshop.domain.BeverageLayer;
import java.util.List;

public class OatMilkDecorator extends BeverageDecorator {

    private static final String CODE = "OAT";
    private static final String LABEL = "Leche de avena";
    private static final double PRICE = 0.75;

    public OatMilkDecorator(Beverage inner) {
        super(inner);
    }

    @Override
    public String getDescription() {
        return inner.getDescription() + " + leche de avena";
    }

    @Override
    public double getCost() {
        return inner.getCost() + PRICE;
    }

    @Override
    public List<BeverageLayer> getLayers() {
        return layersWith(new BeverageLayer(CODE, LABEL, PRICE));
    }
}
