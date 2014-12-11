package com.eightpsman.funnyds.java.client.ui;

import com.eightpsman.funnyds.core.*;
import com.eightpsman.funnyds.java.JavaImageActor;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.TimerTask;

@SuppressWarnings("serial")
public class MiniViewPort extends JPanel{

	public static final int MINI_VIEW_WIDTH  = 50;
	public static final int MINI_VIEW_HEIGHT = 50;
	
	AffineTransform transform;
	
	float ratio = 0.5f;
	int translate_x;
	int translate_y;

	java.util.Timer updateTimer;

	BasicManager manager;

	Color backgroundColor;

	public MiniViewPort(BasicManager manager){
		this.manager = manager;
		setPreferredSize(new Dimension(MINI_VIEW_WIDTH, MINI_VIEW_HEIGHT));

		updateTimer = new java.util.Timer();
		updateTimer.scheduleAtFixedRate(updateTask, 0, (int)(Constants.DELTA_TIME * 1000));

		backgroundColor = new Color(152, 230, 245, 255);
	}

	public void destroy(){
		if (updateTimer != null)
			updateTimer.cancel();
		updateTimer = null;
		manager = null;
	}

	public void update(){
		Bound bound = manager.getBound();
		
		float ratio_x = (float)MINI_VIEW_WIDTH / bound.width();
		float ratio_y = (float)MINI_VIEW_HEIGHT/ bound.height();

		ratio = ratio_x < ratio_y ? ratio_x : ratio_y;
	
		float cx = bound.centerX();
		float cy = bound.centerY();
		
		translate_x = 25 - (int)(cx*ratio);
		translate_y = 25 - (int)(cy*ratio);
	}
	
	@Override
	protected void paintComponent(Graphics graphic) {
		super.paintComponent(graphic);
		Graphics2D g = (Graphics2D)graphic;
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		g.setColor(Color.MAGENTA);
		g.drawRect(0, 0, 50, 50);
		
		transform = new AffineTransform();
		transform.setToScale(ratio, ratio);
		transform.translate(translate_x/ratio, translate_y/ratio);
		g.transform(transform);

		/** draw devices */
		for (int i= manager.getDevices().size() -1; i>=0; i--){
			Device device = manager.getDevices().get(i);
			drawPool(g, device);
		}

		/** draw actors */
		for (Actor actor : manager.getActors()){
			drawActor(g, actor);
		}
	}

	public void drawActor(Graphics g, Actor actor){
		g.setColor(Color.GREEN);
		if (actor instanceof CircleActor){
			g.fillOval((int)(actor.x - actor.width/2) , (int)(actor.y - actor.height/2), (int) actor.width, (int) actor.height);
		}else if (actor instanceof JavaImageActor){
			JavaImageActor ia = (JavaImageActor) actor;
			if (ia.isFlip()){
				g.drawImage(ia.image, (int)ia.left + (int)ia.width, (int)ia.top, -(int)ia.width, (int)ia.height, null);
			}else{
				g.drawImage(ia.image, (int)ia.left, (int)ia.top, (int)ia.width, (int)ia.height, null);
			}
		}else{
			g.fillRect((int)(actor.x - actor.width/2) , (int)(actor.y - actor.height/2), (int) actor.width/2, (int) actor.height/2);
		}
	}

	public void drawPool(Graphics g, Device pool){
		g.setColor(backgroundColor);
		g.fillRect((int)(pool.x- (int)pool.width/2), (int)(pool.y- (int)pool.height/2), (int)pool.width, (int)pool.height);
		g.setColor(Color.MAGENTA);
		g.setFont(g.getFont().deriveFont(40f));
		g.drawString(pool.id + "", (int)pool.x, (int)pool.y);
	}

	TimerTask updateTask = new TimerTask() {
		@Override
		public void run() {
			MiniViewPort.this.update();
		}
	};
}
