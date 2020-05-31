package org.lefmaroli.interpolation;

public class Interpolation {

    public static double linear(double y1, double y2, double mu) {
        if (mu < 0.0 || mu > 1.0) {
            throw new IllegalArgumentException("Factor mu should be bounded between [0.0, 1.0]");
        }
        return linearUnchecked(y1, y2, mu);
    }

    private static double linearUnchecked(double y1, double y2, double mu){
        return y1 * (1 - mu) + y2 * mu;
    }

    public static double linearWithFade(double y1, double y2, double mu) {
        return linearUnchecked(y1, y2, fade(mu));
    }

    private static double linearWithFadeUnchecked(double y1, double y2, double mu){
        return linearUnchecked(y1, y2, fadeUnchecked(mu));
    }

    public static double fade(double value) {
        if (value < 0.0 || value > 1.0) {
            throw new IllegalArgumentException("Value to fade should be bounded between [0.0, 1.0]");
        }
        return fadeUnchecked(value);
    }

    private static double fadeUnchecked(double value){
        double valueCubed = value * value * value;
        //6t^5 - 15t^4 + 10t^3
        return 6 * valueCubed * value * value - 15 * valueCubed * value + 10 * valueCubed;
    }

    public static double linear2D(double x1y1, double x2y1, double x1y2,
                                  double x2y2, double muX, double muY) {
        if (muX < 0.0 || muX > 1.0) {
            throw new IllegalArgumentException("Factor muX should be bounded between [0.0, 1.0]");
        }
        if (muY < 0.0 || muY > 1.0) {
            throw new IllegalArgumentException("Factor muY should be bounded between [0.0, 1.0]");
        }
        double topInterpolation = Interpolation.linearUnchecked(x1y1, x2y1, muX);
        double bottomInterpolation = Interpolation.linearUnchecked(x1y2, x2y2, muX);
        return Interpolation.linearUnchecked(topInterpolation, bottomInterpolation, muY);
    }

    public static double linear2DWithFade(double x1y1, double x2y1, double x1y2,
                                          double x2y2, double muX, double muY) {
        if (muX < 0.0 || muX > 1.0) {
            throw new IllegalArgumentException("Factor muX should be bounded between [0.0, 1.0]");
        }
        if (muY < 0.0 || muY > 1.0) {
            throw new IllegalArgumentException("Factor muY should be bounded between [0.0, 1.0]");
        }
        double topInterpolation = Interpolation.linearWithFadeUnchecked(x1y1, x2y1, muX);
        double bottomInterpolation = Interpolation.linearWithFadeUnchecked(x1y2, x2y2, muX);
        return Interpolation.linearWithFadeUnchecked(topInterpolation, bottomInterpolation, muY);
    }
}
