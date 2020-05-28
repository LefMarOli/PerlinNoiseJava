package org.lefmaroli.perlin.point;

import org.lefmaroli.perlin.INoiseGenerator;

public interface PointNoiseGenerator extends INoiseGenerator<Double[]> {

    @Override
    default int getDimensions() {
        return 1;
    }
}
