package com.wordsaretoys.soar;

/**
 * Base class for application objects that require frame updates.
 * Register subclasses with Display.addGLObject() to receive them.
 * 
 * Note that all subclass methods will be called in the GL thread.
 * 
 * @author chris
 *
 */
public class GLObject {
	public void onInit() {}
	public void onUpdate() {}
	public void onResize() {}
}
