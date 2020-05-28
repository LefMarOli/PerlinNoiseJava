package org.lefmaroli.perlin;

public interface INoiseGenerator<ReturnType> {
    ReturnType getNext(int count);
    double getMaxAmplitude();
    int getDimensions();
}
