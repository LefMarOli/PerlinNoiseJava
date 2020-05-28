package org.lefmaroli.factorgenerator;

public class DoubleGenerator extends MultipliedByNumberGenerator<Double> {
    public DoubleGenerator(Double initialValue, double factor) {
        super(initialValue, factor);
    }

    public DoubleGenerator(Integer initialValue, double factor) {
        super(initialValue.doubleValue(), factor);
    }

    @Override
    public DoubleGenerator getCopy() {
        return new DoubleGenerator(initialValue, factor);
    }

    @Override
    protected Double getXMultipliedByY(Double x, double y) {
        return x * y;
    }
}
