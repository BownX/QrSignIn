package edu.jlu.qrsignin.student;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/**
 * @author xubowen92@gmail.com
 */
public class CaptureView extends View {

  private int width;

  private int height;

  private int defaultColor = Color.GREEN;

  private Paint paint;

  public CaptureView(Context context) {
    this(context, null);
  }

  public CaptureView(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public CaptureView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    paint = new Paint();
    paint.setColor(defaultColor);
    paint.setStyle(Paint.Style.STROKE);
    paint.setStrokeWidth(4);
    setWillNotDraw(false);
  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    width = getMeasuredWidth();
    height = getMeasuredHeight();
  }

  @Override
  protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    if (width != 0 && height != 0) {
      canvas.drawLine(0, 0, width / 3, 0, paint);
      canvas.drawLine(width * 2 / 3, 0, width, 0, paint);
      canvas.drawLine(0, 0, 0, height / 3, paint);
      canvas.drawLine(0, height * 2 / 3, 0, height, paint);
      canvas.drawLine(width, 0, width, height / 3, paint);
      canvas.drawLine(width, height * 2 / 3, width, height, paint);
      canvas.drawLine(0, height, width / 3, height, paint);
      canvas.drawLine(width * 2 / 3, height, width, height, paint);
    }
  }
}
