package org.lefmaroli.utils;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

public class AssertUtils {

  public static void valuesContinuousInArray(double[] array) {
    int windowWidth = 2;
    int[] signs = new int[array.length - 1];
    for (int i = 1; i < array.length; i++) {
      if (Math.abs(array[i] - array[i - 1]) < 1E-4) {
        signs[i - 1] = 0;
      } else signs[i - 1] = array[i] - array[i - 1] > 0 ? 1 : -1;
    }

    for (int i = windowWidth; i < signs.length - windowWidth; i++) {
      int ref = signs[i - windowWidth];
      if (ref == 0) {
        int next = i - windowWidth + 1;
        while (signs[next] == 0 && next < i + windowWidth) {
          next++;
        }
        if (signs[next] == 0) {
          continue;
        } else {
          ref = signs[next];
        }
      }
      int transitionPoint = -1;
      for (int j = -windowWidth + 1; j < windowWidth; j++) {
        if (signs[i + j] != 0 && signs[i + j] != ref) {
          transitionPoint = i + j;
          break;
        }
      }
      if (transitionPoint != -1) {
        double[] subArray = Arrays.copyOfRange(array, i - windowWidth, i + windowWidth + 1);
        for (int j = i - windowWidth + 1; j < transitionPoint; j++) {
          if (signs[j] != 0) {
            assertEquals(
                "Array does not have continuous values:"
                    + Arrays.toString(subArray)
                    + " around i="
                    + i,
                ref,
                signs[j]);
          }
        }
        for (int j = transitionPoint + 1; j < i + windowWidth; j++) {
          if (signs[j] != 0) {
            assertEquals(
                "Array does not have continuous values:"
                    + Arrays.toString(subArray)
                    + " around i="
                    + i,
                signs[transitionPoint],
                signs[j]);
          }
        }
      }
    }
  }
}
