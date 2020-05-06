package org.lefmaroli.random;

import org.lefmaroli.vector.Vector2D;

import java.util.Random;

public class RandomGenerator {

    private final Random basicRandGenerator;

    RandomGenerator(){
        this(System.currentTimeMillis());
    }

    public RandomGenerator(long seed){
        this.basicRandGenerator = new Random(seed);
    }

    public Vector2D getRandomUnitVector2D(){
        double angle = basicRandGenerator.nextDouble() * 2 * Math.PI;
        return new Vector2D(Math.cos(angle), Math.sin(angle));
    }
}
