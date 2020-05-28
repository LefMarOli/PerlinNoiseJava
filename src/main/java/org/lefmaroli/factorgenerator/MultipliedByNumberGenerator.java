package org.lefmaroli.factorgenerator;


public abstract class MultipliedByNumberGenerator<NumberType extends Number> implements NumberGenerator<NumberType> {

    protected final double factor;
    protected final NumberType initialValue;
    private NumberType previousValue;
    private boolean firstCall = true;

    public MultipliedByNumberGenerator(NumberType initialValue, double factor) {
        this.initialValue = initialValue;
        this.factor = factor;
    }

    @Override
    public NumberType getNext() {
        if (firstCall) {
            firstCall = false;
            previousValue = initialValue;
            return initialValue;
        } else {
            NumberType newValue = getXMultipliedByY(previousValue, factor);
            previousValue = newValue;
            return newValue;
        }
    }

    @Override
    public void reset() {
        firstCall = true;
    }

    protected abstract NumberType getXMultipliedByY(NumberType x, double y);

}
