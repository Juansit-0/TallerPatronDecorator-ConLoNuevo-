package coffeeshop.domain.decorators;

import coffeeshop.domain.Beverage;
import coffeeshop.domain.BeverageLayer;
import java.util.List;

public class HoneyDecorator extends BeverageDecorator {

    private static final String CODE = "HONEY";
    private static final String LABEL = "Miel";
    private static final double PRICE = 0.45;

    public HoneyDecorator(Beverage inner) {
        super(inner);
    }

    @Override
    public String getDescription() {
        return inner.getDescription() + " + miel";
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
