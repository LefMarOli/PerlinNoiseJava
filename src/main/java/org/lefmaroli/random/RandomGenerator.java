package org.lefmaroli.random;

import org.lefmaroli.vector.Vector2D;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RandomGenerator {

    private final Random basicRandGenerator;
    private final static List<Vector2D> UNIT_VECTORS_TEMPLATES = new ArrayList<>(360);

    static {
        for (int angle = 0; angle < 360; angle++) {
            double radian = angle * Math.PI / 180.0;
            UNIT_VECTORS_TEMPLATES.add(new Vector2D(Math.cos(radian), Math.sin(radian)));
        }
    }

    RandomGenerator() {
        this(System.currentTimeMillis());
    }

    public RandomGenerator(long seed) {
        this.basicRandGenerator = new Random(seed);
    }

    public Vector2D getRandomUnitVector2D() {
        int angle = basicRandGenerator.nextInt(360);
        return UNIT_VECTORS_TEMPLATES.get(angle);
    }
}
