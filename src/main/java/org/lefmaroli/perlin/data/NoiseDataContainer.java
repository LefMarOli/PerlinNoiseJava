package org.lefmaroli.perlin.data;

import java.util.List;

public interface NoiseDataContainer<RawDataType,
        NoiseDataType extends NoiseData<?, NoiseDataType>,
        UnderlyingDataType extends NoiseData<?, UnderlyingDataType>>
        extends NoiseData<RawDataType, NoiseDataType> {
    List<UnderlyingDataType> getAsList();
}
