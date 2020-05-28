package org.lefmaroli.perlin;

public interface NoiseData<RawDataType, DataType extends NoiseData<RawDataType, DataType>> {

    void add(DataType other);

    void normalizeBy(double maxValue);

    RawDataType getAsRawData();
}
