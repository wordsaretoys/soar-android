package com.wordsaretoys.soar;

/**
 * represents an interpolated 3D space
 * 
 * @author chris
 *
 */
public class Field extends Space {

	public int width;
	public int height;
	public int depth;
	
	private int area;
	private double amplitude;
	private double xPeriod;
	private double yPeriod;
	private double zPeriod;

	/**
	 * create field
	 * @param width, height, depth dimensions of field
	 * @param amp amplitude
	 * @param xpr, ypr, zpr period
	 */
	public Field(int width, int height, int depth, double amp, double xpr, double ypr, double zpr) {
		super(width * height * depth);
		
		this.area = width * height;
		this.width = width;
		this.height = height;
		this.depth = depth;
		this.amplitude = amp;
		this.xPeriod = xpr;
		this.yPeriod = ypr;
		this.zPeriod = zpr;
	}

	/**
	 * get value at (x, y, z)
	 * @param x, y, z
	 * @return value at (x, y, z)
	 */
	public double get(double x, double y, double z) {

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

		double zf = (zPeriod * z) % depth;
		if (zf < 0) {
			zf += depth;
		}
		int zi0 = (int) Math.floor(zf);
		double muz = zf - zi0;

		int xi1 = (xi0 + 1) % width;
		int yi1 = (yi0 + 1) % height;
		int zi1 = (zi0 + 1) % depth;
		
		int y0m = yi0 * width;
		int y1m = yi1 * width;
		int z0m = zi0 * area;
		int z1m = zi1 * area;

		double i1, i2, i3, i4;
		i1 = Space.cerp(data[xi0 + y0m + z0m], data[xi0 + y0m + z1m], muz);
		i2 = Space.cerp(data[xi0 + y1m + z0m], data[xi0 + y1m + z1m], muz);
		i3 = Space.cerp(i1, i2, muy);

		i1 = Space.cerp(data[xi1 + y0m + z0m], data[xi1 + y0m + z1m], muz);
		i2 = Space.cerp(data[xi1 + y1m + z0m], data[xi1 + y1m + z1m], muz);
		i4 = Space.cerp(i1, i2, muy);
		
		return amplitude * Space.cerp(i3, i4, mux);		
	}
}
