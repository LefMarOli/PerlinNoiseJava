package org.lefmaroli.vector;

import static org.junit.Assert.assertEquals;

import com.jparams.verifier.tostring.NameStyle;
import com.jparams.verifier.tostring.ToStringVerifier;
import com.jparams.verifier.tostring.preset.Presets;
import org.junit.Test;

public class Vector3DTest {

  @Test
  public void testToString() {
    ToStringVerifier.forClass(Vector3D.class)
        .withClassName(NameStyle.SIMPLE_NAME)
        .withPreset(Presets.INTELLI_J)
        .verify();
  }

  @Test
  public void testNormalize() {
    Vector3D vector3D = new Vector3D(2.0, 5.0, 9.0);
    Vector3D normalized = vector3D.normalize();
    assertEquals(1.0, normalized.getLength(), 1E-9);
  }

  @Test
  public void testNormalize0Length() {
    Vector3D zeroLength = new Vector3D(0.0, 0.0, 0.0);
    Vector3D normalized = zeroLength.normalize();
    assertEquals(0.0, normalized.getLength(), 1E-9);
  }

  @Test
  public void testGetLength() {
    double x = 5.0;
    double y = 4.0;
    double z = 8.0;
    double expected = Math.sqrt(x * x + y * y + z * z);
    assertEquals(expected, new Vector3D(x, y, z).getLength(), 1E-9);
  }
}
