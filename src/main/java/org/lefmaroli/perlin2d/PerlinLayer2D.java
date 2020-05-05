package org.lefmaroli.perlin2d;

import org.lefmaroli.interpolation.Interpolation;
import org.lefmaroli.random.RandomGenerator;
import org.lefmaroli.vector.UnitVector;

import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

public class PerlinLayer2D {

    private final double amplitudeFactor;
    private final int segmentLength;
    private final RandomGenerator randomGenerator;
    private final List<Queue<Double>> generated;
    private final int width;
    private final int randomBounds;
    private List<UnitVector> previousBounds;

    PerlinLayer2D(int width, int interpolationPoints, double amplitudeFactor, long randomSeed) {
        if (interpolationPoints < 0) {
            throw new IllegalArgumentException("Interpolation points must be greater or equal to 4");
        }
        this.amplitudeFactor = amplitudeFactor;
        this.segmentLength = interpolationPoints + 2;
        this.randomGenerator = new RandomGenerator(randomSeed);
        this.generated = new ArrayList<>(width);
        for (int i = 0; i < width; i++) {
            this.generated.add(new LinkedBlockingQueue<>());
        }
        this.width = width;
        this.randomBounds = 2 + width / segmentLength;
        this.previousBounds = new ArrayList<>(randomBounds);
        for (int i = 0; i < randomBounds; i++) {
            this.previousBounds.add(randomGenerator.getRandomUnitVector2D());
        }
    }

    Vector<Vector<Double>> getNextSlices(int count) {
        if (count < 1) {
            throw new IllegalArgumentException("Count must be greater than 0");
        }
        while (generated.get(0).size() < count) {
            generateNextSegment();
        }

        Vector<Vector<Double>> results = new Vector<>(count);
        for (int i = 0; i < count; i++) {
            Vector<Double> xSeries = new Vector<>(width);
            for (int j = 0; j < width; j++) {
                xSeries.add(generated.get(j).poll());
            }
            results.add(xSeries);
        }

        return results;
    }

    private void generateNextSegment() {
        //Generate new anchors
        List<UnitVector> newBounds = new ArrayList<>(randomBounds);
        for (int i = 0; i < randomBounds; i++) {
            newBounds.add(randomGenerator.getRandomUnitVector2D());
        }


        for (int i = 0; i < randomBounds - 1; i++) {
            UnitVector topLeftBound = previousBounds.get(i);
            UnitVector topRightBound = newBounds.get(i);
            UnitVector bottomLeftBound = previousBounds.get(i + 1);
            UnitVector bottomRightBound = newBounds.get(i + 1);

            //iteration through width
            for (int j = 0; j < segmentLength; j++) {
                //Check if we reached final width
                if (j + i * segmentLength >= width) {
                    break;
                }
                //iteration through length
                for (int k = 0; k < segmentLength; k++) {

                    double segmentDistance = segmentLength;
                    double xDist = (double) (k + 1) / segmentDistance;
                    double yDist = (double) (j + 1) / segmentDistance;
                    //Compute necessary interpolations
                    UnitVector topLeftDistance = new UnitVector(xDist, yDist);
                    UnitVector topRightDistance = new UnitVector(xDist - 1.0, yDist);
                    UnitVector bottomLeftDistance = new UnitVector(xDist,  yDist - 1.0);
                    UnitVector bottomRightDistance = new UnitVector(xDist - 1.0, yDist - 1.0);

                    double topLeftProduct = topLeftBound.getVectorProduct(topLeftDistance);
                    double topRightProduct = topRightBound.getVectorProduct(topRightDistance);
                    double bottomLeftProduct = bottomLeftBound.getVectorProduct(bottomLeftDistance);
                    double bottomRightProduct = bottomRightBound.getVectorProduct(bottomRightDistance);

                    //Interpolation
                    double topInterpolation = Interpolation.linearWithFade(topLeftProduct, topRightProduct, xDist);
                    double bottomInterpolation =
                            Interpolation.linearWithFade(bottomLeftProduct, bottomRightProduct, xDist);
                    double finalValue = Interpolation.linearWithFade(topInterpolation, bottomInterpolation, yDist);
                    generated.get(j + (i * segmentLength)).add(finalValue * amplitudeFactor);
                }
            }
        }

        //Update bounds
        previousBounds = newBounds;
    }
}
