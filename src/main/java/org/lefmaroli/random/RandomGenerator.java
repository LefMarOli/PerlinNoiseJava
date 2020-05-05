package org.lefmaroli.random;

import org.lefmaroli.vector.UnitVector;

import java.util.Random;

public class RandomGenerator {

    private final Random basicRandGenerator;

    RandomGenerator(){
        this(System.currentTimeMillis());
    }

    public RandomGenerator(long seed){
        this.basicRandGenerator = new Random(seed);
    }

    public UnitVector getRandomUnitVector2D(){
        double angle = basicRandGenerator.nextDouble() * 2 * Math.PI;
        return new UnitVector(Math.cos(angle), Math.sin(angle));
    }
}
