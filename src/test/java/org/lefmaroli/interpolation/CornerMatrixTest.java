package org.lefmaroli.interpolation;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class CornerMatrixTest {

  @Test
  void testToString() {
    CornerMatrix matrix = CornerMatrixFactory.getForDimension(2);
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
    Assertions.assertEquals(expected, matrix.toString());
  }

  @Test
  void cantGetSubMatrixForDimension1() {
    CornerMatrix cornerMatrix = CornerMatrixFactory.getForDimension(1);
    Assertions.assertThrows(IllegalArgumentException.class, () -> cornerMatrix.getSubMatrix(0));
  }

  @Test
  void testGetAtIndices() {
    CornerMatrix matrix = CornerMatrixFactory.getForDimension(2);
    matrix.setValueAtIndices(0.0, 0, 0);
    matrix.setValueAtIndices(1.0, 0, 1);
    matrix.setValueAtIndices(2.0, 1, 0);
    matrix.setValueAtIndices(3.0, 1, 1);

    Assertions.assertEquals(0.0, matrix.get(0, 0), 1E-9);
    Assertions.assertEquals(1.0, matrix.get(0, 1), 1E-9);
    Assertions.assertEquals(2.0, matrix.get(1, 0), 1E-9);
    Assertions.assertEquals(3.0, matrix.get(1, 1), 1E-9);
  }
}
