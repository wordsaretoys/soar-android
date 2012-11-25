package com.wordsaretoys.soar;

/**
 * texture/heightmap/field pattern generators
 * 
 * @author chris
 *
 */
final public class Pattern {

	final static private Random rng = new Random();
	
	/**
	 * fill a space with a given value
	 * @param space, space to fill
	 * @param c, value to fill it with
	 */
	final static public void fill(Space space, double c) {
		double[] data = space.data;
		long length = space.length;
		for (int i = 0; i < length; i++) {
			data[i] = c;
		}
	}

	/**
	 * fill a space with random values
	 * @param space space to fill
	 * @param seed RNG seed
	 * @param lo lowest bound of random values
	 * @param hi highest bound of random values
	 */
	final static public void randomize(Space space, long seed, double lo, double hi) {
		double[] data = space.data;
		long length = space.length;

		rng.reseed(seed);
		for (int i = 0; i < length; i++) {
			data[i] = rng.get(lo, hi);
		}
	}
	
	/**
	 * generate pattern by random walking across space
	 * 
	 * blend MUST be the range (0..1)
	 * p0-p3 MUST be in range (0...1)
	 * 
	 * @param surf surface to walk across
	 * @param seed RNG seed
	 * @param reps multiplier for iterations
	 * @param blend multiplier for blending 
	 * @param c value to blend on each pass
	 * @param p0 probability of moving +x on each pass
	 * @param p1 probability of moving +y on each pass
	 * @param p2 probability of moving -x on each pass
	 * @param p3 probability of moving -y on each pass
	 */
	final static public void walk(Surface surf, long seed, double reps, double blend, double c, double p0, double p1, double p2, double p3) {
		double[] data = surf.data;
		int width = surf.width;
		int height = surf.height;
		
		long il = Math.round(width * height * reps);
		double dnelb = 1 - blend;
		int x, y, i, j;
		
		rng.reseed(seed);
		x = (int) Math.floor(rng.get(0, width));
		y = (int) Math.floor(rng.get(0, height));
		for (i = 0; i < il; i++) {
		
			j = x + width * y;
			data[j] = data[j] * dnelb + c * blend;
			
			if (rng.get() < p0) {
				x++;
				if (x >= width) {
					x = 0;
				}
			}
			if (rng.get() < p1) {
				y++;
				if (y >= height) {
					y = 0;
				}
			}
			if (rng.get() < p2) {
				x--;
				if (x < 0) {
					x = width - 1;
				}
			}
			if (rng.get() < p3) {
				y--;
				if (y < 0) {
					y = height - 1;
				}
			}
		}
	}
	
	/**
	 * draw a line across a surface (with wrapping)
	 * 
	 * blend MUST be in the range (0..1)
	 * 
	 * @param surf surface to draw on
	 * @param blend multiplier for blending
	 * @param c value to blend in
	 * @param x, y starting point of line
	 * @param dx, dy direction of line
	 * @param len length of line
	 */
	final static public void scratch(Surface surf, double blend, double c, int x, int y, double dx, double dy, int len) {
		double[] data = surf.data;
		int width = surf.width;
		int height = surf.height;
		
		double dnelb = 1 - blend;
		int i, j;
		
		for (i = 0; i < len; i++) {
		
			j = (int)(Math.floor(x) + width * Math.floor(y));
			data[j] = data[j] * dnelb + c * blend;
			
			x += dx;
			y += dy;
			
			if (x >= width) {
				x = 0;
			}
			if (y >= height) {
				y = 0;
			}
			if (x < 0) {
				x = width - 1;
			}
			if (y < 0) {
				y = height - 1;
			}
		}
	}

	/**
	 * blend in a value at random points
	 * @param space space to fill
	 * @param seed RNG seed
	 * @param reps multiplier for iterations
	 * @param blend multiplier for blending
	 * @param c value to blend in
	 */
	final static public void stipple(Space space, long seed, int reps, double blend, double c) {
		double[] data = space.data;
		long length = space.length;

		long il = Math.round(length * reps);
		double dnelb = 1 - blend;
		int i, j;
		
		rng.reseed(seed);
		for (i = 0; i < il; i++) {
			j = (int) Math.floor(rng.get(0, length));
			data[j] = data[j] * dnelb + c * blend;
		}
	}
	
	/**
	 * adjust values to a given range
	 * @param space space to normalize
	 * @param lo lower bound of range
	 * @param hi upper bound of range
	 */
	final static public void normalize(Space space, double lo, double hi) {
		double[] data = space.data;
		long length = space.length;

		double olo, ohi, d0, d1, nn;
		int i;
		
		// determine existing lo and hi values
		olo = Double.MAX_VALUE;
		ohi = Double.MIN_VALUE;
		for (i = 0; i < length; i++) {
			olo = Math.min(olo, data[i]);
			ohi = Math.max(ohi, data[i]);
		}
		if (olo == ohi)
			return;
		
		// map to new values
		d0 = ohi - olo;
		d1 = hi - lo;
		for (i = 0; i < length; i++) {
			nn = (data[i] - olo) / d0;
			data[i] = nn * d1 + lo;
		}
	}
}
