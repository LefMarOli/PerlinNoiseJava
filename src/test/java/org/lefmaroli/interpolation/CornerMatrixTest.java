package org.lefmaroli.interpolation;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class CornerMatrixTest {

  @Test
  public void testToString() {
    CornerMatrix matrix = CornerMatrix.getForDimension(2);
    matrix.setValueAtIndices(0.0, 0, 0);
    matrix.setValueAtIndices(1.0, 0, 1);
    matrix.setValueAtIndices(2.0, 1, 0);
    matrix.setValueAtIndices(3.0, 1, 1);
    String expected = "CornerMatrix{" + System.lineSeparator();
    expected += "[0][0] = " + 0.0 + System.lineSeparator();
    expected += "[0][1] = " + 1.0 + System.lineSeparator();
    expected += "[1][0] = " + 2.0 + System.lineSeparator();
    expected += "[1][1] = " + 3.0;
    expected += "}";
    assertEquals(expected, matrix.toString());
  }

  @Test(expected = IllegalArgumentException.class)
  public void cantGetSubMatrixForDimension1() {
    CornerMatrix.getForDimension(1).getSubMatrix(0);
  }

  @Test
  public void testGetAtIndices() {
    CornerMatrix matrix = CornerMatrix.getForDimension(2);
    matrix.setValueAtIndices(0.0, 0, 0);
    matrix.setValueAtIndices(1.0, 0, 1);
    matrix.setValueAtIndices(2.0, 1, 0);
    matrix.setValueAtIndices(3.0, 1, 1);

    assertEquals(0.0, matrix.get(0, 0), 1E-9);
    assertEquals(1.0, matrix.get(0, 1), 1E-9);
    assertEquals(2.0, matrix.get(1, 0), 1E-9);
    assertEquals(3.0, matrix.get(1, 1), 1E-9);
  }
}
