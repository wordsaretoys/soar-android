package com.wordsaretoys.soar;

import java.util.ArrayList;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.Log;
import android.view.MotionEvent;


/**
 * implements a GL surface view and renderer
 * plus a frame pump and event registrations
 * 
 * @author chris
 *
 */

public class Display extends GLSurfaceView {
	
	public Context context;
	public Display display;
	
	public int width;
	public int height;
	
	public float touchX;
	public float touchY;
	public float travelX;
	public float travelY;
	public boolean touched;
	public float pressure;
	
	public double interval;
	
	public int fps;
	
	private ArrayList<GLObject> globjects;

	private float rawTouchX;
	private float rawTouchY;
	private float rawTravelX = 0;
	private float rawTravelY = 0;
	private Object touchLock = new Object();
	
	private long lastTime;
	
	private int frameCount;
	private double frameTime;
	
	/**
	 * simple renderer class
	 * mostly just for calling app objects
	 * @author chris
	 *
	 */
	public class Renderer implements GLSurfaceView.Renderer {
		
		private final long GIGA = 1000000000;
		
		public Renderer() {
			super();
			globjects = new ArrayList<GLObject>();
		}

		public void onSurfaceCreated(GL10 unused, EGLConfig config) {
	        Log.i("Soar", "Renderer.onSurfaceCreated");

			for (GLObject o : globjects)
				o.onInit();
			lastTime = System.nanoTime();
			frameTime = lastTime;
	    }

	    public void onDrawFrame(GL10 unused) {
	    	
	    	long t = System.nanoTime();
	    	display.interval = (double)(t - lastTime) / GIGA;
			lastTime = t;

			frameCount++;
			if (t - frameTime >= GIGA) {
				fps = frameCount;
				frameCount = 0;
				frameTime = t;
			}
			
			synchronized(touchLock) {
				touchX = rawTouchX;
				touchY = rawTouchY;
				travelX = rawTravelX / width;
				travelY = rawTravelY / height;
				rawTravelX = 0;
				rawTravelY = 0;
			}
			
			for (GLObject o : globjects)
				o.onUpdate();
			
			requestRender();
	    }

	    public void onSurfaceChanged(GL10 unused, int width, int height) {
	        Log.i("Soar", "Renderer.onSurfaceChanged");

	    	display.width = width;
	    	display.height = height;
			for (GLObject o: globjects)
				o.onResize();
	    }
	}
	
	/**
	 * constructor, creates renderer
	 * @param c
	 */
	public Display(Context context) {
		super(context);
		this.context = context;
		this.display = this;
		setEGLContextClientVersion(2);
		setRenderer(new Renderer());
		setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
	}
	
	/**
	 * add an application object to receive events
	 * @param o
	 */
	public void addGLObject(GLObject o) {
		globjects.add(o);
	}
	
	/**
	 * handles touch events
	 * routes useful stuff to application objects
	 */
	public boolean onTouchEvent(MotionEvent e) {
		
		switch(e.getAction()) {
		
		case MotionEvent.ACTION_DOWN:
			synchronized(touchLock) {
				rawTouchX = e.getX();
				rawTouchY = e.getY();
				touched = true;
				pressure = e.getPressure();
			}
			break;
			
		case MotionEvent.ACTION_MOVE:
			synchronized(touchLock) {
				rawTravelX += (e.getX() - rawTouchX);
				rawTravelY += (e.getY() - rawTouchY);
				rawTouchX = e.getX();
				rawTouchY = e.getY();
				pressure = e.getPressure();
			}
			break;
			
		case MotionEvent.ACTION_UP:
			synchronized(touchLock) {
				rawTouchX = e.getX();
				rawTouchY = e.getY();
				touched = false;
				pressure = e.getPressure();
			}
			break;
			
		}
		
		return true;
	}
	
}
