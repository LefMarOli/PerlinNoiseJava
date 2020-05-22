package org.lefmaroli.interpolation;

public class Interpolation {

    public static double linear(double y1, double y2, double mu) {
        if (mu < 0.0 || mu > 1.0) {
            throw new IllegalArgumentException("Factor mu should be bounded between [0.0, 1.0]");
        }
        return (y1 * (1 - mu) + y2 * mu);
    }

    public static double linearWithFade(double y1, double y2, double mu) {
        return linear(y1, y2, fade(mu));
    }

    public static double fade(double value) {
        if (value < 0.0 || value > 1.0) {
            throw new IllegalArgumentException("Value to fade should be bounded between [0.0, 1.0]");
        }
        double valueCubed = Math.pow(value, 3);
        //6t^5 - 15t^4 + 10t^3
        return 6 * valueCubed * value * value - 15 * valueCubed * value + 10 * valueCubed;
    }

    public static double twoDimensional(double x1y1, double x2y1, double x1y2,
                                        double x2y2, double muX, double muY){
        double topInterpolation = Interpolation.linear(x1y1, x2y1, muX);
        double bottomInterpolation = Interpolation.linear(x1y2, x2y2, muX);
        return Interpolation.linear(topInterpolation, bottomInterpolation, muY);
    }

    public static double twoDimensionalWithFade(double x1y1, double x2y1, double x1y2,
                                                double x2y2, double muX, double muY) {
        double topInterpolation = Interpolation.linearWithFade(x1y1, x2y1, muX);
        double bottomInterpolation = Interpolation.linearWithFade(x1y2, x2y2, muX);
        return Interpolation.linearWithFade(topInterpolation, bottomInterpolation, muY);
    }
}
