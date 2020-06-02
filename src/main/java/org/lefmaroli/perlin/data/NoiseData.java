package org.lefmaroli.perlin.data;

public interface NoiseData<RawDataType, DataType extends NoiseData> {

  void add(DataType other);

  void normalizeBy(double maxValue);

  RawDataType getAsRawData();
}
