package com.wordsaretoys.soar;

/**
 * implements an addressable linear congruential PRNG
 * (wraps the Java PRNG)
 * 
 * @author chris
 *
 */
public class Random extends java.util.Random {

	private static final long serialVersionUID = -7936338198349338087L;

	public Random() {
		super();
	}
	
	public Random(long seed) {
		super(seed != 0 ? seed : System.currentTimeMillis());
	}
	
	public void reseed(long seed) {
		setSeed(seed != 0 ? seed : System.currentTimeMillis());
	}
	
	public double get() {
		return nextDouble();
	}
	
	public double get(double u) {
		return u * nextDouble();
	}
	
	public double get(double l, double u) {
		return l + (u - l) * nextDouble();
	}
}
