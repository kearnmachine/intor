package at.intoor.db;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import java.util.ArrayList;
import java.util.List;

import at.intoor.db.generated.DeviceEntity;
import at.intoor.db.generated.MeasurementEntity;

/**
 * Pojo for persisting Bluetooth devices, wrapper with Helpermethods.
 */

public class Device {
  public final static int MAX_TIMEOUT = 30; // seconds
  private final static float DEFAULT_RADIUS = 20;
  private DeviceEntity device;
  private volatile float calculatedRadius = 0f;
  private Paint paint;
  private Paint paintRadius;
  private Paint paintDelimiter;

  public Device(DeviceEntity dev) {
    device = dev;
    setDefaults();
  }

  public DeviceEntity getDeviceEntity() {
    return this.device;
  }

  @Override
  public String toString() {
    if (device.getAddress() == null) return "";
    String ret = device.getName(); // rssi is written in name in FindDeviceActivity
    ret += ", " + device.getAddress();
    return ret;
  }

  ///////////////////////////////////////////////
  // DRAW ///////////////////////////////////////
  ///////////////////////////////////////////////
  public void setDefaults() {
    paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    paint.setColor(Color.BLUE);
    paintRadius = new Paint();
    paintRadius.setColor(Color.BLUE);
    paintRadius.setAlpha(30);
    paintDelimiter = new Paint();
    paintDelimiter.setStyle(Paint.Style.STROKE);
    paintDelimiter.setColor(Color.BLACK);
    paintDelimiter.setStrokeWidth(4);

  }

  public void drawOn(Canvas canvas) {
    if (device.getX() == null) device.setX(200f);
    if (device.getY() == null) device.setY(200f);
    float x = device.getX();
    float y = device.getY();
    canvas.drawCircle(x, y, DEFAULT_RADIUS, this.paint);
    canvas.drawCircle(x, y, calculatedRadius, this.paintRadius);
    canvas.drawCircle(x,y,calculatedRadius,this.paintDelimiter);
  }


  //////////////////////////////////////////////////////////////////////////////////
  // Helper
  //////////////////////////////////////////////////////////////////////////////////

  public static Device getFromList(String address, List<Device> list) {
    for (Device b : list) {
      if (b.getAddress().equals(address)) return b;
    }
    return null;
  }

  /**
   * convert from db
   *
   * @param list
   * @return
   */
  public static List<DeviceEntity> unwrap(List<Device> list) {
    List<DeviceEntity> ret = new ArrayList<>();
    for (Device d : list) {
      ret.add(d.getDeviceEntity());
    }
    return ret;
  }

  public static List<Device> wrap(List<DeviceEntity> list){
    List<Device> ret = new ArrayList<>();
    for(DeviceEntity d : list){
      ret.add(new Device(d));
    }
    return ret;
  }

  public MeasurementEntity getLastMeasurement() {
    List<MeasurementEntity> list = device.getMeasurements();
    if (list != null && list.size() > 0) return list.get(0);
    else return new MeasurementEntity(null,0L,device.getId(),Integer.MIN_VALUE);
  }
  //////////////////////////////////////////////////////
  // Getter & Setter ///////////////////////////////////
  //////////////////////////////////////////////////////
  public Long getId() {
    return device.getId();
  }

  public void setId(Long id) {
    device.setId(id);
  }

  public String getAddress() {
    return device.getAddress();
  }

  public void setAddress(String address) {
    device.setAddress(address);
  }

  public String getName() {
    return device.getName();
  }

  public void setName(String name) {
    device.setName(name);
  }

  public Integer getTxPower() {
    return device.getTxPower();
  }

  public void setTxPower(Integer txPower) {
    device.setTxPower(txPower);
  }

  public Float getX() {
    return device.getX();
  }

  public void setX(Float x) {
    device.setX(x);
  }

  public Float getY() {
    return device.getY();
  }

  public void setY(Float y) {
    device.setY(y);
  }

  public float getCalculatedRadius() {
    return calculatedRadius;
  }

  public void setCalculatedRadius(float radius) {
    calculatedRadius = radius;
  }

  public List<MeasurementEntity> getMeasurements() {
    return device.getMeasurements();
  }

}
