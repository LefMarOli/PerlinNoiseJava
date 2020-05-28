package org.lefmaroli.perlin;

public interface INoiseGenerator<RawDataType, ReturnType extends NoiseData<RawDataType, ReturnType>> {
    ReturnType getNext(int count);

    double getMaxAmplitude();

    int getDimensions();
}
