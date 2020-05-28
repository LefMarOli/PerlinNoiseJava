package org.lefmaroli.perlin;

public abstract class MultiDimensionalNoiseBuilder<RawDataType,
        ReturnType extends NoiseData<RawDataType, ReturnType>,
        NoiseType extends INoiseGenerator<RawDataType, ReturnType>,
        MultiDimensionalNoiseBuilderType extends MultiDimensionalNoiseBuilder<RawDataType, ReturnType, NoiseType,
                MultiDimensionalNoiseBuilderType>>
        extends NoiseBuilder<RawDataType, ReturnType, NoiseType, MultiDimensionalNoiseBuilderType> {

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
