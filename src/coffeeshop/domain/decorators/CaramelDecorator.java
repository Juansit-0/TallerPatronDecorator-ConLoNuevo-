package coffeeshop.domain.decorators;

import coffeeshop.domain.Beverage;
import coffeeshop.domain.BeverageLayer;
import java.util.List;

public class CaramelDecorator extends BeverageDecorator {

    private static final String CODE = "CARAMEL";
    private static final String LABEL = "Caramelo";
    private static final double PRICE = 0.60;

    public CaramelDecorator(Beverage inner) {
        super(inner);
    }

    @Override
    public String getDescription() {
        return inner.getDescription() + " + caramelo";
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
