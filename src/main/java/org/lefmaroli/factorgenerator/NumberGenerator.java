package org.lefmaroli.factorgenerator;

public interface NumberGenerator<N extends Number> extends ReusableGenerator {

  N getNext();

  NumberGenerator<N> getCopy();
}
