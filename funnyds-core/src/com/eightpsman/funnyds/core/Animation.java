package com.eightpsman.funnyds.core;

import java.awt.image.BufferedImage;

public class Animation extends Thread{

	private int x;
	private int y;
	private int width;
	private int height;
	private BufferedImage[] images;
	
	private int index = 0;
	private int delay = 0;
	
	Runnable onFinish;
	Runnable onChange;
	
	public Animation(BufferedImage[] images, int delay, Runnable onChange, Runnable onFinish){
		this.images = images;
		this.delay = delay;
		this.onChange = onChange;
		this.onFinish = onFinish;
		index = 0;
	}
	public void setOnFinish(Runnable onFinish){
		this.onFinish = onFinish;
	}
	
	public void begin(){
		onChange.run();
		start();
	}
	public Animation x(int x){
		this.x = x;
		return this;
	}
	public Animation y(int y){
		this.y = y;
		return this;
	}
	public Animation width(int width){
		this.width = width;
		return this;
	}
	public Animation height(int height){
		this.height = height;
		return this;
	}
	@Override
	public void run(){
		while (true){
			try {
				Thread.sleep(delay);
				index ++;
				if (index >= images.length)
					break;
				onChange.run(); // update view when change change sprite
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		onFinish.run(); // do something after animation finish
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public BufferedImage getImage() {
		return images[index == images.length ? index-1 : index];
	}

	public void setImages(BufferedImage[] images) {
		this.images = images;
	}
	public int getWidth() {
		return width;
	}
	public void setWidth(int width) {
		this.width = width;
	}
	public int getHeight() {
		return height;
	}
	public void setHeight(int height) {
		this.height = height;
	}
}
