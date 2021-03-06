package edu.cg.scene.objects;

import edu.cg.algebra.*;

public class AxisAlignedBox extends Shape {
    private Point minPoint;
    private Point maxPoint;
    private String name = "";
    static private int CURR_IDX;

    /**
     * Creates an axis aligned box with a specified minPoint and maxPoint.
     */
    public AxisAlignedBox(Point minPoint, Point maxPoint) {
        this.minPoint = minPoint;
        this.maxPoint = maxPoint;
        name = new String("Box " + CURR_IDX);
        CURR_IDX += 1;
        fixBoundryPoints();
    }

    /**
     * Creates a default axis aligned box with a specified minPoint and maxPoint.
     */
    public AxisAlignedBox() {
        minPoint = new Point(-1.0, -1.0, -1.0);
        maxPoint = new Point(1.0, 1.0, 1.0);
    }

    /**
     * This methods fixes the boundary points minPoint and maxPoint so that the values are consistent.
     */
    private void fixBoundryPoints() {
        double min_x = Math.min(minPoint.x, maxPoint.x), max_x = Math.max(minPoint.x, maxPoint.x),
                min_y = Math.min(minPoint.y, maxPoint.y), max_y = Math.max(minPoint.y, maxPoint.y),
                min_z = Math.min(minPoint.z, maxPoint.z), max_z = Math.max(minPoint.z, maxPoint.z);
        minPoint = new Point(min_x, min_y, min_z);
        maxPoint = new Point(max_x, max_y, max_z);
    }

    @Override
    public String toString() {
        String endl = System.lineSeparator();
        return name + endl + "Min Point: " + minPoint + endl + "Max Point: " + maxPoint + endl;
    }

    //Initializers
    public AxisAlignedBox initMinPoint(Point minPoint) {
        this.minPoint = minPoint;
        fixBoundryPoints();
        return this;
    }

    public AxisAlignedBox initMaxPoint(Point maxPoint) {
        this.maxPoint = maxPoint;
        fixBoundryPoints();
        return this;
    }

    @Override
    public Hit intersect(Ray ray) {

        IntersectingT t = new IntersectingT(ray.source(), ray.direction());
        Double minT = t.getMinT();
        if (minT.isNaN()) return null;

        boolean isWithin = t.getIsWithin();
        Vec normal = t.getNormal();
        if (isWithin) {
            normal = normal.neg();
        }

        return new Hit(minT, normal).setIsWithin(isWithin);
    }

    /**
     * Class that receives a point and a vector and returns the  vectors intersecting parameter t
     * with an axis aligned box
     */
    public class IntersectingT {
        private Point start;
        private Vec dir;
        private double minT;
        private double maxT;
        private boolean isWithin;

        public IntersectingT(Point start, Vec dir) {
            this.start = start;
            this.dir = dir;
            minT = calcMinT();
            isWithin = checkIsWithin();
        }

        public double calcMinT() {
            double[] startPoint = start.asArray();
            double[] direction = dir.asArray();
            double[] minPointArr = minPoint.asArray();
            double[] maxPointArr = maxPoint.asArray();
            minT = -Ops.infinity;
            maxT = Ops.infinity;

            for (int i = 0; i < 3; i++) {
                if (Math.abs(direction[i]) <= Ops.epsilon) {
                    if (minPointArr[i] > startPoint[i] || startPoint[i] > maxPointArr[i]) {
                        return Double.NaN;
                    }
                }

                double tempT1 = calcT(minPointArr[i], startPoint[i], direction[i]);
                double tempT2 = calcT(maxPointArr[i], startPoint[i], direction[i]);
                double tempMin = Math.min(tempT1, tempT2);
                double tempMax = Math.max(tempT1, tempT2);

                if (tempMin > minT) {
                    minT = tempMin;
                }

                if (tempMax < maxT) {
                    maxT = tempMax;
                }

                if ((minT > maxT) || (maxT < Ops.epsilon)) return Double.NaN;

            }

            return minT;
        }

        /**
         * @return - minT, the intersecting parameter with the box
         */
        public double getMinT() {
            return minT;
        }

        /**
         * @return true if the point is within the box
         */
        public boolean getIsWithin() {
            return isWithin;
        }

        /**
         * Checks which face of the box is intersected and returns the normal to the face
         *
         * @return
         */
        public Vec getNormal() {
            Point p = start.add(minT, dir);
            Vec norm = null;

            if (Math.abs(p.z - minPoint.z) < Ops.epsilon) {
                norm = new Vec(0.0, 0.0, -1.0);
            } else if (Math.abs(p.z - maxPoint.z) < Ops.epsilon) {
                norm = new Vec(0.0, 0.0, 1.0);
            } else if (Math.abs(p.y - minPoint.y) < Ops.epsilon) {
                norm = new Vec(0.0, -1.0, 0.0);
            } else if (Math.abs(p.y - maxPoint.y) < Ops.epsilon) {
                norm = new Vec(0.0, 1.0, 0.0);
            } else if (Math.abs(p.x - minPoint.x) < Ops.epsilon) {
                norm = new Vec(-1.0, 0.0, 0.0);
            } else if (Math.abs(p.x - maxPoint.x) < Ops.epsilon) {
                norm = new Vec(1.0, 0.0, 0.0);
            }

            return norm;
        }

        /**
         * Checks if the point lies within the box, if so replaces minT with maxT
         *
         * @return true if the point is within the box
         */
        private boolean checkIsWithin() {
            if (minT < Ops.epsilon) {
                minT = maxT;
                isWithin = true;
            }

            return isWithin;
        }

        private double calcT(double box, double start, double direction) {
            return (box - start) / direction;
        }
    }
}
