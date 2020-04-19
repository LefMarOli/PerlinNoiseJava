package org.lefmaroli.interpolation;

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
}
