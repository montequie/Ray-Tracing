package edu.cg;

import edu.cg.algebra.Point;
import edu.cg.algebra.Vec;
import edu.cg.scene.Scene;
import edu.cg.scene.lightSources.DirectionalLight;
import edu.cg.scene.lightSources.Light;
import edu.cg.scene.lightSources.PointLight;
import edu.cg.scene.lightSources.Spotlight;
import edu.cg.scene.objects.AxisAlignedBox;
import edu.cg.scene.objects.Material;
import edu.cg.scene.objects.Plain;
import edu.cg.scene.objects.Shape;
import edu.cg.scene.objects.Sphere;
import edu.cg.scene.objects.Surface;

public class Scenes {
	public static Scene scene1() {
		Shape plainShape = new Plain(new Vec(0.0,0.0,1.0), new Point(0.0,0.0,0.0));
		Material plainMaterial = Material.getMetalMaterial();
		Surface plainSurface = new Surface(plainShape,plainMaterial);
		Light dirLight = new DirectionalLight().initDirection(new Vec(0.0, 0.0, -1.0))
				.initIntensity(new Vec(0.5));
		Light pointLight = new PointLight().initIntensity(new Vec(0.9)).
				initPosition(new Point(2.5, 0.0, 1.0)).initDecayFactors(0.1, 0.05, 1.0);
		Light spotLight = new Spotlight().initIntensity(new Vec(0.9)).
				initPosition(new Point(-2.5, 0.0, 1.0)).initDecayFactors(0.1, 0.05, 1.0)
				.initDirection(new Vec(-0.1,-0.1,-0.1));
		
		return new Scene().initAmbient(new Vec(0.0))
				.initCamera(new Point(0.0, 6.0, 1.0), new Vec(0.0, -0.8, -0.2), new Vec(0, -0.5, 0.5), 2.0)
				.addLightSource(dirLight).addLightSource(pointLight).addLightSource(spotLight)
				.addSurface(plainSurface).initName("scene1").initAntiAliasingFactor(1)
				.initRenderRefarctions(true).initRenderReflections(true).initMaxRecursionLevel(3);
	}
	
	public static Scene scene2() {
		Shape boxShape1 = new AxisAlignedBox(new Point(0, 0, 0), new Point(1, 1, 1));
		// Shape boxShape1 = new Sphere(new Point(0,0,0),0.5);
		Material boxMat1 = new Material().initKa(new Vec(0.8,0.05,0.05)).initKd(new Vec(0.0))
				.initKs(new Vec(0.9)).initShininess(10)
				.initIsTransparent(false).initRefractionIntensity(0.0);
		Surface boxSurface1 = new Surface(boxShape1, boxMat1);

		Light dirLight = new DirectionalLight().initDirection(new Vec(-1.0, -1.0, -1.0))
				.initIntensity(new Vec(0.9));
		Light pointLight1 = new PointLight().initIntensity(new Vec(0.7)).
				initPosition(new Point(2, 2, 1.5));
		Light pointLight2 = new PointLight().initIntensity(new Vec(0.7)).
				initPosition(new Point(2, 1, 0.5));

		return new Scene().initAmbient(new Vec(1.0))
				.initCamera(new Point(4, 4, 1.5), new Vec(-1.0, -1.0, -0.3), new Vec(0, 0, 1), 3)
				.addLightSource(dirLight).addLightSource(pointLight1).addLightSource(pointLight2)
				.addSurface(boxSurface1).initName("scene2").initAntiAliasingFactor(1)
				.initRenderRefarctions(true).initRenderReflections(true).initMaxRecursionLevel(3);
	}

	public static Scene scene3() {
		Shape boxShape1 = new AxisAlignedBox(new Point(-0.5, 0.0, 0), new Point(0.5, 1.0, 1.0));
		Material boxMat1 = new Material().initKa(new Vec(0.01)).initKd(new Vec(0.03)).initKs(new Vec(0.03))
				.initIsTransparent(true).initRefractionIntensity(1.0);
		Surface boxSurface1 = new Surface(boxShape1, boxMat1);

		Shape boxShape2 = new AxisAlignedBox(new Point(0.0, 0.0, 0.0), new Point(1.0, -1.0, 1.0));
		Material boxMat2 = new Material().initKa(new Vec(0.5, 0.0, 0.0)).initKd(new Vec(0.1)).initKs(new Vec(0.1))
				.initIsTransparent(true).initRefractionIntensity(0.1);
		Surface boxSurface2 = new Surface(boxShape2, boxMat2);

		Shape boxShape3 = new AxisAlignedBox(new Point(0.0, 0.0, 0), new Point(-1, -1, 1));
		Material boxMat3 = new Material().initKa(new Vec(0.0, 0.0, 0.5)).initKd(new Vec(0.1)).initKs(new Vec(0.1))
				.initIsTransparent(true).initRefractionIntensity(0.1);
		Surface boxSurface3 = new Surface(boxShape3, boxMat3);

		Light dirLight = new DirectionalLight().initDirection(new Vec(0.0, -1.0, -1.0))
				.initIntensity(new Vec(0.7, 0.5, 0));

		return new Scene().initAmbient(new Vec(1.0))
				.initCamera(new Point(0.0, 6.0, 0.8), new Vec(0.0, -1.0, 0.0), new Vec(0, 0, -0.2), 3)
				.addLightSource(dirLight).addSurface(boxSurface1).addSurface(boxSurface2).addSurface(boxSurface3)
				.initName("scene3").initAntiAliasingFactor(1).initRenderRefarctions(true).initRenderReflections(true)
				.initMaxRecursionLevel(6);
	}

