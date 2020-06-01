package org.lefmaroli.perlin.line;

import org.junit.Test;

import static org.junit.Assert.*;

public class LineNoiseDataTest {


    @Test(expected = IllegalArgumentException.class)
    public void addNewLineNotSameSize(){
        LineNoiseData noiseData = new LineNoiseData(85);
        LineNoiseData other = new LineNoiseData(45);
        noiseData.add(other);
    }

}