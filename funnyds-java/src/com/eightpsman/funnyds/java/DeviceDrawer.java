package com.eightpsman.funnyds.java;

import com.eightpsman.funnyds.core.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.util.TimerTask;

@SuppressWarnings("serial")
public class DeviceDrawer extends JPanel implements ComponentListener{

	/** transformer factor */
	float ratio = 1.0f;
	int translate_x;
	int translate_y;
	AffineTransform transform;

	private int viewW;
	private int viewH;

	java.util.Timer updateTimer;

	BasicManager manager;

	Font textFont;

	Color backgroundColor;

	boolean isSpannable;
	boolean isShowInfo;
	boolean canAddActor;
	boolean isKeepView;

	public DeviceDrawer(BasicManager manager){
		this(manager, true, true, true);
	}

	public DeviceDrawer(BasicManager manager, boolean isInteractable){
		this(manager, isInteractable, isInteractable, isInteractable);
	}

	public DeviceDrawer(BasicManager manager, boolean isSpannable, boolean isShowInfo, boolean canAddActor){
		this.manager = manager;
		this.isSpannable = isSpannable;
		this.isShowInfo = isShowInfo;
		this.canAddActor = canAddActor;
		this.isKeepView = false;

		MouseHandler mouseHandler = new MouseHandler();

		if (isSpannable){
			addMouseListener(mouseHandler);
			addMouseMotionListener(mouseHandler);
			addMouseWheelListener(mouseHandler);
		}
		addComponentListener(this);

		updateTimer = new java.util.Timer();
		updateTimer.scheduleAtFixedRate(updateTask, 0, (int)(Constants.DELTA_TIME * 1000));

		textFont = UIManager.getDefaults().getFont("TextField.font");
		backgroundColor = new Color(152, 230, 245, 255);
	}

	public void changeShowInfo(){
		isShowInfo = !isShowInfo;
	}

	public void setKeepView(boolean isKeepView){
		this.isKeepView = isKeepView;
	}

	public void destroy(){
		if (updateTimer != null)
			updateTimer.cancel();
		updateTimer = null;
		manager = null;
	}

	@Override
	protected void paintComponent(Graphics graphic) {
		super.paintComponent(graphic);

		Graphics2D g = (Graphics2D)graphic;
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		AffineTransform savedTransform = g.getTransform();

		transform = new AffineTransform();
		transform.translate(translate_x, translate_y);
		transform.scale(ratio, ratio);
		g.transform(transform);

		/** draw anchor */
		g.setColor(Color.BLACK);
		g.drawLine(-50, 0, 50, 0);
		g.drawLine(0, -50, 0, 50);

		/** draw devices */
		for (int i= manager.getDevices().size() -1; i>=0; i--){
			Device device = manager.getDevices().get(i);
			drawPool(g, device);
		}

		/** draw bound */
		if (!isSpannable){
			Bound bound = manager.getBound();
			g.setColor(Color.MAGENTA);
			g.drawRect((int)bound.left, (int)bound.top, (int)bound.width(), (int)bound.height());
		}

		/** draw actors */
		for (Actor actor : manager.getActors()){
			drawActor(g, actor);
		}

		g.setTransform(savedTransform);

		/** draw info */
		if (isShowInfo){
			g.setColor(Color.BLACK);
			g.setFont(textFont);
			String info = String.format("TRANSLATE: %d : %d     ZOOM: %d%%", translate_x, translate_y, (int)(ratio*100));
			g.drawString(info, 5, viewH - 5);
		}

	}
	
	public void setViewRatio(float ratio){
		this.ratio = ratio;
	}
	
	public void setFullScreen(){
		if (manager instanceof ClientManager){
			ClientManager clientManager = (ClientManager)manager;
			this.ratio = 1.0f;
			Device dv = clientManager.getLocalDevice();
			translate_x = (int)(viewW/2 - dv.x -1);
			translate_y = (int)(viewH/2 - dv.y -1);
		}
	}
	
	public void setFillScreen(){
		Bound bound = manager.getBound();
		
		float ratio_x = (float)viewW / bound.width();
		float ratio_y = (float)viewH / bound.height();

		ratio = ratio_x < ratio_y ? ratio_x : ratio_y;
	
		float cx = bound.centerX();
		float cy = bound.centerY();
		
		translate_x = viewW/2 - (int)(cx*ratio);
		translate_y = viewH/2 - (int)(cy*ratio);
	}
	
	public void drawPool(Graphics2D g, Device device){
		g.setColor(backgroundColor);
		g.fillRect((int)device.left, (int)device.top, (int)device.width, (int)device.height);
		if (isShowInfo){
			g.setColor(Color.MAGENTA);
			g.setFont(g.getFont().deriveFont(40f));
			g.drawString(device.id + "", (int)device.x, (int)device.y);
		}
		if (device.isActive && isSpannable){
			Stroke savedStrock = g.getStroke();
			g.setColor(Color.MAGENTA);
			g.setStroke(new BasicStroke(2f));
			g.drawRect((int) device.left, (int) device.top, (int)device.width, (int)device.height);
			g.setStroke(savedStrock);
		}
	}

