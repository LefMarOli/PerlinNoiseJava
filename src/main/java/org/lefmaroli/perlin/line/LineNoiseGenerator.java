package org.lefmaroli.perlin.line;

import org.lefmaroli.perlin.INoiseGenerator;
import org.lefmaroli.perlin.MultiDimensionalNoiseGenerator;

public interface LineNoiseGenerator
        extends INoiseGenerator<Double[][], LineNoiseDataContainer>, MultiDimensionalNoiseGenerator {

    @Override
    default int getDimensions() {
        return 2;
    }

    int getLineLength();
}
