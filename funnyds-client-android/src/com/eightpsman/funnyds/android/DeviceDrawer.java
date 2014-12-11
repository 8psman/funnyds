package com.eightpsman.funnyds.android;

import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import com.eightpsman.funnyds.android.actor.AndroidImageActor;
import com.eightpsman.funnyds.core.*;

public class DeviceDrawer extends View{

	ClientManager manager;
	Device localDevice;
	DrawerCallback callback;

	UpdateThread updateThread;

	Paint paint;
	ZoomUtil zoomUtil;

	int deviceColor = Color.argb(255, 152, 230, 245);

	public DeviceDrawer(Context context, AttributeSet attr){
		super(context, attr);
		initialize();
	}

	public void setManager(ClientManager manager){
		this.manager = manager;
		this.localDevice = manager.getLocalDevice();
		zoomUtil.getMatrix().setScale(1f, 1f);
		zoomUtil.getMatrix().postTranslate(localDevice.width/2, localDevice.height/2);

	}

	public void setCallback(DrawerCallback callback){
		this.callback = callback;
	}

	public void initialize(){
		paint = new Paint();
		zoomUtil = new ZoomUtil();
		setOnTouchListener(zoomListener);
	}
	
	@Override
	public void onAttachedToWindow(){
		super.onAttachedToWindow();
		Log.d(Constants.TAG, "Start pool view update thread");
		updateThread = new UpdateThread();
		updateThread.start();
	}
	
	@Override
	public void onDetachedFromWindow(){
		super.onDetachedFromWindow();
		Log.d(Constants.TAG, "Stop update thread");
		updateThread.cancel();
		updateThread = null;
	}
	
	@Override
	public void onDraw(Canvas canvas){
		super.onDraw(canvas);
		canvas.save();
		canvas.concat(zoomUtil.getMatrix());

		/** draw device */
		for (Device device : manager.getDevices())
			drawDevice(canvas, device);

		for (Actor actor : manager.getActors()){
			drawActor(canvas, actor);
		}

		/** draw device bound*/
		Bound bound = manager.getBound();
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(2f);
		paint.setColor(Color.MAGENTA);
		canvas.drawRect(bound.left, bound.top, bound.right, bound.bottom, paint);

		/** draw anchor */
		paint.setColor(Color.MAGENTA);
		canvas.drawLine(-10, 0, 10, 0, paint);
		canvas.drawLine(0, -10, 0, 10, paint);

		canvas.restore();
	}

	/** draw all devices */
	private void drawDevice(Canvas canvas, Device device){
		paint.setStyle(Paint.Style.FILL);
		paint.setColor(deviceColor);
		canvas.drawRect(device.left, device.top, device.right, device.bottom, paint);
		if (device.isActive){
			paint.setStyle(Paint.Style.STROKE);
			paint.setStrokeWidth(5f);
			paint.setColor(Color.MAGENTA);
			canvas.drawRect(device.left, device.top, device.right, device.bottom, paint);
		}
	}

	/** draw all actor */
	private void drawActor(Canvas canvas, Actor actor){
		paint.setColor(Color.GREEN);
		if (actor instanceof CircleActor){
			canvas.drawOval(
					new RectF(	((CircleActor) actor).left, ((CircleActor) actor).top,
								((CircleActor) actor).right, ((CircleActor) actor).bottom), paint);
		}else if (actor instanceof AndroidImageActor){
			AndroidImageActor imageActor = (AndroidImageActor) actor;
			Bitmap bitmap = imageActor.bitmap;

			Matrix flipHorizontalMatrix = new Matrix();
			flipHorizontalMatrix.setScale((actor.isFlip()?-1:1) * imageActor.width/bitmap.getWidth(), imageActor.height/bitmap.getHeight());
			flipHorizontalMatrix.postTranslate((actor.isFlip()?actor.width:0) + actor.left , actor.top);

			canvas.drawBitmap(bitmap, flipHorizontalMatrix, paint);
		}else{
			canvas.drawRect(actor.x - actor.width/2, actor.y - actor.height/2, actor.x + actor.width/2, actor.y + actor.height/2, paint);
		}
	}

	public void updateView(){
		postInvalidate();
	}

	public void translateToCenter(){
		float currentScale = zoomUtil.getScale();
		zoomUtil.getMatrix().setScale(currentScale, currentScale);
		zoomUtil.getMatrix().postTranslate(localDevice.width/2, localDevice.height/2);
	}

