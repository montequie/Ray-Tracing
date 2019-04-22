package edu.cg.scene.lightSources;

import edu.cg.algebra.Hit;
import edu.cg.algebra.Point;
import edu.cg.algebra.Ray;
import edu.cg.algebra.Vec;
import edu.cg.scene.objects.Surface;

public class PointLight extends Light {
    protected Point position;

    //Decay factors:
    protected double kq = 0.01;
    protected double kl = 0.1;
    protected double kc = 1;

    protected String description() {
        String endl = System.lineSeparator();
        return "Intensity: " + intensity + endl +
                "Position: " + position + endl +
                "Decay factors: kq = " + kq + ", kl = " + kl + ", kc = " + kc + endl;
    }

    @Override
    public String toString() {
        String endl = System.lineSeparator();
        return "Point Light:" + endl + description();
    }

    @Override
    public PointLight initIntensity(Vec intensity) {
        return (PointLight) super.initIntensity(intensity);
    }

    @Override
    public Ray rayToLight(Point fromPoint) {
        return new Ray(fromPoint, position);
    }

    @Override
    public boolean isOccludedBy(Surface surface, Ray rayToLight) {
        Hit hit = surface.intersect(rayToLight);
        // in case there is no intersection
        if (hit == null) return false;
        Point hittingPoint = rayToLight.getHittingPoint(hit);
        Point sourceOfRayToLight = rayToLight.source();
        // check if the surface is behind the PointLight
        return sourceOfRayToLight.distSqr(position) > sourceOfRayToLight.distSqr(hittingPoint);
    }

    @Override
    public Vec intensity(Point hittingPoint, Ray rayToLight) {
        double d = hittingPoint.dist(position);
        double denominator = kc + kl * d + kq * Math.pow(d, 2);
        return intensity.mult(1.0 / denominator);
    }

    public PointLight initPosition(Point position) {
        this.position = position;
        return this;
    }

    public PointLight initDecayFactors(double kq, double kl, double kc) {
        this.kq = kq;
        this.kl = kl;
        this.kc = kc;
        return this;
    }

}
