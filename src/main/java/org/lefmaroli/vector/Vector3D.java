package org.lefmaroli.vector;

public class Vector3D {

    private final double x;
    private final double y;
    private final double z;

    public Vector3D(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public static double getVectorProduct(Vector3D lhs, Vector3D rhs) {
        return lhs.getVectorProduct(rhs);
    }

    public double getLength() {
        return Math.sqrt(x * x + y * y + z * z);
    }

    public double getVectorProduct(Vector3D other) {
        return (this.x * other.x) + (this.y * other.y) + (this.z * other.z);
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ(){
        return z;
    }

    public Vector3D normalize() {
        double length = getLength();
        if (length > 0.0) {
            return new Vector3D(x / length, y / length, z / length);
        } else {
            return this;
        }
    }

    @Override
    public String toString() {
        return "Vector3D{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                '}';
    }
}
