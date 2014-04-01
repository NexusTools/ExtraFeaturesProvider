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
		shadowAdditionPainter.setShadowLayer(6f, 0f, 0f, ALPHA_BLACK);
	}
	
    @Override
	public void onDraw(Canvas canvas) {
		if(getDrawable() != null) {
			shadowRectangle.set(0, 0, getWidth() - 1, getHeight() - 1);
			canvas.drawRect(shadowRectangle, shadowAdditionPainter);
		}
		super.onDraw(canvas);
	}
}
