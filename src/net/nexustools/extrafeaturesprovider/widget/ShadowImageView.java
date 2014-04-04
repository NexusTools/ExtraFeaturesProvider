package net.nexustools.extrafeaturesprovider.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.ImageView;

public class ShadowImageView extends ImageView {
	public static final int ALPHA_BLACK = Color.argb(100, 0, 0, 0);
	private Paint shadowAdditionPainter;
	private Rect shadowRectangle;
	private float shadowRadius = 3;
	private float shadowRadiusOffX = 0;
	private float shadowRadiusOffY = 0;
	private int shadowColor = ALPHA_BLACK;
	
	public ShadowImageView(Context context) {
	    super(context);
	    initPainter();
    }
	
	public ShadowImageView(Context context, AttributeSet attrs) {
	    super(context, attrs);
	    initPainter();
    }
	
	public ShadowImageView(Context context, AttributeSet attrs, int defStyle) {
	    super(context, attrs, defStyle);
	    initPainter();
    }
	
	public void initPainter() {
		shadowRectangle = new Rect();
		
		shadowAdditionPainter = new Paint();
		shadowAdditionPainter.setStyle(Style.STROKE);
		shadowAdditionPainter.setShadowLayer(getShadowRadius(), getShadowRadiusOffX(), getShadowRadiusOffY(), getShadowColor());
	}
	
	
    @Override
	public void onDraw(Canvas canvas) {
		if(getDrawable() != null) {
			shadowRectangle.set(0, 0, getWidth() - 1, getHeight() - 1);
			canvas.drawRect(shadowRectangle, shadowAdditionPainter);
		}
		super.onDraw(canvas);
	}

	public float getShadowRadius() {
	    return shadowRadius;
    }

	public void setShadowRadius(float shadowRadius) {
	    this.shadowRadius = shadowRadius;
		shadowAdditionPainter.setShadowLayer(getShadowRadius(), getShadowRadiusOffX(), getShadowRadiusOffY(), getShadowColor());
		invalidate();
    }

	public float getShadowRadiusOffX() {
	    return shadowRadiusOffX;
    }

	public void setShadowRadiusOffX(float shadowRadiusOffX) {
	    this.shadowRadiusOffX = shadowRadiusOffX;
		shadowAdditionPainter.setShadowLayer(getShadowRadius(), getShadowRadiusOffX(), getShadowRadiusOffY(), getShadowColor());
		invalidate();
    }

	public float getShadowRadiusOffY() {
	    return shadowRadiusOffY;
    }

	public void setShadowRadiusOffY(float shadowRadiusOffY) {
	    this.shadowRadiusOffY = shadowRadiusOffY;
		shadowAdditionPainter.setShadowLayer(getShadowRadius(), getShadowRadiusOffX(), getShadowRadiusOffY(), getShadowColor());
		invalidate();
    }

	public int getShadowColor() {
	    return shadowColor;
    }

	public void setShadowColor(int shadowColor) {
	    this.shadowColor = shadowColor;
		shadowAdditionPainter.setShadowLayer(getShadowRadius(), getShadowRadiusOffX(), getShadowRadiusOffY(), getShadowColor());
		invalidate();
    }
}
