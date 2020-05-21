package org.lefmaroli.perlin.exceptions;

public class TooManyInterpolationPointsException extends InterpolationPointException {

    private static final String TOO_MANY_INTERPOLATION_POINTS_MESSAGE_FORMAT =
            "Number of interpolation points %s is greater than %s";

    public TooManyInterpolationPointsException(int interpolationPointsCount, int limit) {
        super(constructMessage(interpolationPointsCount, limit));
    }

    private static String constructMessage(int interpolationPointsCount, int limit) {
        return String.format(TOO_MANY_INTERPOLATION_POINTS_MESSAGE_FORMAT, interpolationPointsCount, limit);
    }
}
