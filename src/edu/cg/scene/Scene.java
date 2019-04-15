package edu.cg.scene;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import edu.cg.Logger;
import edu.cg.UnimplementedMethodException;
import edu.cg.algebra.*;
import edu.cg.scene.camera.PinholeCamera;
import edu.cg.scene.lightSources.Light;
import edu.cg.scene.objects.Surface;

public class Scene {
    private String name = "scene";
    private int maxRecursionLevel = 1;
    private int antiAliasingFactor = 1; //gets the values of 1, 2 and 3
    private boolean renderRefarctions = false;
    private boolean renderReflections = false;

    private PinholeCamera camera;
    private Vec ambient = new Vec(1, 1, 1); //white
    private Vec backgroundColor = new Vec(0, 0.5, 1); //blue sky
    private List<Light> lightSources = new LinkedList<>();
    private List<Surface> surfaces = new LinkedList<>();


    //MARK: initializers
    public Scene initCamera(Point eyePoistion, Vec towardsVec, Vec upVec, double distanceToPlain) {
        this.camera = new PinholeCamera(eyePoistion, towardsVec, upVec, distanceToPlain);
        return this;
    }

    public Scene initAmbient(Vec ambient) {
        this.ambient = ambient;
        return this;
    }

    public Scene initBackgroundColor(Vec backgroundColor) {
        this.backgroundColor = backgroundColor;
        return this;
    }

    public Scene addLightSource(Light lightSource) {
        lightSources.add(lightSource);
        return this;
    }

    public Scene addSurface(Surface surface) {
        surfaces.add(surface);
        return this;
    }

    public Scene initMaxRecursionLevel(int maxRecursionLevel) {
        this.maxRecursionLevel = maxRecursionLevel;
        return this;
    }

    public Scene initAntiAliasingFactor(int antiAliasingFactor) {
        this.antiAliasingFactor = antiAliasingFactor;
        return this;
    }

    public Scene initName(String name) {
        this.name = name;
        return this;
    }

    public Scene initRenderRefarctions(boolean renderRefarctions) {
        this.renderRefarctions = renderRefarctions;
        return this;
    }

    public Scene initRenderReflections(boolean renderReflections) {
        this.renderReflections = renderReflections;
        return this;
    }

    //MARK: getters
    public String getName() {
        return name;
    }

    public int getFactor() {
        return antiAliasingFactor;
    }

    public int getMaxRecursionLevel() {
        return maxRecursionLevel;
    }

    public boolean getRenderRefarctions() {
        return renderRefarctions;
    }

    public boolean getRenderReflections() {
        return renderReflections;
    }

    @Override
    public String toString() {
        String endl = System.lineSeparator();
        return "Camera: " + camera + endl +
                "Ambient: " + ambient + endl +
                "Background Color: " + backgroundColor + endl +
                "Max recursion level: " + maxRecursionLevel + endl +
                "Anti aliasing factor: " + antiAliasingFactor + endl +
                "Light sources:" + endl + lightSources + endl +
                "Surfaces:" + endl + surfaces;
    }

    private transient ExecutorService executor = null;
    private transient Logger logger = null;

    private void initSomeFields(int imgWidth, int imgHeight, Logger logger) {
        this.logger = logger;
        //TODO: initialize your additional field here.
        //      You can also change the method signature if needed.
    }


    public BufferedImage render(int imgWidth, int imgHeight, double viewPlainWidth, Logger logger)
            throws InterruptedException, ExecutionException {
        // TODO: Please notice the following comment.
        // This method is invoked each time Render Scene button is invoked.
        // Use it to initialize additional fields you need.
        initSomeFields(imgWidth, imgHeight, logger);
        BufferedImage img = new BufferedImage(imgWidth, imgHeight, BufferedImage.TYPE_INT_RGB);
        camera.initResolution(imgHeight, imgWidth, viewPlainWidth);
        int nThreads = Runtime.getRuntime().availableProcessors();
        nThreads = nThreads < 2 ? 2 : nThreads;
        this.logger.log("Intitialize executor. Using " + nThreads + " threads to render " + name);
        executor = Executors.newFixedThreadPool(nThreads);

        @SuppressWarnings("unchecked")
        Future<Color>[][] futures = (Future<Color>[][]) (new Future[imgHeight][imgWidth]);

        this.logger.log("Starting to shoot " +
                (imgHeight * imgWidth * antiAliasingFactor * antiAliasingFactor) +
                " rays over " + name);

        for (int y = 0; y < imgHeight; ++y)
            for (int x = 0; x < imgWidth; ++x)
                futures[y][x] = calcColor(x, y);

        this.logger.log("Done shooting rays.");
        this.logger.log("Wating for results...");

        for (int y = 0; y < imgHeight; ++y)
            for (int x = 0; x < imgWidth; ++x) {
                Color color = futures[y][x].get();
                img.setRGB(x, y, color.getRGB());
            }

        executor.shutdown();

        this.logger.log("Ray tracing of " + name + " has been completed.");

        executor = null;
        this.logger = null;

        return img;
    }

