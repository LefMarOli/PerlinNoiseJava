package org.lefmaroli.perlin1d;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Perlin1D {

    private static final Logger LOGGER = LogManager.getLogger(Perlin1D.class);
    private final Vector<Double> generated = new Vector<>();
    private final AtomicInteger currentIndex = new AtomicInteger(0);
    private final PerlinGrid1D grid1D;

    public Perlin1D(PerlinGrid1D grid1D) {
        this.grid1D = grid1D;
    }

    public List<Double> getNext(int count) {
        if (count < 1) {
            throw new IllegalArgumentException("Parameter count must be greater than 0");
        }
        final List<Double> toReturn = new ArrayList<>();
        currentIndex.getAndUpdate(value -> {
            if (generated.size() < value + count) {
                //Perform computation
                generated.addAll(grid1D.getNext(count));
            }
            toReturn.addAll(generated.subList(value, value + count));
            return value + count;
        });
        return toReturn;
    }

    public Double getNext() {
        return getNext(1).get(0);
    }

    public int getCurrentIndex(){
        return currentIndex.get();
    }

    public Double getPrevious() {
        return getPrevious(1).get(0);
    }

    public List<Double> getPrevious(int count) {
        if (count < 1) {
            throw new IllegalArgumentException("Parameter count must be greater than 0");
        } else {
            final List<Double> toReturn = new ArrayList<>(count);
            int previousIndex = currentIndex.getAndUpdate(value -> {
                if (value - count >= 0) {
                    toReturn.addAll(generated.subList(value - count, value));
                    return value - count;
                } else {
                    return value;
                }
            });
            if (toReturn.isEmpty()) {
                throw new IndexOutOfBoundsException(
                        "Count is too high, current index is at " + previousIndex + ", queried " + count +
                                " previous values");
            } else {
                Collections.reverse(toReturn);
                return toReturn;
            }
        }
    }
}
