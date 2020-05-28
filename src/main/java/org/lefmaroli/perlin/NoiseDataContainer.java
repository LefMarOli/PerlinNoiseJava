package org.lefmaroli.perlin;

import java.util.List;

public interface NoiseDataContainer<RawDataType,
        RawUnderlyingDataType,
        NoiseDataType extends NoiseData<RawDataType, NoiseDataType>,
        UnderlyingDataType extends NoiseData<RawUnderlyingDataType, UnderlyingDataType>>
        extends NoiseData<RawDataType, NoiseDataType> {
    List<UnderlyingDataType> getAsList();
}
