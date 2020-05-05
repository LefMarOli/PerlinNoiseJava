package org.lefmaroli.vector;

public class UnitVector {

    private double x;
    private double y;

    public UnitVector(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getVectorProduct(UnitVector other){
        return (this.x * other.x) + (this.y * other.y);
    }
}
