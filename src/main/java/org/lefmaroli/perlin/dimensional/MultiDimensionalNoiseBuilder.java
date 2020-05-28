package org.lefmaroli.perlin.dimensional;

import org.lefmaroli.perlin.INoiseGenerator;
import org.lefmaroli.perlin.NoiseBuilder;
import org.lefmaroli.perlin.data.NoiseData;

public abstract class MultiDimensionalNoiseBuilder<ReturnType extends NoiseData<?, ReturnType>,
        NoiseType extends INoiseGenerator<ReturnType>,
        MultiDimensionalNoiseBuilderType extends MultiDimensionalNoiseBuilder<ReturnType, NoiseType,
                MultiDimensionalNoiseBuilderType>>
        extends NoiseBuilder<ReturnType, NoiseType, MultiDimensionalNoiseBuilderType> {

    private boolean isCircular = false;

    public MultiDimensionalNoiseBuilder(int dimensions) {
        super(dimensions);
    }

    public MultiDimensionalNoiseBuilderType withCircularBounds() {
        isCircular = true;
        return self();
    }

    protected boolean isCircular() {
        return isCircular;
    }
}
