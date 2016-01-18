package at.intoor.db.generated;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

import at.intoor.db.generated.DistanceEntity;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table DISTANCE_ENTITY.
*/
public class DistanceEntityDao extends AbstractDao<DistanceEntity, Long> {

    public static final String TABLENAME = "DISTANCE_ENTITY";

    /**
     * Properties of entity DistanceEntity.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property DistanceId = new Property(0, Long.class, "distanceId", true, "DISTANCE_ID");
        public final static Property TxPower = new Property(1, Integer.class, "txPower", false, "TX_POWER");
        public final static Property Rssi = new Property(2, Integer.class, "rssi", false, "RSSI");
        public final static Property Distance = new Property(3, Double.class, "distance", false, "DISTANCE");
    };


    public DistanceEntityDao(DaoConfig config) {
        super(config);
    }
    
    public DistanceEntityDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'DISTANCE_ENTITY' (" + //
                "'DISTANCE_ID' INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: distanceId
                "'TX_POWER' INTEGER," + // 1: txPower
                "'RSSI' INTEGER," + // 2: rssi
                "'DISTANCE' REAL);"); // 3: distance
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'DISTANCE_ENTITY'";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, DistanceEntity entity) {
        stmt.clearBindings();
 
        Long distanceId = entity.getDistanceId();
        if (distanceId != null) {
            stmt.bindLong(1, distanceId);
        }
 
        Integer txPower = entity.getTxPower();
        if (txPower != null) {
            stmt.bindLong(2, txPower);
        }
 
        Integer rssi = entity.getRssi();
        if (rssi != null) {
            stmt.bindLong(3, rssi);
        }
 
        Double distance = entity.getDistance();
        if (distance != null) {
            stmt.bindDouble(4, distance);
        }
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public DistanceEntity readEntity(Cursor cursor, int offset) {
        DistanceEntity entity = new DistanceEntity( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // distanceId
            cursor.isNull(offset + 1) ? null : cursor.getInt(offset + 1), // txPower
            cursor.isNull(offset + 2) ? null : cursor.getInt(offset + 2), // rssi
            cursor.isNull(offset + 3) ? null : cursor.getDouble(offset + 3) // distance
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, DistanceEntity entity, int offset) {
        entity.setDistanceId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setTxPower(cursor.isNull(offset + 1) ? null : cursor.getInt(offset + 1));
        entity.setRssi(cursor.isNull(offset + 2) ? null : cursor.getInt(offset + 2));
        entity.setDistance(cursor.isNull(offset + 3) ? null : cursor.getDouble(offset + 3));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(DistanceEntity entity, long rowId) {
        entity.setDistanceId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(DistanceEntity entity) {
        if(entity != null) {
            return entity.getDistanceId();
        } else {
            return null;
        }
    }

    /** @inheritdoc */
    @Override    
    protected boolean isEntityUpdateable() {
        return true;
    }
    
}
