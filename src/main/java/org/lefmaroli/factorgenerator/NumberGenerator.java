package org.lefmaroli.factorgenerator;

public interface NumberGenerator<NumberType extends Number> extends ReusableGenerator {

    NumberType getNext();

    NumberGenerator<NumberType> getCopy();
}
