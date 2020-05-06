package org.lefmaroli.vector;

public class Vector2D {

    private final double x;
    private final double y;

    public Vector2D(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getLength(){
        return Math.sqrt(x * x + y * y);
    }

    public double getVectorProduct(Vector2D other) {
        return (this.x * other.x) + (this.y * other.y);
    }

    public Vector2D normalize() {
        double length = getLength();
        if(length > 0.0)
            return new Vector2D(x / length, y / length);
        else{
            return this;
        }
    }

    public static double getVectorProduct(Vector2D lhs, Vector2D rhs) {
        return lhs.getVectorProduct(rhs);
    }

    @Override
    public String toString() {
        return "Vector2D{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}
