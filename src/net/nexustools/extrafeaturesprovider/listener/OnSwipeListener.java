package net.nexustools.extrafeaturesprovider.listener;

import android.content.Context;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public abstract class OnSwipeListener implements OnTouchListener {
	private static final int SWIPE_DISTANCE_THRESHOLD = 75;
	private static final int SWIPE_VELOCITY_THRESHOLD = 100;
	
	private boolean anotherPointer = false;
	
	private GestureDetector gestureDetector = null;

	public OnSwipeListener(Context context) {
		this(context, SWIPE_DISTANCE_THRESHOLD, SWIPE_VELOCITY_THRESHOLD);
	}
	
	public OnSwipeListener(Context context, final int distanceThreshold, final int velocityThreshold) {
		gestureDetector = new GestureDetector(context, new SimpleOnGestureListener() {
			@Override
			public boolean onDown(MotionEvent e) {
				return true;
			}
			
			@Override
			public boolean onFling(MotionEvent firstEvent, MotionEvent secondEvent, float velocityX, float velocityY) {
				float deltaX = secondEvent.getX() - firstEvent.getX();
				float deltaY = secondEvent.getY() - firstEvent.getY();
				if(Math.abs(deltaX) > Math.abs(deltaY)) {
					if(Math.abs(deltaX) > distanceThreshold && Math.abs(velocityX) > velocityThreshold) {
						return deltaX > 0 ? onSwipeRight() : onSwipeLeft();
					}
				} else {
					if(Math.abs(deltaY) > distanceThreshold && Math.abs(velocityY) > velocityThreshold) {
						return deltaY > 0 ? onSwipeBottom() : onSwipeTop();
					}
				}
				return false;
			}
		});
	}
	
	/**
	 * @return <code>true</code> if the event is to be consumed, <code>false</code> if otherwise.
	 */
	public boolean onSwipeRight() {
		return false;
	}
	
	/**
	 * @return <code>true</code> if the event is to be consumed, <code>false</code> if otherwise.
	 */
	public boolean onSwipeLeft() {
		return false;
	}
	
	/**
	 * @return <code>true</code> if the event is to be consumed, <code>false</code> if otherwise.
	 */
	public boolean onSwipeTop() {
		return false;
	}
	
	/**
	 * @return <code>true</code> if the event is to be consumed, <code>false</code> if otherwise.
	 */
	public boolean onSwipeBottom() {
		return false;
	}
	
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if(gestureDetector == null)
			return false;
		int eventAction = event.getAction();
		switch(eventAction) {
			case MotionEvent.ACTION_POINTER_DOWN:
				anotherPointer = true;
				break;
			case MotionEvent.ACTION_POINTER_UP:
				anotherPointer = false;
				break;
			case MotionEvent.ACTION_DOWN:
			case MotionEvent.ACTION_MOVE:
			case MotionEvent.ACTION_UP:
				return anotherPointer ? false : gestureDetector.onTouchEvent(event);
		}
		return false;
	}
}