package com.wordsaretoys.soar;

/**
 * creates bitmap of n dimensions for 
 * spatial interpolation and textures
 * 
 * @author chris
 *
 */
public class Space {

	public double[] data;
	public int length;
	
	/**
	 * create space
	 * @param length size of bitmap
	 */
	public Space(int length) {
		this.length = length;
		this.data = new double[length];
	}
	
	/**
	 * cosine interpolation
	 * @param y1, y2 values to interpolate between
	 * @param mu interpolation factor (0..1)
	 * @return interpolated value
	 */
	static final public double cerp(double y1, double y2, double mu) {
		double mu2 = (1.0 - Math.cos(mu * Math.PI)) / 2.0;
		return (y1 * (1.0 - mu2) + y2 * mu2);
	}	
}
