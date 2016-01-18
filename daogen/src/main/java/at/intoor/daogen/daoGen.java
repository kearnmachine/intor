package at.intoor.daogen;

import android.graphics.Matrix;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Property;
import de.greenrobot.daogenerator.Schema;

public class daoGen {

  public static void main(String[] args) throws Exception {
    Schema schema = new Schema(1011, "at.intoor.db.generated");
    addAllEntities(schema);
    new DaoGenerator().generateAll(schema, "app/src/main/java");
  }

  private static void addAllEntities(Schema schema) {
    Entity deviceEntity = schema.addEntity("DeviceEntity");
    Property deviceId = deviceEntity.addIdProperty().autoincrement().getProperty();
    deviceEntity.addStringProperty("address").unique();
    deviceEntity.addStringProperty("name");
    deviceEntity.addIntProperty("txPower");
    deviceEntity.addFloatProperty("x");
    deviceEntity.addFloatProperty("y");
//    deviceEntity.addIntProperty("rssi");
//    deviceEntity.addStringProperty("timestamp");
//    deviceEntity.addFloatProperty("radius");
//    deviceEntity.addFloatProperty("distance");


    // map image

    Entity mapImageEntity = schema.addEntity("MapImageEntity");
    Property mapImageId = mapImageEntity.addIdProperty().autoincrement().getProperty();
    mapImageEntity.addStringProperty("name").unique();
    mapImageEntity.addByteArrayProperty("map");
    mapImageEntity.addFloatProperty("P" + String.format("%03d", Matrix.MSCALE_X));
    mapImageEntity.addFloatProperty("P" + String.format("%03d", Matrix.MSKEW_X));
    mapImageEntity.addFloatProperty("P" + String.format("%03d", Matrix.MTRANS_X));
    mapImageEntity.addFloatProperty("P" + String.format("%03d", Matrix.MSKEW_Y));
    mapImageEntity.addFloatProperty("P" + String.format("%03d", Matrix.MSCALE_Y));
    mapImageEntity.addFloatProperty("P" + String.format("%03d", Matrix.MTRANS_Y));
    mapImageEntity.addFloatProperty("P" + String.format("%03d", Matrix.MPERSP_0));
    mapImageEntity.addFloatProperty("P" + String.format("%03d", Matrix.MPERSP_1));
    mapImageEntity.addFloatProperty("P" + String.format("%03d", Matrix.MPERSP_2));
    mapImageEntity.addDoubleProperty("scale");

    // distance translation

    Entity distanceEntity = schema.addEntity("DistanceEntity");
    Property distanceId = distanceEntity.addLongProperty("distanceId").primaryKey().autoincrement().getProperty();
    distanceEntity.addIntProperty("txPower");
    distanceEntity.addIntProperty("rssi");
    distanceEntity.addDoubleProperty("distance");

    // measurement

    Entity measurementEntity = schema.addEntity("MeasurementEntity");
    Property measurementId = measurementEntity.addIdProperty().autoincrement().getProperty();
    Property timestamp = measurementEntity.addLongProperty("timestamp").getProperty();
    Property device = measurementEntity.addLongProperty("device").getProperty();
    measurementEntity.addIntProperty("rssi");

    deviceEntity.addToMany(measurementEntity,device,"measurements").orderDesc(timestamp);
  }
}
