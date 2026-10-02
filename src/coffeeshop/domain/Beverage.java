package coffeeshop.domain;

import java.util.List;

public interface Beverage {

    String getDescription();

    double getCost();

    List<BeverageLayer> getLayers();
}
