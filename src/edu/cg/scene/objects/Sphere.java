package edu.cg.scene.objects;

import edu.cg.UnimplementedMethodException;
import edu.cg.algebra.*;

public class Sphere extends Shape {
    private Point center;
    private double radius;

    public Sphere(Point center, double radius) {
        this.center = center;
        this.radius = radius;
    }

    public Sphere() {
        this(new Point(0, -0.5, -6), 0.5);
    }

    @Override
    public String toString() {
        String endl = System.lineSeparator();
        return "Sphere:" + endl + "Center: " + center + endl + "Radius: " + radius + endl;
    }

    public Sphere initCenter(Point center) {
        this.center = center;
        return this;
    }

    public Sphere initRadius(double radius) {
        this.radius = radius;
        return this;
    }

    @Override
    public Hit intersect(Ray ray) {
        double b = 2.0 * ray.direction().dot(ray.source().sub(center)); //TODO check 2.0 *
        double c = ray.source().distSqr(center) - Math.pow(radius, 2.0);
        double sqr = Math.pow(b, 2.0) - 4 * c;

        // There are no roots, the ray does not intersect the sphere
        if (sqr < 0) return null;

        double t1 = (-b + Math.sqrt(sqr)) / 2.0;
        double t2 = (-b - Math.sqrt(sqr)) / 2.0;

        // Since Math.sqrt(sqr) > 0 then if t1 < epsilon then t2 is < epsilon
        if (t1 < Ops.epsilon) return null;

        Vec normalToSurface;
        Boolean isWithin; //Todo Check if needed

        // If t2 is negative chose t1, otherwise t2 is the minimum
        if (t2 < Ops.epsilon) {
            normalToSurface = ray.add(t1).sub(center).normalize();
            return new Hit(t1, normalToSurface);
        } else {
            normalToSurface = ray.add(t2).sub(center).normalize();
            return new Hit(t2, normalToSurface);
        }
    }
}
