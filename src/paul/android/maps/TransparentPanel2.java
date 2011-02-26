package paul.android.maps;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.widget.LinearLayout;

public class TransparentPanel2 extends LinearLayout {

	private Paint innerPaint, borderPaint;
	
	public TransparentPanel2(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	
	private void init() {
		innerPaint = new Paint();
		innerPaint.setARGB(225, 75, 75, 75); // grey
		innerPaint.setAntiAlias(true);
		
		borderPaint = new Paint();
		borderPaint.setARGB(225, 75, 75, 75);
		borderPaint.setAntiAlias(true);
		borderPaint.setStyle(Style.STROKE);
		borderPaint.setStrokeWidth(0);
	}

	public void setInnerPaint(Paint innerPaint) {
		this.innerPaint = innerPaint;
	}
	
	public void setBorderPaint(Paint borderPaint) {
		this.borderPaint = borderPaint;
	}

	protected void dispatchDraw(Canvas canvas) {
		RectF drawRect = new RectF();
		drawRect.set(0,0, getMeasuredWidth(), getMeasuredHeight());
		canvas.drawRect(drawRect, innerPaint);
		canvas.drawRect(drawRect, borderPaint);
		
		super.dispatchDraw(canvas);
	}
}