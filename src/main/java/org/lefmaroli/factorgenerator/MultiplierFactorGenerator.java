package org.lefmaroli.factorgenerator;


public class MultiplierFactorGenerator implements FactorGenerator {

    protected final double factor;
    private final double initialValue;
    protected double previousValue;
    private boolean firstCall = true;

    public MultiplierFactorGenerator(double initialValue, double factor) {
        this.initialValue = initialValue;
        this.factor = factor;
    }

    @Override
    public double getNextFactor() {
        if (firstCall) {
            firstCall = false;
            previousValue = initialValue;
            return initialValue;
        } else {
            double toReturn = previousValue * factor;
            previousValue = toReturn;
            return toReturn;
        }
    }

}
