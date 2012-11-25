package com.wordsaretoys.soar;

/**
 * 
 * represents a vector in 3-space plus standard operations
 * 
 * @author chris
 *
 */
public class Vector {
	
	public double x;
	public double y;
	public double z;

	/**
	 * constructor, set initial values
	 * @param x, y, z initial values
	 */
	public Vector(double x, double y, double z) {
		this.set(x,  y,  z);
	}
	
	public Vector() {
		this.set(0, 0, 0);
	}
	
	/**
	 * set vector components
	 * @param x, y, z components of the vector
	 * @return self
	 */
	public Vector set(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
		return this;
	}
	
	/**
	 * copy components of another vector
	 * @param v, vector to copy from
	 * @return self
	 */
	public Vector copy(Vector v) { 
		this.x = v.x;
		this.y = v.y;
		this.z = v.z;
		return this;
	}

	/**
	 * add another vector to this one
	 * @param v, vector to add
	 * @return self
	 */
	public Vector add(Vector v) {
		this.x += v.x;
		this.y += v.y;
		this.z += v.z;
		return this;
	}
	
	/**
	 * subtract another vector from this one
	 * @param v, vector to subtract
	 * @return self
	 */
	public Vector sub(Vector v) {
		this.x -= v.x;
		this.y -= v.y;
		this.z -= v.z;
		return this;
	}
	
	/**
	 * multiply this vector by a constant
	 * @param c, constant to multiply
	 * @return self
	 */
	public Vector mul(double c) {
		this.x *= c;
		this.y *= c;
		this.z *= c;
		return this;
	}
	
	/**
	 * divide this vector by a constant
	 * return zero-length vector if constant equals zero
	 * @param c, constant to divide by
	 * @return self
	 */
	public Vector div(double c) {
		if (c != 0)
		{
			this.x /= c;
			this.y /= c;
			this.z /= c;
		}
		else
			this.set(0, 0, 0);
		return this;
	}
	
	/**
	 * generate additive inverse of the vector
	 * @return self
	 */
	public Vector neg() {
		return this.set(-this.x, -this.y, -this.z); 
	}
	
	/**
	 * return length of the vector
	 * @return length
	 */
	public double length() {
		return Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
	}
	
	/**
	 * return distance between this vector and another
	 * @param v vector
	 * @return distance
	 */
	public double distance(Vector v) {
		double dx = this.x - v.x;
		double dy = this.y - v.y;
		double dz = this.z - v.z;
		return Math.sqrt(dx * dx + dy * dy + dz * dz);
	}
	
	/**
	 * normalize this vector
	 * @return self
	 */
	public Vector norm() {
		double l = this.length();
		return this.div(l);
	}
	
	/**
	 * return dot product between this vector and another
	 * @param v vector
	 * @return dot product
	 */
	public double dot(Vector v) {
		return this.x * v.x + this.y * v.y + this.z * v.z;
	}
	
	/**
	 * cross this vector with another
	 * @param v vector
	 * @return self
	 */
	public Vector cross(Vector v) {
		double tx = this.x;
		double ty = this.y;
		double tz = this.z;
		this.x = ty * v.z - tz * v.y;
		this.y = tz * v.x - tx * v.z;
		this.z = tx * v.y - ty * v.x;
		return this;
	}
	
	/**
	 * copy vector data to double array
	 * @param a double array (size >= 3)
	 * @return filled array [x, y, z]
	 */
	public double[] toArray(double[] a) {
		a[0] = this.x;
		a[1] = this.y;
		a[2] = this.z;
		return a;
	}
	
	/**
	 * round the vector components to the nearest n
	 * @param n number to round by
	 * @return self
	 */
	public Vector nearest(double n) {
		this.x = (double) Math.round(this.x / n) * n;
		this.y = (double) Math.round(this.y / n) * n;
		this.z = (double) Math.round(this.z / n) * n;
		return this;
	}
	
	/**
	 * transform vector by matrix multiplication
	 * @param m 4x4 matrix laid out in 16-element array
	 * @return self
	 */
	public Vector transform(double[] m) {
		double x = m[0] * this.x + m[4] * this.y + m[8] * this.z + m[12];
		double y = m[1] * this.x + m[5] * this.y + m[9] * this.z + m[13];
		double z = m[2] * this.x + m[6] * this.y + m[10] * this.z + m[14];
		double d = m[3] * this.x + m[7] * this.y + m[11] * this.z + m[15];
		return this.set(x / d, y / d, z / d);
	}
	
	/**
	 * generate a guaranteed perpendicular vector
	 * for length > 0
	 * @return self
	 */
	public Vector perp() {
		double swp;
		if (this.x != this.y) {
			swp = this.x;
			this.x = this.y;
			this.y = swp;
		} else if (this.x != this.z) {
			swp = this.x;
			this.x = this.z;
			this.z = swp;
		} else {
			swp = this.y;
			this.y = this.z;
			this.z = swp;
		}
		if (this.x != 0) {
			this.x = -this.x;
		} else if (this.y != 0) {
			this.y = -this.y;
		} else {
			this.z = -this.z;
		}
		return this;
	}
	
}
