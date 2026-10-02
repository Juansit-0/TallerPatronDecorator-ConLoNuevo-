package coffeeshop.application;

import coffeeshop.application.enums.DrinkType;
import coffeeshop.application.enums.ExtraType;
import coffeeshop.application.model.Catalog;
import coffeeshop.application.model.CatalogItem;
import coffeeshop.domain.Size;
import java.util.Arrays;
import java.util.List;

public class BeverageCatalog {

    public Catalog getCatalog() {
        List<CatalogItem> drinks = Arrays.stream(DrinkType.values())
                .map(drink -> new CatalogItem(drink.getCode(), drink.getLabel(), drink.getBasePrice()))
                .toList();
        List<CatalogItem> extras = Arrays.stream(ExtraType.values())
                .map(extra -> new CatalogItem(extra.getCode(), extra.getLabel(), extra.getPrice()))
                .toList();
        List<CatalogItem> sizes = Arrays.stream(Size.values())
                .map(size -> new CatalogItem(size.getCode(), size.getLabel(), size.getPriceDelta()))
                .toList();
        return new Catalog(drinks, extras, sizes);
    }
}
