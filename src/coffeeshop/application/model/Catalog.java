package coffeeshop.application.model;

import java.util.List;

public record Catalog(List<CatalogItem> drinks, List<CatalogItem> extras, List<CatalogItem> sizes) {
}
