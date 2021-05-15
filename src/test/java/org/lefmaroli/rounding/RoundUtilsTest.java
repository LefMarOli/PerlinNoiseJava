package org.lefmaroli.rounding;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class RoundUtilsTest {

  private static final int TOTAL_NUMBERS = 10;
  private final List<Integer> testList = new ArrayList<>(TOTAL_NUMBERS);

  @BeforeEach
  void setup() {
    for (int i = 0; i < TOTAL_NUMBERS; i++) {
      testList.add((int) Math.pow(2, i));
    }
  }

  @Test
  void isPowerOfTwoTest() {
    for (Integer integer : testList) {
      Assertions.assertTrue(RoundUtils.isPowerOfTwo(integer));
    }
  }

  @Test
  void isPowerOfTwoTestFalse() {
    for (int i = 1; i < testList.size(); i++) {
      Assertions.assertFalse(RoundUtils.isPowerOfTwo(testList.get(i) + 1));
    }
  }

  @Test
  void isPowerOfTwoTestFalseForNegativeNumbers() {
    for (Integer integer : testList) {
      Assertions.assertFalse(RoundUtils.isPowerOfTwo(-integer));
    }
  }

  @Test
  void testRoundUpToPowerOfTwoSmallerNumber() {
    for (int i = 2; i < testList.size(); i++) {
      Assertions.assertEquals(testList.get(i), RoundUtils.ceilToPowerOfTwo(testList.get(i) - 1), 0);
    }
  }

  @Test
  void testRoundUpToPowerOfTwoEqualNumber() {
    for (Integer integer : testList) {
      Assertions.assertEquals(integer, RoundUtils.ceilToPowerOfTwo(integer), 0);
    }
  }

  @Test
  void testRoundDownToPowerOfTwoBiggerNumber() {
    for (int i = 1; i < testList.size(); i++) {
      Assertions.assertEquals(testList.get(i), RoundUtils.floorToPowerOfTwo(testList.get(i) + 1), 0);
    }
  }

  @Test
  void testRoundDownToPowerOfTwoEqualNumber() {
    for (Integer integer : testList) {
      Assertions.assertEquals(integer, RoundUtils.floorToPowerOfTwo(integer), 0);
    }
  }

  @ParameterizedTest(name = "{index} n:{0}, m:{1}")
  @MethodSource("provideParameters")
  void testIllegalArgumentForFactorRounding(int n, int m) {
    Assertions.assertThrows(
        IllegalArgumentException.class, () -> RoundUtils.roundNToClosestFactorOfM(n, m));
  }

  @SuppressWarnings("unused")
  private static Stream<Arguments> provideParameters() {
    return Stream.of(
        Arguments.of(5, 0),
        Arguments.of(5, -5),
        Arguments.of(0, 5),
        Arguments.of(-5, 5),
        Arguments.of(85, 50));
  }

  @Test
  void testRoundToClosestFactorUnder() {
    Assertions.assertEquals(24, RoundUtils.roundNToClosestFactorOfM(23, 48));
  }

  @Test
  void testRoundToClosestFactorLower() {
    Assertions.assertEquals(16, RoundUtils.roundNToClosestFactorOfM(15, 48));
  }

  @Test
  void testRoundToClosestFactorExactly() {
    Assertions.assertEquals(16, RoundUtils.roundNToClosestFactorOfM(16, 48));
  }

  @Test
  void testRoundToClosestFactorSameDistanceDefaultsToLowest() {
    Assertions.assertEquals(2, RoundUtils.roundNToClosestFactorOfM(3, 16));
  }
}
