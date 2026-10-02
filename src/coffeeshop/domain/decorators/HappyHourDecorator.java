package coffeeshop.domain.decorators;

import coffeeshop.domain.Beverage;
import coffeeshop.domain.BeverageLayer;
import java.util.List;

public class HappyHourDecorator extends BeverageDecorator {

    private static final String CODE = "HAPPYHOUR";
    private static final String LABEL = "Happy Hour";
    private static final double DISCOUNT = -1.00;

    public HappyHourDecorator(Beverage inner) {
        super(inner);
    }

    @Override
    public String getDescription() {
        return inner.getDescription() + " - happy hour";
    }

    @Override
    public double getCost() {
        return inner.getCost() + DISCOUNT;
    }

    @Override
    public List<BeverageLayer> getLayers() {
        return layersWith(new BeverageLayer(CODE, LABEL, DISCOUNT));
    }
}
