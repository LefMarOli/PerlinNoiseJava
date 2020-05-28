package org.lefmaroli.perlin.point;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class NoisePointNavigator {

    private static final Logger LOGGER = LogManager.getLogger(NoisePointNavigator.class);
    private final Vector<PointNoiseData> generated = new Vector<>();
    private final AtomicInteger currentIndex = new AtomicInteger(0);
    private final PointNoiseGenerator noiseGenerator;

    public NoisePointNavigator(PointNoiseGenerator noiseGenerator) {
        this.noiseGenerator = noiseGenerator;
    }

    public List<PointNoiseData> getNext(int count) {
        if (count < 1) {
            throw new IllegalArgumentException("Parameter count must be greater than 0");
        } else {
            return getNextValuesFromCurrentIndex(count);
        }
    }

    public List<Double> getNextValues(int count){
        return getNext(count).stream().map(PointNoiseData::getAsRawData).collect(Collectors.toList());
    }

    private List<PointNoiseData> getNextValuesFromCurrentIndex(int count) {
        int currentIndexPosition = currentIndex.getAndUpdate(value -> value + count);
        generateNewData(count, currentIndexPosition);
        return new ArrayList<>(generated.subList(currentIndexPosition, currentIndexPosition + count));
    }

    private void generateNewData(int count, int currentIndexPosition) {
        if (generated.size() < currentIndexPosition + count) {
            generated.addAll(noiseGenerator.getNext(count).getAsList());
        }
    }

    public PointNoiseData getNext() {
        return getNext(1).get(0);
    }

    public Double getNextValue(){
        return getNextValues(1).get(0);
    }

    public int getCurrentIndex() {
        return currentIndex.get();
    }

    public PointNoiseData getPrevious() {
        return getPrevious(1).get(0);
    }

    public List<PointNoiseData> getPrevious(int count) {
        if (count < 1) {
            throw new IllegalArgumentException("Parameter count must be greater than 0");
        } else {
            return getPreviousValues(count);
        }
    }

    private List<PointNoiseData> getPreviousValues(int count) {
        int previousIndex = getAndUpdateIndexToPreviousIfInRange(count);
        if (previousIndex - count < 0) {
            throw new IndexOutOfBoundsException(
                    "Count is too high, current index is at " + previousIndex + ", queried " + count +
                            " previous values");
        }
        return getPreviousValuesFromIndex(count, previousIndex);
    }

    private List<PointNoiseData> getPreviousValuesFromIndex(int count, int fromIndex) {
        List<PointNoiseData> toReturn = new ArrayList<>(generated.subList(fromIndex - count, fromIndex));
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
