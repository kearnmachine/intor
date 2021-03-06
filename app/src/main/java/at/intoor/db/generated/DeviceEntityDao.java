package at.intoor.db.generated;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

import at.intoor.db.generated.DeviceEntity;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table DEVICE_ENTITY.
*/
public class DeviceEntityDao extends AbstractDao<DeviceEntity, Long> {

    public static final String TABLENAME = "DEVICE_ENTITY";

    /**
     * Properties of entity DeviceEntity.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property Address = new Property(1, String.class, "address", false, "ADDRESS");
        public final static Property Name = new Property(2, String.class, "name", false, "NAME");
        public final static Property TxPower = new Property(3, Integer.class, "txPower", false, "TX_POWER");
        public final static Property X = new Property(4, Float.class, "x", false, "X");
        public final static Property Y = new Property(5, Float.class, "y", false, "Y");
    };

    private DaoSession daoSession;


    public DeviceEntityDao(DaoConfig config) {
        super(config);
    }
    
    public DeviceEntityDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
        this.daoSession = daoSession;
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'DEVICE_ENTITY' (" + //
                "'_id' INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "'ADDRESS' TEXT UNIQUE ," + // 1: address
                "'NAME' TEXT," + // 2: name
                "'TX_POWER' INTEGER," + // 3: txPower
                "'X' REAL," + // 4: x
                "'Y' REAL);"); // 5: y
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'DEVICE_ENTITY'";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, DeviceEntity entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String address = entity.getAddress();
        if (address != null) {
            stmt.bindString(2, address);
        }
 
        String name = entity.getName();
        if (name != null) {
            stmt.bindString(3, name);
        }
 
        Integer txPower = entity.getTxPower();
        if (txPower != null) {
            stmt.bindLong(4, txPower);
        }
 
        Float x = entity.getX();
        if (x != null) {
            stmt.bindDouble(5, x);
        }
 
        Float y = entity.getY();
        if (y != null) {
            stmt.bindDouble(6, y);
        }
    }

    @Override
    protected void attachEntity(DeviceEntity entity) {
        super.attachEntity(entity);
        entity.__setDaoSession(daoSession);
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public DeviceEntity readEntity(Cursor cursor, int offset) {
        DeviceEntity entity = new DeviceEntity( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // address
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // name
            cursor.isNull(offset + 3) ? null : cursor.getInt(offset + 3), // txPower
            cursor.isNull(offset + 4) ? null : cursor.getFloat(offset + 4), // x
            cursor.isNull(offset + 5) ? null : cursor.getFloat(offset + 5) // y
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, DeviceEntity entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setAddress(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setName(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setTxPower(cursor.isNull(offset + 3) ? null : cursor.getInt(offset + 3));
        entity.setX(cursor.isNull(offset + 4) ? null : cursor.getFloat(offset + 4));
        entity.setY(cursor.isNull(offset + 5) ? null : cursor.getFloat(offset + 5));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(DeviceEntity entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(DeviceEntity entity) {
        if(entity != null) {
            return entity.getId();
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