	public static Scene scene4() {
		int pyramidHeight = 4, boxHeight=1,boxWidth=1,boxDepth=1;
		Scene pyramidScence = new Scene();
		pyramidScence.initName("Scene4");
		pyramidScence.initAmbient(new Vec(0.33));
		pyramidScence.initRenderRefarctions(true).initRenderReflections(true).initMaxRecursionLevel(6);
		// Init camera position and setup
		Point cameraPosition = new Point(2*boxWidth*Math.pow(pyramidHeight+1,2),2*boxDepth*Math.pow(pyramidHeight+1,2),boxHeight*pyramidHeight);
		Vec towardsVec = new Vec(-1.0, -1.0, 0.0);
		Vec upVec = new Vec(0.0, 0.0, 1.0);
		double distanceFromPlain = 0.25*cameraPosition.dist(new Point(0.0,0.0,0.0));
		pyramidScence.initCamera(cameraPosition,towardsVec, upVec, distanceFromPlain);
		// Add some light sources to the Scene
		Light dirLight = new DirectionalLight().initDirection(new Vec(-0.5, -0.5, -1.0))
				.initIntensity(new Vec(0.5));
		PointLight pointLight1 = new PointLight().initPosition(new Point(12.0,4.0,6.0)).initIntensity(new Vec(1.0));
		PointLight pointLight2 = new PointLight().initPosition(new Point(4.0,12.0,6.0)).initIntensity(new Vec(1.0));
		PointLight pointLight3 = new PointLight().initPosition(new Point(-12.0,-20.0,6.0)).initIntensity(new Vec(1.0));
		pyramidScence.addLightSource(dirLight);
		pyramidScence.addLightSource(pointLight1);
		pyramidScence.addLightSource(pointLight2);
		pyramidScence.addLightSource(pointLight3);
		for (int currentHeight = 0; currentHeight < pyramidHeight;currentHeight++) {
			int numOfBoxes = (int)Math.pow(2,pyramidHeight-1) - 2*currentHeight;
			int offsetX = currentHeight*boxWidth;
			int offsetY = currentHeight*boxDepth;
			for (int i = 0; i <numOfBoxes;i++) {
				for (int j = 0; j<numOfBoxes;j++) {
					Shape boxShape = new AxisAlignedBox(new Point(offsetX+i*boxWidth, offsetY+j*boxDepth, boxHeight*currentHeight),
							new Point(offsetX+(i+1)*boxWidth, offsetY+(j+1)*boxDepth, (currentHeight+1)*boxHeight));
					Material boxMat = Material.getRandomMaterial();
					Surface boxSurface = new Surface(boxShape, boxMat);
					pyramidScence.addSurface(boxSurface);
				}
			}
			
		}
		Plain scenePlain = new Plain(new Vec(0.0, 0.0, 1.0), new Point(0.0, 0.0, 0.0));
		Material plainMat = Material.getGlassMaterial(false).initReflectionIntensity(0.4);
		Surface plainSurface = new Surface(scenePlain, plainMat);
		pyramidScence.addSurface(plainSurface);
		return pyramidScence;
	}

	public static Scene scene5() {
		Shape sphereShape1 = new Sphere(new Point(0.5, 0.5, 0.5), 0.5);
		Material sphereMat1 = Material.getRandomMaterial();
		Surface sphereSurface1 = new Surface(sphereShape1, sphereMat1);

		Shape sphereShape2 = new Sphere(new Point(-0.5, 0.5, 0.5), 0.5);
		Material sphereMat2 = Material.getRandomMaterial();
		Surface sphereSurface2 = new Surface(sphereShape2, sphereMat2);

		Shape sphereShape3 = new Sphere(new Point(0.5, -0.5, 0.5), 0.5);
		Material sphereMat3 = Material.getRandomMaterial();
		Surface sphereSurface3 = new Surface(sphereShape3, sphereMat3);

		Shape sphereShape4 = new Sphere(new Point(-0.5, -0.5, 0.5), 0.5);
		Material sphereMat4 = Material.getRandomMaterial();
		Surface sphereSurface4 = new Surface(sphereShape4, sphereMat4);

		Light dirLight = new DirectionalLight().initDirection(new Vec(0.0, 0.0, -1.0))
				.initIntensity(new Vec(0.7));

		return new Scene().initAmbient(new Vec(1.0))
				.initCamera(new Point(0.0, 0.0, 2.0), new Vec(0.0, 0.0 , -1.0),
						new Vec(1.0, 1.0, 0.0), 1.0)
				.addLightSource(dirLight).addSurface(sphereSurface1).addSurface(sphereSurface2)
				.addSurface(sphereSurface3).addSurface(sphereSurface4).initName("scene5").initAntiAliasingFactor(1)
				.initRenderRefarctions(true).initRenderReflections(true).initMaxRecursionLevel(6);
	}

}
