package com.wordsaretoys.soar;

import android.opengl.GLES20;


/**
 * maintain a camera: a special case of rotator
 * that implements a projection matrix and view
 * 
 * @author chris
 *
 */
public class Camera extends Rotator {

	public double viewAngle = 30.0;
	public double nearLimit = 0.5;
	public double farLimit = 1000.0;
	
	public float[] modelview;
	public float[] projector;
	
	public Vector position;
	public Vector offset;
	
	public double aspectRatio;
	
	private final double DEG2RAD = Math.PI / 180.0;
	
	/**
	 * constructor
	 */
	public Camera() {
		modelview = new float[16];
		projector = new float[16];
		
		position = new Vector();
		offset = new Vector();
	}
	
	/**
	 * set camera aspect ratio and viewport
	 * @param width, height dimensions of viewport
	 */
	public void setViewport(int width, int height) {
		aspectRatio = (double) width / (double) height;
		GLES20.glViewport(0, 0, width, height);
		update();
	}
	
	/**
	 * generate modelview and projector matrices
	 */
	public void update() {
		// update rotation matrix
		super.update();

		// update projection matrix
		double h = 1 / Math.tan(viewAngle * DEG2RAD);
		double d = nearLimit - farLimit;

		projector[0] = (float)(h / aspectRatio);
		projector[1] = projector[2] = projector[3] = 0;

		projector[5] = (float)h;
		projector[4] = projector[6] = projector[7] = 0;

		projector[10] = (float)((farLimit + nearLimit) / d);
		projector[8] = projector[9] = 0;
		projector[11] = -1;

		projector[14] = (float)(2 * nearLimit * farLimit / d);
		projector[12] = projector[13] = projector[15] = 0;
		
		// update modelview matrix
		System.arraycopy(rotations, 0, modelview, 0, 16);

		modelview[12] = (float)(-(modelview[0] * position.x + 
				modelview[4] * position.y + 
				modelview[8] * position.z) - offset.x);
		modelview[13] = (float)(-(modelview[1] * position.x + 
				modelview[5] * position.y 
				+ modelview[9] * position.z) - offset.y);
		modelview[14] = (float)(-(modelview[2] * position.x + 
				modelview[6] * position.y + 
				modelview[10] * position.z) - offset.z);

		modelview[3] = modelview[7] = modelview[11] = 0;
		modelview[15] = 1;
	}
	
}
