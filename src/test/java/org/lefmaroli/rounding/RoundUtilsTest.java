package org.lefmaroli.rounding;

import org.junit.Test;

import static org.junit.Assert.*;

public class RoundUtilsTest {

    @Test
    public void isPowerOfTwoTest(){
        assertTrue(RoundUtils.isPowerOfTwo(1));
        assertTrue(RoundUtils.isPowerOfTwo(2));
        assertTrue(RoundUtils.isPowerOfTwo(4));
        assertTrue(RoundUtils.isPowerOfTwo(8));
        assertTrue(RoundUtils.isPowerOfTwo(16));
        assertTrue(RoundUtils.isPowerOfTwo(32));
        assertTrue(RoundUtils.isPowerOfTwo(64));

        assertFalse(RoundUtils.isPowerOfTwo(56));
        assertFalse(RoundUtils.isPowerOfTwo(17));
        assertFalse(RoundUtils.isPowerOfTwo(876));
        assertFalse(RoundUtils.isPowerOfTwo(155));
        assertFalse(RoundUtils.isPowerOfTwo(5));
        assertFalse(RoundUtils.isPowerOfTwo(9874));
        assertFalse(RoundUtils.isPowerOfTwo(6));
    }

    @Test
    public void testRoundUpToPowerOfTwo(){
        assertEquals(4, RoundUtils.ceilToPowerOfTwo(3));
        assertEquals(8, RoundUtils.ceilToPowerOfTwo(5));
        assertEquals(16, RoundUtils.ceilToPowerOfTwo(13));
        assertEquals(32, RoundUtils.ceilToPowerOfTwo(25));

        assertEquals(8, RoundUtils.ceilToPowerOfTwo(8));
    }

}