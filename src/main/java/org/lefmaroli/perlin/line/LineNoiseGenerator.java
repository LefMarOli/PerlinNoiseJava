package org.lefmaroli.perlin.line;

import org.lefmaroli.perlin.INoiseGenerator;

public interface LineNoiseGenerator extends INoiseGenerator<Double[][]> {

    int getLineLength();

    @Override
    default int getDimensions() {
        return 2;
    }
}
