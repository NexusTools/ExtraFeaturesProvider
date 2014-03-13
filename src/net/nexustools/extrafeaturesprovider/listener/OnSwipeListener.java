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
	
	private GestureDetector gestureDetector = null;
	
	public OnSwipeListener(Context context) {
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
					if(Math.abs(deltaX) > SWIPE_DISTANCE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
						return deltaX > 0 ? onSwipeRight() : onSwipeLeft();
					}
				} else {
					if(Math.abs(deltaY) > SWIPE_DISTANCE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
						return deltaY > 0 ? onSwipeBottom() : onSwipeTop();
					}
				}
				return false;
			}
		});
	}
	
	/** 
	 * @return True if the event is to be consumed, false if otherwise.
	 */
	public boolean onSwipeRight() {return false;}

	/** 
	 * @return True if the event is to be consumed, false if otherwise.
	 */
	public boolean onSwipeLeft() {return false;}

	/** 
	 * @return True if the event is to be consumed, false if otherwise.
	 */
	public boolean onSwipeTop() {return false;}

	/** 
	 * @return True if the event is to be consumed, false if otherwise.
	 */
	public boolean onSwipeBottom() {return false;}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if(gestureDetector != null)
			return gestureDetector.onTouchEvent(event);
		else
			return false;
	}
}