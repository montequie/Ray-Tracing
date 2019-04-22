package edu.cg.scene.lightSources;

import edu.cg.algebra.Ops;
import edu.cg.algebra.Point;
import edu.cg.algebra.Ray;
import edu.cg.algebra.Vec;
import edu.cg.scene.objects.Surface;

public class Spotlight extends PointLight {
    private Vec direction;

    public Spotlight initDirection(Vec direction) {
        this.direction = direction;
        this.direction.normalize();
        return this;
    }

    @Override
    public String toString() {
        String endl = System.lineSeparator();
        return "Spotlight: " + endl +
                description() +
                "Direction: " + direction + endl;
    }

    @Override
    public Spotlight initPosition(Point position) {
        return (Spotlight) super.initPosition(position);
    }

    @Override
    public Spotlight initIntensity(Vec intensity) {
        return (Spotlight) super.initIntensity(intensity);
    }

    @Override
    public Spotlight initDecayFactors(double q, double l, double c) {
        return (Spotlight) super.initDecayFactors(q, l, c);
    }

    @Override
    public Vec intensity(Point hittingPoint, Ray rayToLight) {
        Vec D = direction.neg();
        Vec V = rayToLight.direction().normalize();
        double dotVD = D.dot(V);

        if (dotVD < Ops.epsilon) return new Vec(0, 0, 0);

        return super.intensity(hittingPoint, rayToLight).mult(dotVD);
    }

    @Override
    public boolean isOccludedBy(Surface surface, Ray rayToLight) {
        Vec V = rayToLight.direction().normalize().neg();
        Vec D = direction;
        if (V.dot(D) < Ops.epsilon) return true;
        return super.isOccludedBy(surface, rayToLight);
    }
}
