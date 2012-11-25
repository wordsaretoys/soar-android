package com.wordsaretoys.soar;

/**
 * represent quaternion in 3-space plus standard operations
 * 
 * @author chris
 *
 */
public class Quaternion {
	
	public double x;
	public double y;
	public double z;
	public double w;
	
	/**
	 * create new quaternion
	 * @param x, y, z rotation axis
	 * @param w rotation around axis
	 */
	public Quaternion(double x, double y, double z, double w) {
		this.set(x, y, z, w);
	}
	
	public Quaternion() {
		this.set(0, 0, 0, 0);
	}
	
	/**
	 * set the components of the quaternion
	 * @param x, y, z rotation axis
	 * @param w rotation around axis
	 * @return self
	 */
	public Quaternion set(double x, double y, double z, double w) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
		return this;
	}
	
	/**
	 * copy components from another quaternion
	 * @param q quaternion
	 * @return self
	 */
	public Quaternion copy(Quaternion q) { 
		this.x = q.x;
		this.y = q.y;
		this.z = q.z;
		this.w = q.w;
		return this;
	}
	
	/**
	 * multiply this quaternion by another
	 * @param q quaternion
	 * @return self
	 */
	public Quaternion mul(Quaternion q) {
		double tx = this.x;
		double ty = this.y;
		double tz = this.z;
		double tw = this.w;
		this.x = tw * q.x + tx * q.w + ty * q.z - tz * q.y;
		this.y = tw * q.y + ty * q.w + tz * q.x - tx * q.z;
		this.z = tw * q.z + tz * q.w + tx * q.y - ty * q.x;
		this.w = tw * q.w - tx * q.x - ty * q.y - tz * q.z;
		return this;
	}
	
	/**
	 * generate inverse of quaternion
	 * @return self
	 */
	public Quaternion neg() {
		return this.set(-this.x, -this.y, -this.z, this.w); 
	}
	
	/**
	 * normalize the quaternion
	 * @return self
	 */
	public Quaternion norm() {
		double mag = Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z + this.w * this.w);
		return this.set(this.x / mag, this.y / mag, this.z / mag, this.w / mag);
	}
	
	/**
	 * generate matrix from quaternion
	 * @param m 4x4 matrix laid out in 16-element array
	 * @return filled array
	 */
	public float[] toMatrix(float[] m) {
		m[0] = (float)(1.0 - 2.0 * (this.y * this.y + this.z * this.z));
		m[1] = (float)(2.0 * (this.x * this.y + this.z * this.w));
		m[2] = (float)(2.0 * (this.x * this.z - this.y * this.w));
		m[3] = 0;
		m[4] = (float)(2.0 * (this.x * this.y - this.z * this.w));
		m[5] = (float)(1.0 - 2.0 * (this.x * this.x + this.z * this.z));
		m[6] = (float)(2.0 * (this.z * this.y + this.x * this.w));
		m[7] = 0;
		m[8] = (float)(2.0 * (this.x * this.z + this.y * this.w));
		m[9] = (float)(2.0 * (this.y * this.z - this.x * this.w));
		m[10] = (float)(1.0 - 2.0 * (this.x * this.x + this.y * this.y));
		m[11] = 0;
		m[12] = 0;
		m[13] = 0;
		m[14] = 0;
		m[15] = 1;
		return m;
	}
	
	/**
	 * generate quaternion from axis-angle representation
	 * @param x, y, z rotation axis
	 * @param ang rotation angle (in radians)
	 * @return self
	 */
	public Quaternion setFromAxisAngle(double x, double y, double z, double ang) {
		double ha = Math.sin(ang / 2.0);
		return this.set(x * ha, y * ha, z * ha, Math.cos(ang / 2.0));
	}
	
	/**
	 * smooth interpolation between two quaternions
	 * @param a first quaternion
	 * @param b second quaternion
	 * @param m interpolation factor
	 * @return self
	 */
	public Quaternion slerp(Quaternion a, Quaternion b, double m) {
		this.x += m * (b.x - a.x);
		this.y += m * (b.y - a.y);
		this.z += m * (b.z - a.z);
		this.w += m * (b.w - a.w);
		return this.norm();
	}

}
