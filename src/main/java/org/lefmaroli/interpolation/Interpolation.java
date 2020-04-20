package org.lefmaroli.interpolation;

import java.util.Map;

public class Interpolation {

    public static double linear(double y1, double y2, double mu) {
        return (y1 * (1 - mu) + y2 * mu);
    }

    public static double linearWithFade(double y1, double y2, double mu) {
        return linear(y1, y2, fade(mu));
    }

    public static double fade(double value) {
        double valueCubed = Math.pow(value, 3);
        //6t^5 - 15t^4 + 10t^3
        return 6 * valueCubed * value * value - 15 * valueCubed * value + 10 * valueCubed;
    }

    public static Map.Entry<Integer, Double> findClosestLowerIndex(int referenceIndex, int referenceSize, int targetSize) {

        double relativeIndexPosition = referenceIndex / (double) referenceSize;

        //only search in an area of -10/+10 around original index
        int closestLowerIndex = 0;
        double closestRelativeDistance = Double.MAX_VALUE;
        double previousClosestDistance = Double.MAX_VALUE;
        for (int j = referenceIndex - 10; j < referenceIndex + 10; j++) {
            if (j < 0 || j >= targetSize) {
                continue;
            }
            double relativeNewIndexPosition = j / (double) targetSize;
            double relativeDistance = Math.abs(relativeNewIndexPosition - relativeIndexPosition);
            if (relativeDistance >= closestRelativeDistance) {
                if (previousClosestDistance < relativeDistance) {
                    closestLowerIndex--;
                }
                break;
            }
            if (relativeDistance < closestRelativeDistance) {
                closestLowerIndex = j;
                previousClosestDistance = closestRelativeDistance;
                closestRelativeDistance = relativeDistance;
            }
        }
        if (closestLowerIndex == targetSize - 1){
            closestLowerIndex--;
        }
        return Map.entry(closestLowerIndex, closestRelativeDistance);
    }
}
