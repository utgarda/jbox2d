package org.jbox2d.common;

import org.jbox2d.collision.OBB;
import org.jbox2d.pooling.TLMat22;

public class OBBViewportTransform implements IViewportTransform{
	
	private final OBB box = new OBB();
	private boolean yFlip = false;
	private final Mat22 yFlipMat = new Mat22(1,0,0,-1);
	private final Mat22 yFlipMatInv = yFlipMat.invert();
	
	public OBBViewportTransform(){
		box.R.setIdentity();
	}
	
	public void set(OBBViewportTransform vpt){
		box.center.set(vpt.box.center);
		box.extents.set( vpt.box.extents);
		box.R.set( vpt.box.R);
		yFlip = vpt.yFlip;
	}
	
	/**
	 * @see IViewportTransform#setCamera(float, float, float)
	 */
	public void setCamera(float x, float y, float scale){
		box.center.set(x, y);
		Mat22.createScaleTransform(scale, box.R);
	}
	
	/**
	 * @see IViewportTransform#getExtents()
	 */
	public Vec2 getExtents(){
		return box.extents;
	}
	
	/**
	 * @see IViewportTransform#setExtents(Vec2)
	 */
	public void setExtents(Vec2 argExtents){
		box.extents.set(argExtents);
	}
	
	/**
	 * @see IViewportTransform#setExtents(float, float)
	 */
	public void setExtents(float argHalfWidth, float argHalfHeight){
		box.extents.set(argHalfWidth, argHalfHeight);
	}
	
	/**
	 * @see IViewportTransform#getCenter()
	 */
	public Vec2 getCenter(){
		return box.center;
	}
	
	/**
	 * @see IViewportTransform#setCenter(Vec2)
	 */
	public void setCenter(Vec2 argPos){
		box.center.set(argPos);
	}
	
	/**
	 * @see IViewportTransform#setCenter(float, float)
	 */
	public void setCenter(float x, float y){
		box.center.set(x,y);
	}
	
	/**
	 * gets the transform of the viewport, transforms around the center.
	 * Not a copy.
	 * @return
	 */
	public Mat22 getTransform(){
		return box.R;
	}
	
	/**
	 * Sets the transform of the viewport.  Transforms about the center.
	 * @param transform
	 */
	public void setTransform(Mat22 transform){
		box.R.set(transform);
	}
	
	/**
	 * Multiplies the obb transform by the given transform
	 * @param argTransform
	 */
	public void mulByTransform(Mat22 argTransform){
		box.R.mulLocal(argTransform);
	}
	
	/**
	 * @see IViewportTransform#isYFlip()
	 */
	public boolean isYFlip() {
		return yFlip;
	}

	/**
	 * @see IViewportTransform#setYFlip(boolean)
	 */
	public void setYFlip(boolean yFlip) {
		this.yFlip = yFlip;
	}

	// djm pooling
	private static final TLMat22 tlInv = new TLMat22();
	/**
	 * @see IViewportTransform#vectorInverseTransform(Vec2, Vec2)
	 */
	public void vectorInverseTransform(Vec2 argScreen, Vec2 argWorld) {
		Mat22 inv = tlInv.get();
		inv.set(box.R);
		inv.invertLocal();
		inv.mulToOut(argScreen, argWorld);
	}

	/**
	 * @see IViewportTransform#vectorTransform(Vec2, Vec2)
	 */
	public void vectorTransform(Vec2 argWorld, Vec2 argScreen) {
		box.R.mulToOut(argWorld, argScreen);
	}
	
	/**
	 * @see IViewportTransform#getWorldToScreen(Vec2, Vec2)
	 */
	public void getWorldToScreen(Vec2 argWorld, Vec2 argScreen){
		argScreen.set(argWorld);
		argScreen.subLocal(box.center);
		box.R.mulToOut(argScreen, argScreen);
		if(yFlip){
			yFlipMat.mulToOut( argScreen, argScreen);
		}
		argScreen.addLocal(box.extents);
	}
	
	
	/**
	 * @see IViewportTransform#getScreenToWorld(Vec2, Vec2)
	 */
	public void getScreenToWorld(Vec2 argScreen, Vec2 argWorld){
		argWorld.set(argScreen);
		argWorld.subLocal(box.extents);
		Mat22 inv = box.R.invert();
		inv.mulToOut(argWorld, argWorld);
		if(yFlip){
			yFlipMatInv.mulToOut( argWorld, argWorld);
		}
		argWorld.addLocal(box.center);
	}
}