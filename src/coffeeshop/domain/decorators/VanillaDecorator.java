package coffeeshop.domain.decorators;

import coffeeshop.domain.Beverage;
import coffeeshop.domain.BeverageLayer;
import java.util.List;

public class VanillaDecorator extends BeverageDecorator {

    private static final String CODE = "VANILLA";
    private static final String LABEL = "Vainilla";
    private static final double PRICE = 0.55;

    public VanillaDecorator(Beverage inner) {
        super(inner);
    }

    @Override
    public String getDescription() {
        return inner.getDescription() + " + vainilla";
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
