package org.lefmaroli.factorgenerator;

public interface SingleFactorGenerator extends ReusableGenerator {

    double getNextFactor();
    SingleFactorGenerator getCopy();
}
