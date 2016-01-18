package at.intoor.draw;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.view.MotionEvent;
import android.view.View;

import java.math.BigDecimal;

public class ScaleLine implements View.OnTouchListener {
  private BigDecimal refLength = new BigDecimal(2); //ref length to calculate scale
  private PointF start;
  private PointF end;
  private PointF firstTouch = null;
  private PointF secondTouch = null;
  private Paint paint = new Paint();
  private boolean firstTouchIsStart = true;

  public ScaleLine(float x1, float y1, float x2, float y2) {
    start = new PointF(x1, y1);
    end = new PointF(x2, y2);
    paint.setColor(Color.RED);
    paint.setStyle(Paint.Style.STROKE);
    paint.setStrokeWidth(8f);
  }

  public void drawOn(Canvas canvas) {
    canvas.drawLine(start.x, start.y, end.x, end.y, paint);
  }

  public double getScale() {
    BigDecimal x1 = new BigDecimal(start.x);
    BigDecimal y1 = new BigDecimal(start.y);
    BigDecimal x2 = new BigDecimal(end.x);
    BigDecimal y2 = new BigDecimal(end.y);
    BigDecimal length = new BigDecimal(Math.sqrt(Math.pow(x1.subtract(x2).doubleValue(),2) + Math.pow(y1.subtract(y2).doubleValue(),2)));
    return length.divide(refLength,15,BigDecimal.ROUND_HALF_UP).doubleValue();
  }

  @Override
  public boolean onTouch(View v, MotionEvent event) {
    MapView mapView = (MapView) v;
    PointF touch = new PointF(event.getX(), event.getY());
    switch (event.getAction() & MotionEvent.ACTION_MASK) {
      case MotionEvent.ACTION_DOWN:
        double distanceSquaredStart = Math.pow((double) touch.x - (double) start.x, 2) + Math.pow((double) touch.y - (double) start.y, 2);
        double distanceSquaredEnd = Math.pow((double) touch.x - (double) end.x, 2) + Math.pow((double) touch.y - (double) end.y, 2);
        if (Double.compare(distanceSquaredStart, distanceSquaredEnd) < 0) {
          //take start
          firstTouch = start;
          secondTouch = end;
        }else {
          //take end
          firstTouch = end;
          secondTouch = start;
        }
        break;
      case MotionEvent.ACTION_MOVE:
        if (firstTouch == null || secondTouch == null) return false;
        firstTouch.x = event.getX(0); //alt impl //selectedDevice.getX() + (event.getX() - start.x);
        firstTouch.y = event.getY(0); //alt impl //selectedDevice.getY() + (event.getY() - start.y);
        if(event.getPointerCount()>1){
          secondTouch.x = event.getX(1);
          secondTouch.y = event.getY(1);
        }
        break;
    }

    mapView.postInvalidate();
    return true;
  }

}
