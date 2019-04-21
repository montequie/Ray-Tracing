package edu.cg.scene.camera;

import edu.cg.UnimplementedMethodException;
import edu.cg.algebra.Point;
import edu.cg.algebra.Ray;
import edu.cg.algebra.Vec;

public class PinholeCamera {
	private Point cameraPosition;
	private Point center;
	private Vec towardsVec;
	private Vec rightVec;
    private Vec upVec;
    private double distanceToPlain;
    private int height;
    private int width;
    private double viewPlainWidth;
	
	/**
	 * Initializes a pinhole camera model with default resolution 200X200 (RxXRy) and image width 2.
	 * @param cameraPosition - The position of the camera.
	 * @param towardsVec - The towards vector of the camera (not necessarily normalized).
	 * @param upVec - The up vector of the camera.
	 * @param distanceToPlain - The distance of the camera (position) to the center point of the image-plain.
	 *
	 */
	public PinholeCamera(Point cameraPosition, Vec towardsVec, Vec upVec, double distanceToPlain) {
		this.cameraPosition = cameraPosition;
		this.towardsVec = towardsVec.normalize();
		this.rightVec = towardsVec.cross(upVec).normalize();
		this.upVec = rightVec.cross(towardsVec).normalize();
		this.distanceToPlain = distanceToPlain;
		this.center = new Ray(cameraPosition, towardsVec).add(distanceToPlain);
	}
	/**
	 * Initializes the resolution and width of the image.
	 * @param height - the number of pixels in the y direction.
	 * @param width - the number of pixels in the x direction.
	 * @param viewPlainWidth - the width of the image plain in world coordinates.
	 */
	public void initResolution(int height, int width, double viewPlainWidth) {
		this.height = height;
		this.width = width;
		this.viewPlainWidth = viewPlainWidth;
	}

	/**
	 * Transforms from pixel coordinates to the center point of the corresponding pixel in model coordinates.
	 * @param x - the index of the x direction of the pixel.
	 * @param y - the index of the y direction of the pixel.
	 * @return the middle point of the pixel (x,y) in the model coordinates.
	 */
	public Point transform(int x, int y) {

	    double ratio = viewPlainWidth/width;
	    double rightCalc = (double) (x - width/2) * ratio;
	    double upCalc = (double) (y - height/2) * -ratio;
        Vec up = upVec.mult(upCalc);
        Vec right = rightVec.mult(rightCalc);
        this.center = new Ray(cameraPosition, towardsVec).add(distanceToPlain);
        return center.add(up).add(right);
	}
	
	/**
	 * Returns a copy of the camera position
	 * @return a "new" point representing the camera position.
	 */
	public Point getCameraPosition() {
		return new Point(cameraPosition.x, cameraPosition.y, cameraPosition.z);
	}
}
