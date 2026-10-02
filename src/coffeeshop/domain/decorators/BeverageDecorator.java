package coffeeshop.domain.decorators;

import coffeeshop.domain.Beverage;
import coffeeshop.domain.BeverageLayer;
import java.util.ArrayList;
import java.util.List;

public abstract class BeverageDecorator implements Beverage {

    protected final Beverage inner;

    protected BeverageDecorator(Beverage inner) {
        this.inner = inner;
    }

    @Override
    public String getDescription() {
        return inner.getDescription();
    }

    @Override
    public double getCost() {
        return inner.getCost();
    }

    @Override
    public List<BeverageLayer> getLayers() {
        return inner.getLayers();
    }

    protected List<BeverageLayer> layersWith(BeverageLayer layer) {
        List<BeverageLayer> layers = new ArrayList<>(inner.getLayers());
        layers.add(layer);
        return layers;
    }
}
