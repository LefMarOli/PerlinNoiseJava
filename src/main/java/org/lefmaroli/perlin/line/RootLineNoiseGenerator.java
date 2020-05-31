package org.lefmaroli.perlin.line;

import org.lefmaroli.perlin.RootNoiseGenerator;
import org.lefmaroli.vector.Vector2D;

import java.util.List;

public abstract class RootLineNoiseGenerator
        extends RootNoiseGenerator<LineNoiseDataContainer, LineNoiseData, List<Vector2D>> {

    public RootLineNoiseGenerator(int noiseInterpolationPointsCount) {
        super(noiseInterpolationPointsCount);
    }

    public abstract int getLineInterpolationPointsCount();
}
