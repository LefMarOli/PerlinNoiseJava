package org.lefmaroli.perlin1d;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lefmaroli.interpolation.Interpolation;

import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

public class Perlin1D {

    private static Logger logger = LogManager.getLogger(Perlin1D.class);

    Perlin1D() {
        this(25);
    }

    public Perlin1D(int distance) {
        this(distance, System.currentTimeMillis());
    }

    Perlin1D(int distance, long seed) {
        if (distance <= 5) {
            throw new IllegalArgumentException("Parameter distance must be greater than 5.");
        }
        this.distance = distance;
        this.step = 1.0 / distance;
        this.random = new Random(seed);
        this.lastRandom = random.nextDouble();
    }

    public List<Double> getNext(int count) {
        if (count < 1) {
            throw new IllegalArgumentException("Parameter count must be greater than 0");
        }
        if (computed.size() < count) {
            //Perform computation
            computed.addAll(computeAtLeast(count));
        }
        List<Double> result = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            result.add(computed.remove());
        }
        return result;
    }

    public Double getNext() {
        if (computed.isEmpty()) {
            computed.addAll(computeAtLeast(1));
        }
        return computed.remove();
    }

    public float getDistance() {
        return distance;
    }

    public List<Double> computeAtLeast(int count) {
        //Preprocess to save on cost of operation
        if (count < MIN_COUNT) {
            count = MIN_COUNT;
        }

        int toComputeCount = (getInterpolationBoundsCount(count, distance) - 1) * distance;
        double currentFactor = 1.0;
        int currentDistance = distance;
        List<Double> results = computeLayer(toComputeCount, currentDistance, currentFactor);

        int newLayers = 0;
        while (currentDistance / 2.0 > 25.0) {
            newLayers++;
            System.out.println(newLayers);
            currentDistance = currentDistance / 2;
            currentFactor = currentFactor / 2.0;
            List<Double> newLayer = computeLayer(toComputeCount, currentDistance, currentFactor);
            for (int i = 0; i < results.size(); i++) {
                //current relative position in results
                double relativeIndexPosition = i / (double) results.size();
                Map.Entry<Integer, Double> closest = findClosestIndex(newLayer.size(), i, relativeIndexPosition);
                double previousValue = results.get(i);
                int closestIndex = closest.getKey();
                double toAddValue = Interpolation
                        .linearWithFade(newLayer.get(closestIndex), newLayer.get(closestIndex + 1), closest.getValue());
                results.set(i, previousValue + toAddValue);
            }
        }

//        computeNoiseLayers(results);
        return results;
    }

//    private Map.Entry<Integer, Double> findClosestIndex(int newLayerSize, int i, double relativeIndexPosition) {
//        //find other closest indexes in new layer
//        //only search in an area of -10/+10 around original index
//        int closestLowerIndex = 0;
//        double closestRelativeDistance = Double.MAX_VALUE;
//        double previousClosestDistance = Double.MAX_VALUE;
//        for (int j = i - 10; j < i + 10; j++) {
//            if (j < 0 || j >= newLayerSize) {
//                continue;
//            }
//            double relativeNewIndexPosition = j / (double) newLayerSize;
//            double relativeDistance = Math.abs(relativeNewIndexPosition - relativeIndexPosition);
//            if (relativeDistance >= closestRelativeDistance) {
//                if (previousClosestDistance < relativeDistance) {
//                    closestLowerIndex--;
//                }
//                break;
//            }
//            if (relativeDistance < closestRelativeDistance) {
//                closestLowerIndex = j;
//                previousClosestDistance = closestRelativeDistance;
//                closestRelativeDistance = relativeDistance;
//            }
//        }
//        if (closestLowerIndex == newLayerSize - 1){
//            closestLowerIndex--;
//        }
//        return Map.entry(closestLowerIndex, closestRelativeDistance);
//    }

//    private void computeNoiseLayers(List<Double> results) {
//
//        for (int i = 0; i < bounds.size() - 1; i++) {
//            for (int j = 0; j < distance; j++) {
//                int index = j + distance * i;
//                Double previousValue = results.get(index);
//
//                double addedValue = Interpolation.linearWithFade(bounds.get(i),
//                        bounds.get(i + 1), 1.0 / distance * j) / factor;
//                results.set(index, previousValue + addedValue);
//            }
//        }
//        if (distance / 2.0 > 5) {
//            computeNoiseLayers(results, distance / 2.0);
//        }
//    }

    private List<Double> computeLayer(int toComputeCount, int distance, double factor) {
        List<Double> layerValues = new ArrayList<>();
        List<Double> bounds = getRandomsList(getInterpolationBoundsCount(toComputeCount, distance));
        for (int i = 0; i < bounds.size() - 1; i++) {
            for (int j = 0; j < distance; j++) {
                double addedValue = Interpolation.linearWithFade(bounds.get(i),
                        bounds.get(i + 1), 1.0 / distance * j) / factor;
                layerValues.add(addedValue);
            }
        }
        return layerValues;
    }

    private List<Double> getRandomsList(int count) {
        List<Double> randoms = new ArrayList<>(count);
        //Always start at previously generated last boundary
        randoms.add(lastRandom);
        for (int i = 0; i < count - 1; i++) {
            randoms.add(random.nextDouble());
        }
        lastRandom = randoms.get(count - 1);
        return randoms;
    }

    private int getInterpolationBoundsCount(int count, int distance) {
        return (int) Math.ceil((double) (count) / distance) + 1;
    }

    private static final int MIN_COUNT = 1000;
    private final int distance;
    private final double step;
    private final Random random;
    private final Queue<Double> computed = new LinkedBlockingQueue<>();
    private Double lastRandom;
}
