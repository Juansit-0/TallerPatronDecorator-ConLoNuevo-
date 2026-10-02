package coffeeshop.domain.decorators;

import coffeeshop.domain.Beverage;
import coffeeshop.domain.BeverageLayer;
import java.util.List;

public class ExtraShotDecorator extends BeverageDecorator {

    private static final String CODE = "SHOT";
    private static final String LABEL = "Shot extra";
    private static final double PRICE = 0.80;

    public ExtraShotDecorator(Beverage inner) {
        super(inner);
    }

    @Override
    public String getDescription() {
        return inner.getDescription() + " + shot extra";
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
