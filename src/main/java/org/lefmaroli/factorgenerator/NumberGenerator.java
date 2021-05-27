package org.lefmaroli.factorgenerator;

public interface NumberGenerator<N extends Number> extends ReusableGenerator, Iterable<N> {

  N getNext();

  NumberGenerator<N> getCopy();
}
