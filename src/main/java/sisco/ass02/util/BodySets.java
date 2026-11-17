package sisco.ass02.util;

import sisco.ass02.model.Body;
import sisco.ass02.model.geometry.P2d;
import sisco.ass02.model.geometry.V2d;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BodySets {

	private BodySets() { }

	public static List<Body> twoBodies() {
		var bodies = new ArrayList<Body>();
		bodies.add(new Body(0, new P2d(-0.3, 0), new V2d(0,0), 0.1, 1));
		bodies.add(new Body(1, new P2d(0.3, 0), new V2d(0,0), 0.1, 1));	
		return bodies;
	}

	public static List<Body> threeBodies() {
		var bodies = new ArrayList<Body>();
		bodies.add(new Body(0, new P2d(0, 0), new V2d(0,0), 0.6, 20));
		bodies.add(new Body(1, new P2d(0.3, 0), new V2d(0,0.25), 0.1,  1));		
		bodies.add(new Body(2, new P2d(-0.5, 0), new V2d(0,-0.15), 0.1, 1));		
		return bodies;
	}

	public static List<Body> random(int nBodies) {
		return random(nBodies, new Random(System.currentTimeMillis()));
	}

	public static List<Body> random(int nBodies, long seed) {
		return random(nBodies, new Random(seed));
	}

	public static List<Body> random(int nBodies, Random rand) {
		var bodies = new ArrayList<Body>();
		for (int i = 0; i < nBodies; i++) {
			double x = -1.0 + 2.0*rand.nextDouble();
			double y = -1.0 + 2.0*rand.nextDouble();
			double vx = -0.1 + 0.2*rand.nextDouble();
			double vy = -0.1 + 0.2*rand.nextDouble();
			double m = 1 + rand.nextDouble()*9;
			Body b = new Body(i, new P2d(x, y), new V2d(vx, vy), 0.01, m);
			bodies.add(b);
		}
		return bodies;
	}
}
