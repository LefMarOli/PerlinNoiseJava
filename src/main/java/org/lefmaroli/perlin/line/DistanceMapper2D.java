package org.lefmaroli.perlin.line;

import org.lefmaroli.vector.Vector2D;

import java.util.HashMap;
import java.util.Map;

public class DistanceMapper2D {
    private final int xLength;
    private final int yLength;

    private final Map<Integer, Map<Integer, VectorDistances>> boundDistances;

    DistanceMapper2D(int xLength, int yLength) {
        if(xLength <= 0) {
            throw new IllegalArgumentException("X length must be greater than 0.");
        }
        if(yLength <= 0){
            throw new IllegalArgumentException("Y length must be greater than 0.");
        }
        this.xLength = xLength;
        this.yLength = yLength;
        boundDistances = new HashMap<>(xLength);
        initializeBoundDistancesMap();
    }

    VectorDistances getForCoordinates(int x, int y) {
        validateXYRange(x, y);
        return boundDistances.get(x).get(y);
    }

    private void initializeBoundDistancesMap() {
        for (int j = 0; j < xLength; j++) {
            boundDistances.put(j, new HashMap<>(yLength));
        }
        mapDistances();
    }

    private void mapDistances() {
        for (int y = 0; y < yLength; y++) {
            double yDist = (double) (y) / (yLength);
            for (int x = 0; x < xLength; x++) {
                double xDist = (double) (x) / (xLength);
                Vector2D topLeftDist = new Vector2D(xDist, yDist);
                Vector2D topRightDist = new Vector2D(xDist - 1.0, yDist);
                Vector2D bottomLeftDist = new Vector2D(xDist, yDist - 1.0);
                Vector2D bottomRightDist = new Vector2D(xDist - 1.0, yDist - 1.0);
                VectorDistances distances =
                        new VectorDistances(topLeftDist, topRightDist, bottomLeftDist, bottomRightDist);
                boundDistances.get(x).put(y, distances);
            }
        }
    }

    private void validateXYRange(int x, int y) {
        if (x < 0 || x >= xLength) {
            throw new IllegalArgumentException("x is out of range, expected [0," + xLength + "[");
        }
        if (y < 0 || y >= yLength) {
            throw new IllegalArgumentException("y is out of range, expected [0," + yLength + "[");
        }
    }

    public static class VectorDistances {
        private final Vector2D topLeftDistance;
        private final Vector2D topRightDistance;
        private final Vector2D bottomLeftDistance;
        private final Vector2D bottomRightDistance;

        public VectorDistances(Vector2D topLeftDistance, Vector2D topRightDistance, Vector2D bottomLeftDistance,
                               Vector2D bottomRightDistance) {
            this.topLeftDistance = topLeftDistance;
            this.topRightDistance = topRightDistance;
            this.bottomLeftDistance = bottomLeftDistance;
            this.bottomRightDistance = bottomRightDistance;
        }

        public Vector2D getTopLeftDistance() {
            return topLeftDistance;
        }

        public Vector2D getTopRightDistance() {
            return topRightDistance;
        }

        public Vector2D getBottomLeftDistance() {
            return bottomLeftDistance;
        }

        public Vector2D getBottomRightDistance() {
            return bottomRightDistance;
        }
    }

}
