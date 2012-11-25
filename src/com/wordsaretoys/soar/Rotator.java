package com.wordsaretoys.soar;

/**
 * implements a full three-axis rotation object, 
 * with optional constraints on angles involved.
 * 
 * @author chris 
 *
 */
public class Rotator {

	public Quaternion product;
	public Quaternion compx;
	public Quaternion compy;
	public Quaternion compz;
	
	public Vector up;
	public Vector right;
	public Vector front;
	
	public float[] transpose;
	public float[] rotations;
	
	public boolean free;
	public Vector bound;
	public double scale;
	
	private Quaternion c0;
	private Quaternion q0;
	
	/**
	 * constructor, create fields
	 */
	public Rotator() {
		product = new Quaternion(0, 0, 0, 1);
		
		compx = new Quaternion(0, 0, 0, 1);
		compy = new Quaternion(0, 0, 0, 1);
		compz = new Quaternion(0, 0, 0, 1);
		
		up = new Vector();
		right = new Vector();
		front = new Vector();
		
		transpose = new float[16];
		rotations = new float[16];
		
		free = true;
		bound = new Vector();
		scale = 1;
		
		c0 = new Quaternion();
		q0 = new Quaternion();
	}
	
	/**
	 * update rotation matrix and orientation vectors
	 * 
	 * only call this in app code after manual change
	 * to product quaternion
	 * 
	 */
	public void update() {
		// generate rotation matrix
		product.toMatrix(rotations);
		
		// generate orientation vectors
		// (front vector inverted for LH coordinate system)
		right.set(rotations[0], rotations[4], rotations[8]);
		up.set(rotations[1], rotations[5], rotations[9]);
		front.set(rotations[2], rotations[6], rotations[10]).neg();
		
		// apply scale factor to rotation components
		rotations[0] = (float)(rotations[0] * scale);
		rotations[1] = (float)(rotations[1] * scale);
		rotations[2] = (float)(rotations[2] * scale);
		rotations[4] = (float)(rotations[4] * scale);
		rotations[5] = (float)(rotations[5] * scale);
		rotations[6] = (float)(rotations[6] * scale);
		rotations[8] = (float)(rotations[8] * scale);
		rotations[9] = (float)(rotations[9] * scale);
		rotations[10] = (float)(rotations[10] * scale);
		
		// copy to transpose matrix
		System.arraycopy(rotations, 0, transpose, 0, 16);
		transpose[1] = rotations[4];
		transpose[4] = rotations[1];
		transpose[2] = rotations[8];
		transpose[8] = rotations[2];
		transpose[6] = rotations[9];
		transpose[9] = rotations[6];
	}
	
	/**
	 * rotate by specified amount
	 * @param rx, ry, rz rotation on each axis
	 */
	public void turn(double rx, double ry, double rz) {
		// if in free-rotation mode
		if (free) {
		
			compx.setFromAxisAngle(1, 0, 0, rx);
			compy.setFromAxisAngle(0, 1, 0, ry);
			compz.setFromAxisAngle(0, 0, 1, rz);
			q0.copy(compx).mul(compy).mul(compz).mul(product).norm();
			product.copy(q0);
			
		} else {

			// bound mode requires tracking and testing each axis separately

			q0.setFromAxisAngle(1, 0, 0, rx);
			c0.copy(compx).mul(q0).norm();
			if (c0.w >= bound.x) {
				compx.copy(c0);
			}

			q0.setFromAxisAngle(0, 1, 0, ry);
			c0.copy(compy).mul(q0).norm();
			if (c0.w >= bound.y) {
				compy.copy(c0);
			}
			
			q0.setFromAxisAngle(0, 0, 1, rz);
			c0.copy(compz).mul(q0).norm();
			if (c0.w >= bound.z) {
				compz.copy(c0);
			}
			
			product.set(0, 0, 0, 1);
			product.mul(compx).mul(compy).mul(compz).norm();
		}
		
		// generate rotation matricies and unit vectors
		update();
	}
	
	/**
	 * track a second rotator, aligning local rotation with it
	 * 
	 * useful for animations where making a direct copy would
	 * "snap" the model into place without smooth transitions
	 * 
	 * @param r rotation object
	 * @param t amount to align by (0..1)
	 */
	public void track(Rotator r, double t) {
		// perform spherical interpolation
		product.slerp(product, r.product, t);

		// if in bound mode
		if (!free) {
			// decompose product into components
			c0.set(product.x, 0, 0, product.w).norm();
			if (c0.w >= bound.x) {
				compx.copy(c0);
			} else {
				compx.set(0, 0, 0, 1);
			}
			
			c0.set(0, product.y, 0, product.w).norm();
			if (c0.w >= bound.y) {
				compy.copy(c0);
			} else {
				compy.set(0, 0, 0, 1);
			}
			
			c0.set(0, 0, product.z, product.w).norm();
			if (c0.w >= bound.z) {
				compz.copy(c0);
			} else {
				compz.set(0, 0, 0, 1);
			}
		}
		
		update();
	}
}
