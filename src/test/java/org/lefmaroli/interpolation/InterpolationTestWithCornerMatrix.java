package org.lefmaroli.interpolation;

import static org.junit.Assert.*;

import org.junit.Test;

public class InterpolationTestWithCornerMatrix {


  @Test
  public void test1D(){
    CornerMatrix cornerMatrix = CornerMatrix.getForDimension(1);
    cornerMatrix.setValueAtIndices(0, 0);
    cornerMatrix.setValueAtIndices(1, 1);
    double[] distance = new double[1];
    distance[0] = 0.5;
    assertEquals(0.5, Interpolation.linear(cornerMatrix, distance), 1E-9);
  }

  @Test
  public void test2D(){
    CornerMatrix cornerMatrix = CornerMatrix.getForDimension(2);
    cornerMatrix.setValueAtIndices(0.0, 0, 0);
    cornerMatrix.setValueAtIndices(1.0, 0, 1);
    cornerMatrix.setValueAtIndices(1.0, 1, 0);
    cornerMatrix.setValueAtIndices(0.0, 1, 1);
    double[] distance = new double[2];
    distance[0] = 0.3;
    distance[1] = 0.3;
    assertEquals(0.42, Interpolation.linear(cornerMatrix, distance), 1E-9);
  }

  @Test
  public void test3D(){
    CornerMatrix cornerMatrix = CornerMatrix.getForDimension(3);
    double[] distances3D = new double[3];
    double value = 0.0;
    for (int i = 0; i < 2; i++) {
      for (int j = 0; j < 2; j++) {
        for (int k = 0; k < 2; k++) {
          cornerMatrix.setValueAtIndices(value, i, j, k);
          value += 1.0;
        }
      }
    }
    for (int i = 1; i < 4; i++) {
      distances3D[i - 1] = i * 0.1;
    }
    double actual = Interpolation.linear(cornerMatrix, distances3D);
    assertEquals(1.1, actual, 1E-9);
  }

  @Test
  public void test4D() {
    CornerMatrix cornerMatrix = CornerMatrix.getForDimension(4);
    double[] distances4D = new double[4];
    double value = 0.0;
    for (int i = 0; i < 2; i++) {
      for (int j = 0; j < 2; j++) {
        for (int k = 0; k < 2; k++) {
          for (int m = 0; m < 2; m++) {
            cornerMatrix.setValueAtIndices(value, i, j, k, m);
            value += 1.0;
          }
        }
      }
    }
    for (int i = 1; i < 5; i++) {
      distances4D[i - 1] = i * 0.1;
    }
    double actual = Interpolation.linear(cornerMatrix, distances4D);
    assertEquals(2.6, actual, 1E-9);
  }

}