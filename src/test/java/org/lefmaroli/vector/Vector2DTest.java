package org.lefmaroli.vector;

import static org.junit.Assert.assertEquals;

import com.jparams.verifier.tostring.NameStyle;
import com.jparams.verifier.tostring.ToStringVerifier;
import com.jparams.verifier.tostring.preset.Presets;
import org.junit.Test;

public class Vector2DTest {

  @Test
  public void vectorProductOrthogonalVectorsTest() {
    Vector2D first = new Vector2D(1.0, 0.0);
    Vector2D second = new Vector2D(0.0, 1.0);
    assertVectorProductEqualSymmetrically(first, second, 0.0, 0.0);
  }

  @Test
  public void testGetVectorProductOppositeDirectionVectors() {
    Vector2D first = new Vector2D(1.0, 0.0);
    Vector2D second = new Vector2D(-1.0, 0.0);
    assertVectorProductEqualSymmetrically(first, second, -1.0, 0.0);
  }

  @Test
  public void testGetVectorProductSameDirectionVectors() {
    Vector2D first = new Vector2D(1.0, 0.0);
    Vector2D second = new Vector2D(1.0, 0.0);
    assertVectorProductEqualSymmetrically(first, second, 1.0, 0.0);
  }

  @Test
  public void testNormalize() {
    Vector2D notNormalized = new Vector2D(2.0, 1.5);
    Vector2D normalized = notNormalized.normalize();
    assertEquals(1.0, normalized.getLength(), 1E-18);
  }

  @Test
  public void testNormalizeLength() {
    Vector2D notNormalized = new Vector2D(0, 0);
    Vector2D normalized = notNormalized.normalize();
    assertEquals(0.0, normalized.getLength(), 1E-18);
  }

  @Test
  public void testLength() {
    assertEquals(1.0, new Vector2D(1.0, 0.0).getLength(), 0.0);
    assertEquals(1.0, new Vector2D(-1.0, 0.0).getLength(), 0.0);
    assertEquals(1.0, new Vector2D(0.0, 1.0).getLength(), 0.0);
    assertEquals(1.0, new Vector2D(0.0, -1.0).getLength(), 0.0);
    assertEquals(Math.sqrt(2.0), new Vector2D(1.0, 1.0).getLength(), 1E-18);
    assertEquals(Math.sqrt(2.0), new Vector2D(-1.0, -1.0).getLength(), 1E-18);
  }

  @Test
  public void testToString() {
    ToStringVerifier.forClass(Vector2D.class)
        .withClassName(NameStyle.SIMPLE_NAME)
        .withPreset(Presets.INTELLI_J)
        .verify();
  }

  private void assertVectorProductEqualSymmetrically(
      Vector2D first, Vector2D second, double expectedValue, double delta) {
    assertEquals(expectedValue, first.getVectorProduct(second), delta);
    assertEquals(expectedValue, second.getVectorProduct(first), delta);
    assertEquals(expectedValue, Vector2D.getVectorProduct(first, second), delta);
    assertEquals(expectedValue, Vector2D.getVectorProduct(second, first), delta);
  }
}
