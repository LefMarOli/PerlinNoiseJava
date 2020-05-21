package org.lefmaroli.perlin.exceptions;

public class NoInterpolationPointException extends InterpolationPointException {

    private static final String NO_INTERPOLATION_MESSAGE = "Number of interpolation points smaller than 1";

    public NoInterpolationPointException() {
        super(NO_INTERPOLATION_MESSAGE);
    }
}
