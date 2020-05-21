package org.lefmaroli.perlin.point;

import org.lefmaroli.perlin.NoiseGenerator;

public abstract class NoisePointGenerator extends NoiseGenerator {

    public abstract Double[] getNextPoints(int count);

    @Override
    public int getDimensions() {
        return 1;
    }
}