	public void setFullScreen(){
		Matrix matrix = zoomUtil.getMatrix();
		matrix.setScale(1f, 1f);
		Device dv = manager.getLocalDevice();
		matrix.postTranslate(dv.width/2 - dv.x -1, dv.height/2 - dv.y -1);
		updateView();
	}

	public void setFillScreen(){
		Bound bound = manager.getBound();
		Device dv = manager.getLocalDevice();
		float ratio_x = (float)dv.width / bound.width();
		float ratio_y = (float)dv.height / bound.height();

		float ratio = ratio_x < ratio_y ? ratio_x : ratio_y;

		float cx = bound.centerX();
		float cy = bound.centerY();

		float translate_x = dv.width/2 - (int)(cx*ratio);
		float translate_y = dv.height/2 - (int)(cy*ratio);

		zoomUtil.getMatrix().setScale(ratio, ratio);
		zoomUtil.getMatrix().postTranslate(translate_x, translate_y);
		updateView();
	}

	public OnTouchListener zoomListener = new OnTouchListener() {
		float downx;
		float downy;
		boolean isPressingDevice;
		boolean isMovingDevice;
		Device movingDevice;
		float moving_delta_x;
		float moving_delta_y;
		@Override
		public boolean onTouch(View view, MotionEvent event) {
			switch (event.getAction() & MotionEvent.ACTION_MASK){
				case MotionEvent.ACTION_DOWN:
					downx = event.getX();
					downy = event.getY();

					/** check if user want to move device */
					PointF touch = zoomUtil.getRealPosition(event);
					isPressingDevice = false;
					isMovingDevice = false;
					for (Device dv : manager.getDevices())
						if (dv.isInDevice(touch.x, touch.y)){
							isPressingDevice = true;
							movingDevice = dv;
							PointF pos = zoomUtil.getRealPosition(event);
							moving_delta_x = pos.x - dv.x;
							moving_delta_y = pos.y - dv.y;
							break;
						}
					zoomUtil.onTouch(view, event);
					break;

				case MotionEvent.ACTION_POINTER_DOWN:
					zoomUtil.onTouch(view, event);
					break;

				case MotionEvent.ACTION_MOVE:
					float now_x = event.getX();
					float now_y = event.getY();

					if (isPressingDevice){
						if (Math.abs(now_x - downx) > 10 || Math.abs(now_y - downy) > 10)
							isPressingDevice = false;
						if (isPressingDevice)
							if (event.getEventTime() - event.getDownTime() > 1000){
								isPressingDevice = false;
								isMovingDevice = true;
								movingDevice.isActive = true;
							}
					}

					/** moving device */
					if (isMovingDevice){
						PointF pos = zoomUtil.getRealPosition(event);
						movingDevice.px = (pos.x - moving_delta_x) / manager.getDpi();
						movingDevice.py = (pos.y - moving_delta_y) / manager.getDpi();
						manager.updateDevice(movingDevice);
					}else{
						zoomUtil.onTouch(view, event);
					}
					break;

				case MotionEvent.ACTION_POINTER_UP:
					zoomUtil.onTouch(view, event);
					break;

				case MotionEvent.ACTION_UP:
					if (isMovingDevice)
						movingDevice.isActive = false;
					zoomUtil.onTouch(view, event);
					/** check new actor */
					if (Math.abs(event.getX() - downx) < 10 && Math.abs(event.getY() - downy) < 10)
						if (event.getEventTime() - event.getDownTime() < 500){
							PointF pos = zoomUtil.getRealPosition(event);
							for (Device device : manager.getDevices())
								if (device.isInDevice(pos.x, pos.y)){
									callback.onNewActor(pos.x, pos.y);
									break;
								}
						}

					break;
			}

			callback.onChangeView(zoomUtil.getTranslateX(), zoomUtil.getTranslateY(), zoomUtil.getScale());

			invalidate();
			return true;
		}
	};

	class UpdateThread extends Thread{
		
		boolean isRunning;
		public UpdateThread(){
			isRunning = true;
		}
		public void cancel(){
			isRunning = false;
		}
		@Override
		public void run(){
			while (isRunning){
				try {
					Thread.sleep((long)(Constants.DELTA_TIME * 1000));
					updateView();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public interface DrawerCallback{
		void onChangeView(float translate_x, float translate_y, float scale);
		void onNewActor(float x, float y);
	}

}
