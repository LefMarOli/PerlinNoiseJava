package org.lefmaroli.factorgenerator;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

public abstract class MultipliedByNumberGenerator<N extends Number> implements NumberGenerator<N> {

  public static final int DEFAULT_LIMIT = 10;
  protected final double factor;
  protected final N initialValue;
  private final List<N> computed = new LinkedList<>();
  private int currentPosition;

  protected MultipliedByNumberGenerator(N initialValue, double factor) {
    this.initialValue = initialValue;
    this.currentPosition = 0;
    this.factor = factor;
    this.computed.add(initialValue);
  }

  @Override
  public N getNext() {
    var toReturn = getAtPosition(currentPosition);
    currentPosition++;
    return toReturn;
  }

  private N getAtPosition(int position) {
    if (position > computed.size() - 1) {
      var previous = computed.get(position - 1);
      var newValue = getXMultipliedByY(previous, factor);
      computed.add(newValue);
      return newValue;
    } else {
      return computed.get(position);
    }
  }

  @Override
  public void reset() {
    currentPosition = 0;
  }

  protected abstract N getXMultipliedByY(N x, double y);

  @Override
  public Iterator<N> iterator() {
    return new MultipliedByNumberGeneratorIterator();
  }

  private class MultipliedByNumberGeneratorIterator implements Iterator<N> {

    private int iteratorPosition;

    public MultipliedByNumberGeneratorIterator() {
      this.iteratorPosition = 0;
    }

    @Override
    public boolean hasNext() {
      return iteratorPosition < DEFAULT_LIMIT;
    }

    @Override
    public N next() {
      if (iteratorPosition == DEFAULT_LIMIT || iteratorPosition > DEFAULT_LIMIT) {
        throw new NoSuchElementException(
            "Reached default limit of " + DEFAULT_LIMIT + " numbers allowed by this generator");
      }
      var toReturn = getAtPosition(iteratorPosition);
      iteratorPosition++;
      return toReturn;
    }
  }
}
