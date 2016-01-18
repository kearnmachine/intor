package at.intoor.db;

import at.intoor.db.generated.MapImageEntity;

/**
 * Helper class to work with MapImageEntity
 */
public class MapImageHelper {

  public static float[] getValues(MapImageEntity map) {
    float[] values = new float[9];
    values[0] = map.getP000();
    values[1] = map.getP001();
    values[2] = map.getP002();
    values[3] = map.getP003();
    values[4] = map.getP004();
    values[5] = map.getP005();
    values[6] = map.getP006();
    values[7] = map.getP007();
    values[8] = map.getP008();
    return values;
  }

  public static void setValues(MapImageEntity map, float[] values) {
    map.setP000(values[0]);
    map.setP001(values[1]);
    map.setP002(values[2]);
    map.setP003(values[3]);
    map.setP004(values[4]);
    map.setP005(values[5]);
    map.setP006(values[6]);
    map.setP007(values[7]);
    map.setP008(values[8]);
  }
}