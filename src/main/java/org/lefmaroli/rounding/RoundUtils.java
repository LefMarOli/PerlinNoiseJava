package org.lefmaroli.rounding;

import java.util.ArrayList;
import java.util.List;

public class RoundUtils {

  private RoundUtils() {}

  public static boolean isPowerOfTwo(int n) {
    return n > 0 && (n & n - 1) == 0;
  }

  public static int ceilToPowerOfTwo(int n) {
    if (isPowerOfTwo(n)) {
      return n;
    } else {
      var power = 0;
      while (n != 0) { // 000000
        n >>= 1;
        power++;
      }
      return 1 << power;
    }
  }

  public static int floorToPowerOfTwo(int n) {
    if (isPowerOfTwo(n)) {
      return n;
    } else {
      var power = 0;
      while (n != 1) { // 000001
        n >>= 1;
        power++;
      }
      return 1 << power;
    }
  }

  public static int roundNToClosestFactorOfM(int n, int m) {
    if (m <= 0) {
      throw new IllegalArgumentException("m must be greater than 0");
    }
    if (n <= 0) {
      throw new IllegalArgumentException("n must be greater than 0");
    }
    if (n > m) {
      throw new IllegalArgumentException("m must greater than n");
    }
    if (m % n == 0) {
      return n;
    } else {
      List<Integer> factorsOfM = getFactorsOf(m);
      int minDistance = m + 1;
      var closestFactor = 0;
      for (Integer factor : factorsOfM) {
        int distance = Math.abs(n - factor);
        if (distance < minDistance) {
          minDistance = distance;
          closestFactor = factor;
        }
      }
      return closestFactor;
    }
  }

  private static List<Integer> getFactorsOf(int number) {
    List<Integer> toReturn = new ArrayList<>(2);
    toReturn.add(1);
    toReturn.add(number);
    for (var i = 2; i < Math.sqrt(number); i++) {
      if (number % i == 0) {
        toReturn.add(i);
        toReturn.add(number / i);
      }
    }
    return toReturn;
  }
}