    private Future<Color> calcColor(int x, int y) {
        return executor.submit(() -> {
            // TODO: You need to re-implement this method if you want to handle
            //       super-sampling. You're also free to change the given implementation as you like.
            Point centerPoint = camera.transform(x, y);
            Ray ray = new Ray(camera.getCameraPosition(), centerPoint);
            Vec color = calcColor(ray, 0);
            return color.toColor();
        });
    }

    private Vec calcColor(Ray ray, int recusionLevel) {
        // TODO: Implement this method.
        //       This is the recursive method in RayTracing.
        if (recusionLevel >= maxRecursionLevel) {
            return new Vec();
        }
        Hit closetHit = findMinIntersection(ray);
        // in case no intersections was found
        if (closetHit == null) return backgroundColor;
        // get the hitting point
        Point hittingPoint = ray.getHittingPoint(closetHit);
        // get the working surface
        Surface hittingSurface = closetHit.getSurface();

        Vec color = new Vec();
        color.add(calcAmbientColor(hittingSurface));
        for (Light l : lightSources) {
            Ray rayToLight = l.rayToLight(hittingPoint);
            if (!isOccluded(l, rayToLight)) {
                color.add(calcDiffuseColor(closetHit, rayToLight).mult(l.intensity(hittingPoint, rayToLight)));
                color.add(calcSpecularColor(closetHit, rayToLight, ray).mult(l.intensity(hittingPoint, rayToLight)));
            }
        }
        if (renderReflections) {
            // TODO:
            Vec N = closetHit.getNormalToSurface();
            Vec L = ray.direction();
            // get the reflection vector direction
            Vec reflectedDirection = Ops.reflect(L, N);
            Ray reflectedRay = new Ray(hittingPoint, reflectedDirection;
            // the reflection coefficient for the material
            double K_R = hittingSurface.reflectionIntensity();
            // recursive call calcColor for the reflected ray
            Vec reflectedColor = calcColor(reflectedRay, recusionLevel + 1).mult(new Vec(K_R));
            color = color.add(reflectedColor);
        }
        if (renderRefarctions) {
            // TODO:
            Vec refractionColor = new Vec();
            if (surface.isTransparent()) {
                double n1 = surface.n1(minHit);
                double n2 = surface.n2(minHit);
                Vec refractionDirection = Ops.refract(ray.direction(), minHit.getNormalToSurface(), n1, n2);
                Vec refractionWeight = new Vec(surface.refractionIntensity());
                refractionColor = this.calcColor(new Ray(hittingPoint, refractionDirection), recusionLevel + 1).mult(refractionWeight);
                color = color.add(refractionColor);
            }
        }
        return color;
    }


    private Hit findMinIntersection(Ray ray) {
        Hit closetHit = null;
        // find the first (closet) surface intersected by the ray
        for (Surface s : surfaces) {
            // TODO: make sure to setSurface
            Hit hit = s.intersect(ray);
            closetHit = (closetHit == null) ? hit : closetHit;
            if (hit != null)
                closetHit = (hit.compareTo(closetHit) >= 0) ? closetHit : hit;
        }
        return closetHit;
    }


    /**
     * represents light emanating directly from an object
     *
     * @param hittingPoint
     * @return
     */
    private Vec calcEmissionColor(Light l, Point hittingPoint, Ray rayToLight) {
        // TODO: delete if not needed
        throw new UnimplementedMethodException("calcEmissionColor");
    }

    /**
     * @param s - the surface
     * @return I_A * K_A
     */
    private Vec calcAmbientColor(Surface s) {
        // Ka - the ambient part of the material
        return s.Ka().mult(ambient);
    }

    /**
     * @param closetHit
     * @param rayToLight
     * @return the diffuse reflection component of the light source, the amount of light that bounces
     * back | I_D = K_D * (N^ . L^) . I_L
     */
    private Vec calcDiffuseColor(Hit closetHit, Ray rayToLight) {
        Vec N = closetHit.getNormalToSurface();
        Vec L = rayToLight.direction();
        // surface’s coefficient of diffuse reflection
        Vec K_d = closetHit.getSurface().Kd();
        return K_d.mult(Math.max(N.dot(L), 0.0));
    }

    /**
     * @param closetHit
     * @param rayToLight
     * @param viewpointRay - the ray from the camera
     * @return the specular component of the light source
     */
    private Vec calcSpecularColor(Hit closetHit, Ray rayToLight, Ray viewpointRay) {
        Vec N = closetHit.getNormalToSurface();
        Vec L = rayToLight.direction();
        // the mirror reflection of the light vector off the surface
        Vec R = Ops.reflect(L.neg(), N);
        Vec V = viewpointRay.direction();
        // surface’s coefficient of specular reflection
        Vec K_s = closetHit.getSurface().Ks();
        // n defines the shininess constant for this material.
        int n = closetHit.getSurface().shininess();
        double VR = R.dot(V.neg());
        // TODO: if VR < 0.0
        return K_s.mult(Math.pow(R.dot(V.neg()), n));
    }

    /**
     * @param light
     * @param rayToLight
     * @return true if occluded
     */
    private boolean isOccluded(Light light, Ray rayToLight) {
        for (Surface surface : surfaces) {
            if (light.isOccludedBy(surface, rayToLight)) return true;
        }
        return false;
    }
}
