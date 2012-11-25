package com.wordsaretoys.soar;

/**
 * represents an interpolated 1D space
 * 
 * @author chris
 *
 */
public class Line extends Space {

	public int width;
	
	private double amplitude;
	private double period;
	
	/**
	 * create line
	 * @param width length of line
	 * @param amp line amplitude
	 * @param per line period
	 */
	public Line(int width, double amp, double per) {
		super(width);
		
		this.width = width;
		this.amplitude = amp;
		this.period = per;
	}

	/**
	 * get the line value at x
	 * @param x 
	 * @return value at x
	 */
	public double get(double x) {
		double xf = (period * x) % width;
		if (xf < 0) {
			xf += width;
		}
		int xi0 = (int) Math.floor(xf);
		double mu = xf - xi0;
		int xi1 = (int) (xi0 + 1) % width;
		
		return amplitude * Space.cerp(data[xi0], data[xi1], mu);
	}
}
