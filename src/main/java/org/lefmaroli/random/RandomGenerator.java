package org.lefmaroli.random;

import org.lefmaroli.vector.Vector2D;
import org.lefmaroli.vector.Vector3D;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RandomGenerator {

    private final static List<Vector2D> UNIT_VECTORS_TEMPLATES_2D = new ArrayList<>(360);
    private final static List<Vector3D> UNIT_VECTORS_TEMPLATES_3D = new ArrayList<>(10000);

    static {
        //Generate unit 2D vectors
        for (int angle = 0; angle < 360; angle++) {
            double radian = angle * Math.PI / 180.0;
            UNIT_VECTORS_TEMPLATES_2D.add(new Vector2D(Math.cos(radian), Math.sin(radian)));
        }
    }

    private final Random basicRandGenerator;

    RandomGenerator() {
        this(System.currentTimeMillis());
    }

    public RandomGenerator(long seed) {
        this.basicRandGenerator = new Random(seed);
    }

    public Vector2D getRandomUnitVector2D() {
        int angle = basicRandGenerator.nextInt(UNIT_VECTORS_TEMPLATES_2D.size());
        return UNIT_VECTORS_TEMPLATES_2D.get(angle);
    }

    public Vector3D getRandomUnitVector3D() {
        if (UNIT_VECTORS_TEMPLATES_3D.isEmpty()) {
            generate3DSamples(basicRandGenerator);
        }
        int rand = basicRandGenerator.nextInt(UNIT_VECTORS_TEMPLATES_3D.size());
        return UNIT_VECTORS_TEMPLATES_3D.get(rand);
    }

    private static void generate3DSamples(Random randomGenerator) {
        double lengthLimit = 1E-4;
        double max = 1.0;
        double min = -1.0;
        for (int i = 0; i < 10000; i++) {
            Vector3D vector3D = new Vector3D(0, 0, 0);
            double length = 0.0;
            while (length < lengthLimit) {
                double x = randomGenerator.nextDouble() * (max - min + 1) + min;
                double y = randomGenerator.nextDouble() * (max - min + 1) + min;
                double z = randomGenerator.nextDouble() * (max - min + 1) + min;
                vector3D = new Vector3D(x, y, z);
                length = vector3D.getLength();
            }
            UNIT_VECTORS_TEMPLATES_3D.add(vector3D.normalize());
        }
    }
}
