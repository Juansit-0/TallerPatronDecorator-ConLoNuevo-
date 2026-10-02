package coffeeshop.domain.decorators;

import coffeeshop.domain.Beverage;
import coffeeshop.domain.BeverageLayer;
import coffeeshop.domain.Size;
import java.util.List;

public class SizeDecorator extends BeverageDecorator {

    private final Size size;

    public SizeDecorator(Beverage inner, Size size) {
        super(inner);
        this.size = size;
    }

    @Override
    public String getDescription() {
        return inner.getDescription() + " (" + size.getLabel() + ")";
    }

    @Override
    public double getCost() {
        return inner.getCost() + size.getPriceDelta();
    }

    @Override
    public List<BeverageLayer> getLayers() {
        return layersWith(new BeverageLayer(size.getCode(), "Tamaño " + size.getLabel(), size.getPriceDelta()));
    }
}
