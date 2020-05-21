package org.lefmaroli.perlin.line;

import org.lefmaroli.perlin.NoiseGenerator;

public abstract class NoiseLineGenerator extends NoiseGenerator {

    public abstract Double[][] getNextLines(int count);

    public abstract int getLineLength();

    @Override
    public int getDimensions() {
        return 2;
    }
}
