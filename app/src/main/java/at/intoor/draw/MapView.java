package at.intoor.draw;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.ImageView;

import java.util.List;

import at.intoor.db.Device;

public class MapView extends ImageView {
  private List<Device> devices;
  private double scale = 1d;
  private ScaleLine scaleLine = null;

  public MapView(Context context) {
    super(context);
  }

  public MapView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public MapView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  public MapView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);
  }

  @Override
  protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    for (Device d : this.devices) {
      d.drawOn(canvas);
    }
    if (scaleLine != null) scaleLine.drawOn(canvas);
  }

  public void setDeviceList(List<Device> devices) {
    this.devices = devices;
    this.postInvalidate();
  }

  public void setScaleLine(ScaleLine scaleLine) {
    this.scaleLine = scaleLine;
    this.postInvalidate();
  }

  public double getScale(){
    return scale;
  }

  public void setScale(double scale){
    this.scale = scale;
  }
}
