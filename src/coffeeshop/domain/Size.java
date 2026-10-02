package coffeeshop.domain;

public enum Size {

    SMALL("SMALL", "Pequeño", -0.50),
    MEDIUM("MEDIUM", "Mediano", 0.00),
    LARGE("LARGE", "Grande", 1.00);

    private final String code;
    private final String label;
    private final double priceDelta;

    Size(String code, String label, double priceDelta) {
        this.code = code;
        this.label = label;
        this.priceDelta = priceDelta;
    }

    public String getCode() {
        return code;
    }

    public String getLabel() {
        return label;
    }

    public double getPriceDelta() {
        return priceDelta;
    }
}
