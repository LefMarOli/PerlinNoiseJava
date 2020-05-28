package org.lefmaroli.perlin;

public abstract class MultiDimensionalNoiseBuilder<ReturnType, NoiseType extends INoiseGenerator<ReturnType>,
        MultiDimensionalNoiseBuilderType extends MultiDimensionalNoiseBuilder>
        extends NoiseBuilder<ReturnType, NoiseType, MultiDimensionalNoiseBuilderType> {

    private boolean isCircular = false;

    public MultiDimensionalNoiseBuilder(int dimensions) {
        super(dimensions);
    }

    public MultiDimensionalNoiseBuilderType withCircularBounds(){
        isCircular = true;
        return self();
    }

    protected boolean isCircular(){
        return isCircular;
    }
}
