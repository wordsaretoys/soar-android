package com.wordsaretoys.soar;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.content.Context;
import android.opengl.GLES20;
import android.util.Log;


/**
 * maintains a shader program consisting of one
 * vertex shader function and one fragment shader
 * function. 
 * 
 * @author chris
 *
 */
public class Shader {

	private int vobj;
	private int fobj;
	private int program;
	
	public Shader() {
	}

	/**
	 * generates GL shader program from source code
	 * 
	 * @param vertex source code for vertex shader
	 * @param fragment source code for fragment shader
	 * @throws Exception 
	 */
	public boolean build(String vertex, String fragment) {
		String error;
		int[] status = new int[1];
		
		// compile the vertex shader
		vobj = GLES20.glCreateShader(GLES20.GL_VERTEX_SHADER);
		GLES20.glShaderSource(vobj, vertex);
		GLES20.glCompileShader(vobj);
		
		// check the compilation
		GLES20.glGetShaderiv(vobj, GLES20.GL_COMPILE_STATUS, status, 0);
		if (status[0] == 0) {
			error = GLES20.glGetShaderInfoLog(vobj);
			String reason = "vertex shader compile error: " + error;
			Log.e("shader", reason);
			return false;
		}

		// compile the fragment shader
		fobj =  GLES20.glCreateShader(GLES20.GL_FRAGMENT_SHADER);
		GLES20.glShaderSource(fobj, fragment);
		GLES20.glCompileShader(fobj);

		// check the compilation
		GLES20.glGetShaderiv(fobj, GLES20.GL_COMPILE_STATUS, status, 0);
		if (status[0] == 0) {
			error = GLES20.glGetShaderInfoLog(fobj);
			String reason = "fragment shader compile error: " + error;
			Log.e("shader", reason);
			return false;
		}

		// create and link the shader program
		program = GLES20.glCreateProgram();
		GLES20.glAttachShader(program, vobj);
		GLES20.glAttachShader(program, fobj);
		GLES20.glLinkProgram(program);

		// check the linkage
		GLES20.glGetShaderiv(program, GLES20.GL_LINK_STATUS, status, 0);
		if (status[0] == 0) {
			error = GLES20.glGetProgramInfoLog(vobj);
			String reason = "shader program link error: " + error;
			Log.e("shader", reason);
			return false;
		}
		
		return true;
	}
	
	/**
	 * get attribute location id
	 * @param name attribute variable name
	 * @return id
	 */
	public int getAttributeId(String name) {
		return GLES20.glGetAttribLocation(program, name);
	}
	
	/**
	 * get uniform/sampler location id
	 * @param name uniform variable name
	 * @return id
	 */
	public int getUniformId(String name) {
		return GLES20.glGetUniformLocation(program, name);
	}
	
	/**
	 * activate the shader program
	 */
	public void activate() {
		GLES20.glUseProgram(program);
	}
	
	/**
	 * release the shader program
	 */
	public void release() {
		GLES20.glDeleteShader(vobj);
		GLES20.glDeleteShader(fobj);
		GLES20.glDeleteProgram(program);
	}
	
	/**
	 * reads a shader source block from a named resource
	 * @param context activity context
	 * @param resourceId 
	 * @return text of source block
	 */
	static public String getText(Context context, int resourceId) {
		InputStream stream = context.getResources().openRawResource(resourceId);
		BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
		String line, file = "";
		try {
			while( (line = reader.readLine()) != null) {
				file += line;
			}
		} catch(IOException e) {
			e.printStackTrace();
		}
		return file;
	}
}
