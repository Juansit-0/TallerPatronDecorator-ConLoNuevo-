package coffeeshop.application.model;

import coffeeshop.application.enums.DrinkType;
import coffeeshop.application.enums.ExtraType;
import coffeeshop.domain.Size;
import java.util.List;

public record OrderRequest(DrinkType base, Size size, List<ExtraType> extras) {
}
