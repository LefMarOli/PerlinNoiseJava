package org.lefmaroli.rounding;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;

public class RoundUtilsTest {

  private final List<Integer> testList = new ArrayList<>();

  @Before
  public void setup() {
    for (int i = 0; i < 10; i++) {
      testList.add((int) Math.pow(2, i));
    }
  }

  @Test
  public void isPowerOfTwoTest() {
    for (Integer integer : testList) {
      assertTrue(RoundUtils.isPowerOfTwo(integer));
    }
  }

  @Test
  public void isPowerOfTwoTestFalse() {
    for (int i = 1; i < testList.size(); i++) {
      assertFalse(RoundUtils.isPowerOfTwo(testList.get(i) + 1));
    }
  }

  @Test
  public void isPowerOfTwoTestFalseForNegativeNumbers() {
    for (Integer integer : testList) {
      assertFalse(RoundUtils.isPowerOfTwo(-integer));
    }
  }

  @Test
  public void testRoundUpToPowerOfTwoSmallerNumber() {
    for (int i = 2; i < testList.size(); i++) {
      assertEquals(testList.get(i), RoundUtils.ceilToPowerOfTwo(testList.get(i) - 1), 0);
    }
  }

  @Test
  public void testRoundUpToPowerOfTwoEqualNumber() {
    for (Integer integer : testList) {
      assertEquals(integer, RoundUtils.ceilToPowerOfTwo(integer), 0);
    }
  }

  @Test
  public void testRoundDownToPowerOfTwoBiggerNumber() {
    for (int i = 1; i < testList.size(); i++) {
      assertEquals(testList.get(i), RoundUtils.floorToPowerOfTwo(testList.get(i) + 1), 0);
    }
  }

  @Test
  public void testRoundDownToPowerOfTwoEqualNumber() {
    for (Integer integer : testList) {
      assertEquals(integer, RoundUtils.floorToPowerOfTwo(integer), 0);
    }
  }

  @Test(expected = IllegalArgumentException.class)
  public void testIllegalArgumentForFactorRounding() {
    RoundUtils.roundNToClosestFactorOfM(5, 0);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testIllegalArgumentForFactorRounding2() {
    RoundUtils.roundNToClosestFactorOfM(5, -5);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testIllegalArgumentForFactorRounding3() {
    RoundUtils.roundNToClosestFactorOfM(0, 5);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testIllegalArgumentForFactorRounding4() {
    RoundUtils.roundNToClosestFactorOfM(-5, 5);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNLargerThanM(){
    RoundUtils.roundNToClosestFactorOfM(85, 50);
  }

  @Test
  public void testRoundToClosestFactorUnder() {
    assertEquals(24, RoundUtils.roundNToClosestFactorOfM(23, 48));
  }

  @Test
  public void testRoundToClosestFactorLower() {
    assertEquals(16, RoundUtils.roundNToClosestFactorOfM(15, 48));
  }

  @Test
  public void testRoundToClosestFactorExactly() {
    assertEquals(16, RoundUtils.roundNToClosestFactorOfM(16, 48));
  }

  @Test
  public void testRoundToClosestFactorSameDistanceDefaultsToLowest() {
    assertEquals(2, RoundUtils.roundNToClosestFactorOfM(3, 16));
  }
}
