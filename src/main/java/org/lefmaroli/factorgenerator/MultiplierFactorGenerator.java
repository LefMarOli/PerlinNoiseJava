package org.lefmaroli.factorgenerator;


public class MultiplierFactorGenerator implements SingleFactorGenerator {

    private final double factor;
    private final double initialValue;
    private double previousValue;
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

    @Override
    public SingleFactorGenerator getCopy() {
        return new MultiplierFactorGenerator(this.initialValue, this.factor);
    }

    @Override
    public void reset() {
        firstCall = true;
    }

}
