package org.lefmaroli.perlin;

public abstract class NoiseGenerator {
    public abstract boolean equals(Object other);
    public abstract int hashCode();
    public abstract int getDimensions();
    public abstract double getMaxAmplitude();
}
