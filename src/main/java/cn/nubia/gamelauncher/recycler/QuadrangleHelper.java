package cn.nubia.gamelauncher.recycler;

import android.graphics.Point;

public class QuadrangleHelper {
    public static boolean isPointInQuadrangle(Point a, Point b, Point c, Point d, Point p) {
        return ((triangleArea(a, b, p) + triangleArea(b, c, p)) + triangleArea(c, d, p)) + triangleArea(d, a, p) == triangleArea(a, b, c) + triangleArea(c, d, a);
    }

    public static boolean isPointInQuadrangle(Point[] quadrangle, Point p) {
        if (quadrangle == null || quadrangle.length < 4) {
            return false;
        }
        return isPointInQuadrangle(quadrangle[0], quadrangle[1], quadrangle[2], quadrangle[3], p);
    }

    private static double triangleArea(Point a, Point b, Point c) {
        return Math.abs(((double) ((((((a.x * b.y) + (b.x * c.y)) + (c.x * a.y)) - (b.x * a.y)) - (c.x * b.y)) - (a.x * c.y))) / 2.0d);
    }
}
