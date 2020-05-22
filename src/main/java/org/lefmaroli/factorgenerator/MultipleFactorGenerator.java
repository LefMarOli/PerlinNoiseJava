package org.lefmaroli.factorgenerator;

import java.util.ArrayList;
import java.util.List;

public class MultipleFactorGenerator implements ReusableGenerator {

    private final List<SingleFactorGenerator> factorGenerators;

    public MultipleFactorGenerator(List<SingleFactorGenerator> factorGenerators) {
        this.factorGenerators = factorGenerators;
    }

    public List<Double> getNextFactors(){
        List<Double> results = new ArrayList<>(factorGenerators.size());
        for (SingleFactorGenerator factorGenerator : factorGenerators) {
            results.add(factorGenerator.getNextFactor());
        }
        return results;
    }

    public int getNumberOfFactors(){
        return factorGenerators.size();
    }

    @Override
    public void reset() {
        for (SingleFactorGenerator factorGenerator : factorGenerators) {
            factorGenerator.reset();
        }
    }

    public static MultipleFactorGenerator getSymmetricalFactorGenerator(int dimensions, SingleFactorGenerator factorGenerator) {
        List<SingleFactorGenerator> defaultGenerators = new ArrayList<>(dimensions);
        for (int i = 0; i < dimensions; i++) {
            defaultGenerators.add(factorGenerator.getCopy());
        }
        return new MultipleFactorGenerator(defaultGenerators);
    }
}
