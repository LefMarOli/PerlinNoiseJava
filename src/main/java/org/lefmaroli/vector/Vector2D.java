package org.lefmaroli.vector;

public class Vector2D {

    private final double x;
    private final double y;

    public Vector2D(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getVectorProduct(Vector2D other){
        return (this.x * other.x) + (this.y * other.y);
    }

    public static double getVectorProduct(Vector2D lhs, Vector2D rhs){
        return lhs.getVectorProduct(rhs);
    }
}
