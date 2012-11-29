package com.wordsaretoys.soar;

import java.nio.ByteBuffer;

import android.opengl.GLES20;


/**
 * represents a single texture object.
 * construct textures from surfaces.
 * 
 * @author chris
 *
 */
public class Texture {
	
	private int[] id = new int[1];

	public Texture() {
		id[0] = -1;
	}

	/**
	 * generates a texture from a surface object
	 * 
	 * surface SHOULD be normalized to (0..1) and 
	 * MUST have width & height as powers of two!
	 * 
	 * @param surf
	 * @return true if build suceeded
	 */
	public boolean build(Surface surf) {
		// create and populate a byte buffer
		int length = surf.length;
		double[] data = surf.data;
		ByteBuffer bb = ByteBuffer.allocateDirect(length);
		for (int i = 0; i < length; i++) {
			bb.put( (byte)(data[i] * 256) );
		}
		
		// build into a texture
		return build(GLES20.GL_LUMINANCE, bb, surf.width, surf.height);
	}
	
	/**
	 * build a buffer into a GL texture 
	 * @param format buffer data format (GL_LUMINANCE, GL_RGB, GL_RGBA)
	 * @param bb byte buffer
	 * @param width, height texture dimensions
	 * @return true if build succeeded
	 */
	public boolean build(int format, ByteBuffer bb, int width, int height) {
		// release any existing resources
		release();
		
		// allocate a GL texture
		GLES20.glGenTextures(1, id, 0);

		// reposition buffer pointer
		bb.position(0);

		// copy texture data and generate mipmap
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, id[0]);
		GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, format, width, height, 0, format, GLES20.GL_UNSIGNED_BYTE, bb);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR_MIPMAP_LINEAR);
		GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
		
		return (GLES20.glGetError() == 0);
	}
	
	/**
	 * bind the texture to a sampler and texture unit
	 * 
	 * used in conjunction with shader.activate()
	 * the sampler parameter is available from the activated shader
	 * 
	 * @param index texture unit index {0..MAX_TEXTURE_IMAGE_UNITS}
	 * @param sampler id of sampler variable from shader
	 */
	public void bind(int index, int sampler) {
		GLES20.glUniform1i(sampler, index);
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + index);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, id[0]);
	}
	
	/**
	 * release the GL texture
	 */
	public void release() {
		GLES20.glDeleteTextures(1, id, 0);
	}
	
}
