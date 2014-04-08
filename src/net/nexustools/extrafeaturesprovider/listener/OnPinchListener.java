package net.nexustools.extrafeaturesprovider.listener;

import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public abstract class OnPinchListener implements OnTouchListener {
	enum State {
		NONE, DRAG, PINCH
	}
	
	public float lastX;
	public float lastY;
	
	public State currentState = State.NONE;
	public boolean pinchInwards = false;
	
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch(event.getAction() & MotionEvent.ACTION_MASK) {
			case MotionEvent.ACTION_DOWN:
				currentState = State.DRAG;
			break;
			
			case MotionEvent.ACTION_POINTER_DOWN:
				lastX = event.getX(1);
				lastY = event.getY(1);
				float deltaX = event.getX(0) - event.getX(1);
				float deltaY = event.getY(0) - event.getY(1);
				double dist = Math.sqrt(deltaX * deltaX + deltaY * deltaY);
				if(dist > 10D)
					currentState = State.PINCH;
			
			break;
			
			case MotionEvent.ACTION_UP:
				currentState = State.NONE;
			break;
			case MotionEvent.ACTION_POINTER_UP:
				currentState = State.DRAG;
			break;
			
			case MotionEvent.ACTION_MOVE:
				switch(currentState) {
					case DRAG:
						return onDragEvent();
						
					case PINCH:
						float nDeltaX = event.getX(1) - lastX;
						float nDeltaY = event.getY(1) - lastY;
						
						lastX = event.getX(1);
						lastY = event.getY(1);
						return onPinchEvent(nDeltaX, nDeltaY);
					default:
					break;
				}
			break;
		}
		
		return false;
	}
	
	/**
	 * @return True if the event is to be consumed, false if otherwise.
	 */
	public abstract boolean onDragEvent();
	
	/**
	 * @param deltaX
	 *            The amount of change in the x position from the last event.
	 * @param deltaY
	 *            The amount of change in the y position from the last event.
	 * 
	 * @return <code>true</code> if the event is to be consumed, false if otherwise.
	 */
	public abstract boolean onPinchEvent(float deltaX, float deltaY);
}
