package org.lefmaroli.random;

import org.lefmaroli.vector.Vector2D;
import org.lefmaroli.vector.Vector3D;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RandomGenerator {

    private final Random basicRandGenerator;
    private final static List<Vector2D> UNIT_VECTORS_TEMPLATES_2D = new ArrayList<>(360);
    private final static List<Vector3D> UNIT_VECTORS_TEMPLATES_3D = new ArrayList<>(360 * 180);

    static {
        //Generate unit 2D vectors
        for (int angle = 0; angle < 360; angle++) {
            double radian = angle * Math.PI / 180.0;
            UNIT_VECTORS_TEMPLATES_2D.add(new Vector2D(Math.cos(radian), Math.sin(radian)));
        }
    }

    static{
        //Generate unit 3D vectors
        for (int angle = 0; angle < 360; angle++){
            double radianAngle = angle * Math.PI / 180.0;
            for(int azimuth = 0; azimuth < 180; azimuth ++){
                double radianAzimuth = azimuth * Math.PI / 180.0;
                double x = Math.sin(radianAzimuth) * Math.cos(radianAngle);
                double y = Math.sin(radianAzimuth) * Math.sin(radianAngle);
                double z = Math.cos(radianAzimuth);
                UNIT_VECTORS_TEMPLATES_3D.add(new Vector3D(x, y, z).normalize());
            }
        }
    }

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
        int rand = basicRandGenerator.nextInt(UNIT_VECTORS_TEMPLATES_3D.size());
        return UNIT_VECTORS_TEMPLATES_3D.get(rand);
    }
}
