package com.wordsaretoys.soar;

/**
 * represents an interpolated 2D space
 * @author chris
 *
 */
public class Surface extends Space {

	public int width;
	public int height;
	
	private double amplitude;
	private double xPeriod;
	private double yPeriod;
	
	/**
	 * constructor, create surface
	 * 
	 * use this constructor for heightmaps
	 * 
	 * @param width, height dimensions of surface
	 * @param amp amplitude
	 * @param xpr, ypr period
	 */
	public Surface(int width, int height, double amp, double xpr, double ypr) {
		super(width * height);
		
		this.width = width;
		this.height = height;
		this.amplitude = amp;
		this.xPeriod = xpr;
		this.yPeriod = ypr;
	}

	/**
	 * constructor, create surface
	 * 
	 * use this constructor for textures
	 * 
	 * @param width, height dimensions of surface
	 */
	public Surface(int width, int height) {
		super(width * height);
		
		this.width = width;
		this.height = height;
		amplitude = xPeriod = yPeriod = 0;
	}
	
	/**
	 * get value at (x, y)
	 * @param x, y
	 * @return value at (x, y)
	 */
	public double get(double x, double y) {

		double xf = (xPeriod * x) % width;
		if (xf < 0) {
			xf += width;
		}
		int xi0 = (int) Math.floor(xf);
		double mux = xf - xi0;

		double yf = (yPeriod * y) % height;
		if (yf < 0) {
			yf += height;
		}
		int yi0 = (int) Math.floor(yf);
		double muy = yf - yi0;

		int xi1 = (xi0 + 1) % width;
		int yi1 = (yi0 + 1) % height;
		int y0m = yi0 * width;
		int y1m = yi1 * width;

		double i1 = Space.cerp(data[xi0 + y0m], data[xi0 + y1m], muy);
		double i2 = Space.cerp(data[xi1 + y0m], data[xi1 + y1m], muy);
		return amplitude * Space.cerp(i1, i2, mux);	
	}
}
