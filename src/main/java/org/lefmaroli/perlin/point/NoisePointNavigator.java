package org.lefmaroli.perlin.point;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class NoisePointNavigator {

    private static final Logger LOGGER = LogManager.getLogger(NoisePointNavigator.class);
    private final Vector<Double> generated = new Vector<>();
    private final AtomicInteger currentIndex = new AtomicInteger(0);
    private final NoisePointGenerator noiseGenerator;

    public NoisePointNavigator(NoisePointGenerator noiseGenerator) {
        this.noiseGenerator = noiseGenerator;
    }

    public List<Double> getNext(int count) {
        if (count < 1) {
            throw new IllegalArgumentException("Parameter count must be greater than 0");
        } else {
            return getNextValuesFromCurrentIndex(count);
        }
    }

    private List<Double> getNextValuesFromCurrentIndex(int count) {
        int currentIndexPosition = currentIndex.getAndUpdate(value -> value + count);
        generateNewData(count, currentIndexPosition);
        return new ArrayList<>(generated.subList(currentIndexPosition, currentIndexPosition + count));
    }

    private void generateNewData(int count, int currentIndexPosition) {
        if (generated.size() < currentIndexPosition + count) {
            Collections.addAll(generated, noiseGenerator.getNextPoints(count));
        }
    }

    public Double getNext() {
        return getNext(1).get(0);
    }

    public int getCurrentIndex() {
        return currentIndex.get();
    }

    public Double getPrevious() {
        return getPrevious(1).get(0);
    }

    public List<Double> getPrevious(int count) {
        if (count < 1) {
            throw new IllegalArgumentException("Parameter count must be greater than 0");
        } else {
            return getPreviousValues(count);
        }
    }

    private List<Double> getPreviousValues(int count) {
        int previousIndex = getAndUpdateIndexToPreviousIfInRange(count);
        if (previousIndex - count < 0) {
            throw new IndexOutOfBoundsException(
                    "Count is too high, current index is at " + previousIndex + ", queried " + count +
                            " previous values");
        }
        return getPreviousValuesFromIndex(count, previousIndex);
    }

    private List<Double> getPreviousValuesFromIndex(int count, int fromIndex) {
        List<Double> toReturn = new ArrayList<>(generated.subList(fromIndex - count, fromIndex));
        Collections.reverse(toReturn);
        return toReturn;
    }

    private int getAndUpdateIndexToPreviousIfInRange(int count) {
        return currentIndex.getAndUpdate(value -> {
            if (value - count >= 0) {
                return value - count;
            } else {
                return value;
            }
        });
    }
}
