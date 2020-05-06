package org.lefmaroli.rounding;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class RoundUtilsTest {

    private final List<Integer> testList = new ArrayList<>();

    @Before
    public void setup(){
        for (int i = 0; i < 10; i++) {
            testList.add((int)Math.pow(2, i));
        }
    }

    @Test
    public void isPowerOfTwoTest(){
        for (Integer integer : testList) {
            assertTrue(RoundUtils.isPowerOfTwo(integer));
        }
    }

    @Test
    public void isPowerOfTwoTestFalse(){
        for (int i = 1; i < testList.size(); i++) {
            assertFalse(RoundUtils.isPowerOfTwo(testList.get(i) + 1));
        }
    }

    @Test
    public void isPowerOfTwoTestFalseForNegativeNumbers(){
        for (Integer integer : testList) {
            assertFalse(RoundUtils.isPowerOfTwo(-integer));
        }
    }

    @Test
    public void testRoundUpToPowerOfTwoSmallerNumber(){
        for (int i = 2; i < testList.size(); i++) {
            assertEquals(testList.get(i), RoundUtils.ceilToPowerOfTwo(testList.get(i) - 1), 0);
        }
    }

    @Test
    public void testRoundUpToPowerOfTwoEqualNumber(){
        for (Integer integer : testList) {
            assertEquals(integer, RoundUtils.ceilToPowerOfTwo(integer), 0);
        }
    }

    @Test
    public void testRoundDownToPowerOfTwoBiggerNumber(){
        for (int i = 1; i < testList.size(); i++) {
            assertEquals(testList.get(i), RoundUtils.floorToPowerOfTwo(testList.get(i) + 1), 0);
        }
    }

    @Test
    public void testRoundDownToPowerOfTwoEqualNumber(){
        for (Integer integer : testList) {
            assertEquals(integer, RoundUtils.floorToPowerOfTwo(integer), 0);
        }
    }
}