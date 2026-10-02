package coffeeshop.application;

import coffeeshop.application.enums.DrinkType;
import coffeeshop.application.enums.ExtraType;
import coffeeshop.application.model.OrderRequest;
import coffeeshop.domain.Size;
import java.util.ArrayList;
import java.util.List;

public class OrderRequestBuilder {

    public static final int MAX_EXTRAS = 8;

    private DrinkType base;
    private Size size = Size.MEDIUM;
    private final List<ExtraType> extras = new ArrayList<>();

    public OrderRequestBuilder base(DrinkType base) {
        this.base = base;
        return this;
    }

    public OrderRequestBuilder base(String code) {
        return base(DrinkType.fromCode(code));
    }

    public OrderRequestBuilder size(Size size) {
        this.size = size;
        return this;
    }

    public OrderRequestBuilder size(String code) {
        return size(Size.fromCode(code));
    }

    public OrderRequestBuilder extra(ExtraType extra) {
        extras.add(extra);
        return this;
    }

    public OrderRequestBuilder extra(String code) {
        return extra(ExtraType.fromCode(code));
    }

    public OrderRequestBuilder extras(List<ExtraType> values) {
        extras.addAll(values);
        return this;
    }

    public OrderRequest build() {
        if (base == null) {
            throw new IllegalArgumentException("Falta la bebida base");
        }
        if (size == null) {
            throw new IllegalArgumentException("Falta el tamaño");
        }
        if (extras.size() > MAX_EXTRAS) {
            throw new IllegalArgumentException("Máximo " + MAX_EXTRAS + " extras por bebida");
        }
        return new OrderRequest(base, size, List.copyOf(extras));
    }
}
