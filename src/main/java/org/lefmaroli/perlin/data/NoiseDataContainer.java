package org.lefmaroli.perlin.data;

public interface NoiseDataContainer<
        RawDataType,
        NoiseDataType extends NoiseData<?, NoiseDataType>,
        UnderlyingDataType extends NoiseData<?, UnderlyingDataType>>
    extends NoiseData<RawDataType, NoiseDataType> {
  UnderlyingDataType[] getAsArray();
}
