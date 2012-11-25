package com.wordsaretoys.soar;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;

import android.opengl.GLES20;


/**
 * maintains a list of vertexes that can be built
 * into a Vertex Buffer Object (VBO). optionally,
 * maintains a list of indexes to minimize space.
 * 
 * @author chris
 *
 */
public class Mesh {

	public int drawPrimitive = GLES20.GL_TRIANGLES;
	
	public FloatBuffer data;
	public int length = 0;
	
	public ShortBuffer indexData;
	public int indexLength = 0;
	
	public int drawCount = 0;
	public int stride = 0;
	
	public boolean retain = false;

	final static private int START_LENGTH = 256;
	final static private double LN2 = Math.log(2);
	
	private int[] buffer = new int[2];
	
	private ArrayList<Integer> attrId;
	private ArrayList<Integer> attrSize;
	
	/**
	 * constructor, create buffers and attribute tables
	 */
	public Mesh() {
		data = FloatBuffer.allocate(START_LENGTH);
		indexData = ShortBuffer.allocate(START_LENGTH);
		
		attrId = new ArrayList<Integer>();
		attrSize = new ArrayList<Integer>();
	}
	
	/**
	 * add an attribute to the mesh
	 * 
	 * attribute ids should be retrieved from the shader.
	 * attributes may only contain floats, no integers.
	 * 
	 * @param id attribute id
	 * @param size attribute size in floats
	 */
	public void add(int id, int size) {
		attrId.add(id);
		attrSize.add(size);
		stride += size;
	}
	
	/**
	 * grow the vertex buffer if necessary
	 * @param n number of floats to grow by
	 */
	private void grow(int n) {
		int newSize = length + n;
		if (newSize > data.capacity()) {
			// find smallest power of 2 greater than newSize
			int l = (int) Math.pow(2, Math.ceil(Math.log(newSize) / LN2));
			FloatBuffer newBuffer = FloatBuffer.allocate(l);
			data.position(0);
			newBuffer.put(data);
			data = newBuffer;
			data.position(length);
		}
	}
	
	/**
	 * specify a set of vertex data
	 * 
	 * data must be specified in the same order as the
	 * attributes that were specified in mesh.add()
	 * 
	 * @param args list of floats to add to the mesh
	 */
	public void set(double... args) {
		int i, il = args.length;
		grow(il);
		for (i = 0; i < il; i++) {
			data.put( (float) args[i] );
		}
		length += il;
	}
	
	/**
	 * load an array of vertex data
	 * @param d array
	 */
	public void load(float[] d) {
		grow(d.length);
		data.put(d);
		length += d.length;
	}
	
	/**
	 * reset the mesh for use with a new data set
	 */
	public void reset() {
		length = 0;
		indexLength = 0;
		data.position(0);
		indexData.position(0);
	}
	
	/**
	 * release GL resources
	 */
	public void release() {
		GLES20.glDeleteBuffers(2, buffer, 0);
	}
	
	/**
	 * generate GL buffers from the vertex/index data
	 */
	public void build() {
		// discard any existing buffers
		release();
		
		// allocate new buffers and bind data to them
		GLES20.glGenBuffers(2, buffer, 0);
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffer[0]);
		data.position(0);
		GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, length * 4, data, GLES20.GL_STATIC_DRAW);
		
		GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, buffer[1]);
		indexData.position(0);
		GLES20.glBufferData(GLES20.GL_ELEMENT_ARRAY_BUFFER, indexLength * 2, indexData, GLES20.GL_STATIC_DRAW);
		
		// set draw length
		drawCount = (indexLength > 0) ? indexLength : (int) Math.ceil(length / stride);
		
		// if data isn't to be retained, discard it
		if (!retain) {
			data = FloatBuffer.allocate(START_LENGTH);
			indexData = ShortBuffer.allocate(START_LENGTH);
		}
	}
	
	/**
	 * draw the mesh
	 * @param offset starting vertex to draw
	 * @param length count of vertexes to draw
	 */
	public void draw(int offset, int length) {
		// bind the buffers
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffer[0]);
		GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, buffer[1]);
		
		// enable and specify each attribute
		int i, acc, il, id, sz;
		for (i = 0, acc = 0, il = attrId.size(); i < il; i++) {
			id = attrId.get(i);
			sz = attrSize.get(i);
			GLES20.glEnableVertexAttribArray(id);
			GLES20.glVertexAttribPointer(id, sz, GLES20.GL_FLOAT, false, stride * 4, acc * 4);
			acc += sz;
		}
		
		// draw elements/arrays
		if (indexLength > 0) {
			GLES20.glDrawElements(drawPrimitive, length, GLES20.GL_UNSIGNED_SHORT, offset);
		} else {
			GLES20.glDrawArrays(drawPrimitive, offset, length);
		}
		
		// disable attributes
		for (i = 0, il = attrId.size(); i < il; i++) {
			GLES20.glDisableVertexAttribArray(attrId.get(i));
		}
	}
	
	/**
	 * draw entire mesh
	 */
	public void draw() {
		draw(0, drawCount);
	}
	
	/**
	 * grow the index buffer if necessary
	 * @param n number of shorts to grow by
	 */
	private void growIndex(int n) {
		int newSize = indexLength + n;
		if (newSize > indexData.capacity()) {
			// find smallest power of 2 greater than newSize
			int l = (int) Math.pow(2, Math.ceil(Math.log(newSize) / LN2));
			ShortBuffer newBuffer = ShortBuffer.allocate(l);
			indexData.position(0);
			newBuffer.put(indexData);
			indexData = newBuffer;
			indexData.position(indexLength);
		}
	}
	
	/**
	 * load an array of index data
	 * @param d array
	 */
	public void loadIndex(short[] d) {
		growIndex(d.length);
		indexData.put(d);
		indexLength += d.length;
	}
	
	/**
	 * specify a set of index data
	 * 
	 * @param args list of shorts to add to the mesh
	 */
	public void index(int... args) {
		int i, il = args.length;
		growIndex(il);
		for (i = 0; i < il; i++) {
			indexData.put( (short) args[i]);
		}
		indexLength += il;
	}
	
	/**
	 * method for iterating over a 2D mesh
	 * useful for building heightmaps
	 * 
	 * create an anonymous instance of the class
	 * and override the vertex method with code
	 * to set each vertex.
	 * 
	 * @author chris
	 *
	 */
    static public class iterator2D {
    	
    	/**
    	 * iterates over a 2D mesh, generating indexes
    	 * 
    	 * @param mesh
    	 * @param il, jl number of steps
    	 * @param wind winding order
    	 */
    	public iterator2D(Mesh mesh, int il, int jl, boolean wind) {
    		int im = il - 1;
    		int jm = jl - 1;
    		int k = mesh.length / mesh.stride;
    		int i, j;

    		for (i = 0; i < il; i++) {
    			for (j = 0; j < jl; j++, k++) {
    				vertex((double)i / (double) im, (double) j / (double) jm);
    				if (i < im && j < jm) {
    					if (wind) {
    						mesh.index(k, k + jl, k + 1, k + jl, k + jl + 1, k + 1);
    					} else {
    						mesh.index(k, k + 1, k + jl, k + jl, k + 1, k + jl + 1);
    					}
    				}
    			}
    		}
    	}

    	/**
    	 * override in local class to set vertexes
    	 * @param ir, jr ratio of current step to total steps (0..1) 
    	 */
    	public void vertex(double ir, double jr) {}
    }
}
