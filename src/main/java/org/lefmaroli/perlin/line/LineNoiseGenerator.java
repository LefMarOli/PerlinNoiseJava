package org.lefmaroli.perlin.line;

import org.lefmaroli.perlin.INoiseGenerator;
import org.lefmaroli.perlin.dimensional.MultiDimensionalNoiseGenerator;

public interface LineNoiseGenerator
        extends INoiseGenerator<LineNoiseDataContainer>, MultiDimensionalNoiseGenerator {

    @Override
    default int getDimensions() {
        return 2;
    }

    int getLineLength();
}
