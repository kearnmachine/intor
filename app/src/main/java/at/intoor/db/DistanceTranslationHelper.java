package at.intoor.db;

import android.util.Log;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import at.intoor.db.generated.DistanceEntity;

public class DistanceTranslationHelper {
  private static final String TAG = "DistanceTrsltr";
  public final static int LIMIT = -140;
  private static final BigDecimal pi = new BigDecimal(Math.PI);
  private static final BigDecimal c = new BigDecimal("299792458");
  private static final BigDecimal f = new BigDecimal("2402000000");//2402,2426,2480 MHz möglich

  private static final BigDecimal factor = c.divide(new BigDecimal("4").multiply(f).multiply(pi), 20, BigDecimal.ROUND_HALF_UP);
  private static final BigDecimal twenty = new BigDecimal("20");

  public static List<DistanceEntity> createRSSITable(int txPower) {
    List<DistanceEntity> list = new ArrayList<>();
    Log.d(TAG,"Generating Table for txPower= " +txPower + "\n");
    for (int rssi = txPower; rssi > LIMIT; rssi--) {
      BigDecimal powerTerm = new BigDecimal(txPower - rssi).divide(twenty, 20, BigDecimal.ROUND_HALF_UP);
      BigDecimal d = factor.multiply(new BigDecimal(Math.pow(10, powerTerm.doubleValue())));
      DistanceEntity entity = new DistanceEntity(null, txPower, rssi, d.doubleValue());
      list.add(entity);
      Log.d(TAG, rssi + "  " + d + "\n");
    }
    return list;
  }

  public static int getListLength(int txPower) {
//    return -LIMIT+txPower; todo uncomment
    return 100000; /// always reset
  }
}
