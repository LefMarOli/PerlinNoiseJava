package org.lefmaroli.perlin.line;

import org.lefmaroli.perlin.RootNoiseGenerator;

public abstract class RootLineNoiseGenerator extends RootNoiseGenerator<LineNoiseDataContainer, LineNoiseData> {

    public RootLineNoiseGenerator(int noiseInterpolationPoints) {
        super(noiseInterpolationPoints);
    }

    public abstract int getLineInterpolationPointsCount();
}
