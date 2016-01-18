package at.intoor.draw;

import android.graphics.PointF;
import android.view.MotionEvent;
import android.view.View;

import java.util.List;

import at.intoor.db.Device;

/**
 * http://judepereira.com/blog/multi-touch-in-android-translate-scale-and-rotate/
 */
public class DevicePositioner implements View.OnTouchListener {
  private Device selectedDevice;
  private List<Device> deviceList;
  private PointF start = new PointF();

  public void setDeviceList(List<Device> list) {
    this.deviceList = list;
  }

  @Override
  public boolean onTouch(View v, MotionEvent event) {
    if (deviceList == null || deviceList.size() == 0) return false;
    MapView mapView = (MapView) v;

    switch (event.getAction() & MotionEvent.ACTION_MASK) {
      case MotionEvent.ACTION_DOWN:
        //todo get current beacon
        double smallestDistanceSquared = Double.MAX_VALUE;
        start.set(event.getX(), event.getY());
        Device currentDevice = null;
        for (Device d : deviceList) {
          double distanceSquared = Math.pow((double) start.x - (double) d.getX(), 2) + Math.pow((double) start.y - (double) d.getY(), 2);
          if (distanceSquared < smallestDistanceSquared) {
            smallestDistanceSquared = distanceSquared;
            currentDevice = d;
          }
        }
        if (currentDevice == null) currentDevice = deviceList.get(0);
        selectedDevice = currentDevice;
        break;
      case MotionEvent.ACTION_MOVE:
        if (selectedDevice == null) return false;
        float dx = event.getX(); //alt impl //selectedDevice.getX() + (event.getX() - start.x);
        float dy = event.getY(); //alt impl //selectedDevice.getY() + (event.getY() - start.y);
        selectedDevice.setX(dx);
        selectedDevice.setY(dy);
        break;
    }

    mapView.postInvalidate();
    return true;
  }
}