	public void drawActor(Graphics g, Actor actor){
		g.setColor(Color.GREEN);
		if (actor instanceof CircleActor){
			g.fillOval((int)(actor.left) , (int)(actor.top), (int) actor.width, (int) actor.height);
		}else if (actor instanceof JavaImageActor){
			JavaImageActor ia = (JavaImageActor) actor;
			if (ia.isFlip()){
				g.drawImage(ia.image, (int)ia.left + (int)ia.width, (int)ia.top, -(int)ia.width, (int)ia.height, null);
			}else{
				g.drawImage(ia.image, (int)ia.left, (int)ia.top, (int)ia.width, (int)ia.height, null);
			}
			g.drawRect((int)actor.x -5, (int)actor.y -5, 10, 10);
		}else{
			g.setColor(Color.red);
			g.fillRect((int) (actor.left), (int) (actor.top), (int) actor.width, (int) actor.height);
		}
		g.setColor(Color.MAGENTA);
		g.setFont(g.getFont().deriveFont(10f));
		g.drawString(actor.id +"", (int)(actor.x - actor.width/2), (int)actor.top);
	}

	@Override
	public void componentResized(ComponentEvent e) {
		viewW = getWidth();
		viewH = getHeight();
		translate_x = viewW / 2;
		translate_y = viewH / 2;
		repaint();
	}

	@Override
	public void componentMoved(ComponentEvent e) {

	}

	@Override
	public void componentShown(ComponentEvent e) {

	}

	@Override
	public void componentHidden(ComponentEvent e) {

	}

	public void inverseTransform(Point point){
		try {
			transform.inverseTransform(point, point);
		} catch (NoninvertibleTransformException e) {
			e.printStackTrace();
		}
	}

	class MouseHandler extends MouseAdapter{
		Point start = new Point();
		Point pDrag = new Point();
		Point pPrev = new Point();
		Device movingDevice;
		
		@Override
		public void mouseMoved(MouseEvent event) {
			super.mouseMoved(event);
		}
		
		@Override
		public void mouseClicked(MouseEvent event) {
			super.mouseClicked(event);
			if (!canAddActor) return;
			if (SwingUtilities.isLeftMouseButton(event)){
				Point point = new Point(event.getX(), event.getY());
				inverseTransform(point);
				for (Device device : manager.getDevices())
					if (device.isInDevice(point.x, point.y)){
						if (callback != null){
							callback.createActorAt(point.x, point.y);
						}else{

						}
						break;
					}
			}
		}

		@Override
		public void mousePressed(MouseEvent event) {
			super.mousePressed(event);

			if (isKeepView) return;

			start.x = pPrev.x = event.getX();
			start.y = pPrev.y = event.getY();
			
			Point p = new Point(event.getX(), event.getY());
			inverseTransform(p);
			
			if (SwingUtilities.isLeftMouseButton(event)){
				if (movingDevice != null)
					movingDevice.isActive = false;
				// current active pool
//				movingDevice = null;
//				for (Device device : manager.getDevices()){
//				if (device.isActive){
//					if (!device.isInDevice(p.x, p.y)){
//						device.isActive = false;
//					}else{
//						movingDevice = device;
//					}
//					break;
//				}
//			}
		}else if (SwingUtilities.isRightMouseButton(event)){
			for (Device device : manager.getDevices())
				if (device.isInDevice(p.x, p.y)){
					device.isActive = true;
					movingDevice = device;
					for (Device dv : manager.getDevices())
						if (dv != device)
							dv.isActive = false;
					break;
				}

		}

		}

		@Override
		public void mouseWheelMoved(MouseWheelEvent event) {
			if (isKeepView) return;

			ratio -= event.getWheelRotation() * 0.05f;
			if (ratio < 0.1f) ratio = 0.1f;
			if (ratio > 2.0f) ratio = 2.0f;
			super.mouseWheelMoved(event);
		}

		int move_x;
		int move_y;
		@Override
		public void mouseDragged(MouseEvent event) {
			if (isKeepView) return;

			pDrag.x = event.getX();
			pDrag.y = event.getY();
			int x = pDrag.x - pPrev.x;
			int y = pDrag.y - pPrev.y;
			pPrev.x = pDrag.x;
			pPrev.y = pDrag.y;
			
			if (SwingUtilities.isLeftMouseButton(event)){
				translate_x += x;
				translate_y += y;
			}else if (SwingUtilities.isRightMouseButton(event)){
				if (movingDevice != null){
					movingDevice.px += x / ratio / manager.getDpi();
					movingDevice.py += y / ratio / manager.getDpi();
					manager.updateDevice(movingDevice);
				}
			}
			super.mouseDragged(event);
		}
	}

	TimerTask updateTask = new TimerTask() {
		@Override
		public void run() {
			if (!isSpannable || isKeepView)
				setFullScreen();
			DeviceDrawer.this.repaint();
		}
	};

	DeviceDrawerCallback callback;

	public void setCallback(DeviceDrawerCallback callback){
		this.callback = callback;
	}

	public interface DeviceDrawerCallback{
		Actor createActorAt(int x, int y);
	}

}
