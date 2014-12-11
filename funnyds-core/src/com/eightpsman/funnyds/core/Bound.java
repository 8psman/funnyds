package com.eightpsman.funnyds.core;

public class Bound {

	public float left;
	public float top;
	public float right;
	public float bottom;
	
	public Bound(){
		left = top = right = bottom = 0;
	}
	
	public float width(){
		return Math.abs(right - left);
	}
	
	public float height(){
		return Math.abs(top - bottom);
	}
	
	public float centerX(){
		return (left + right)/2;
	}
	
	public float centerY(){
		return (top + bottom)/2;
	}
}
