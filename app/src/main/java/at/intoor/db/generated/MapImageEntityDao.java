package at.intoor.db.generated;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

import at.intoor.db.generated.MapImageEntity;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table MAP_IMAGE_ENTITY.
*/
public class MapImageEntityDao extends AbstractDao<MapImageEntity, Long> {

    public static final String TABLENAME = "MAP_IMAGE_ENTITY";

    /**
     * Properties of entity MapImageEntity.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property Name = new Property(1, String.class, "name", false, "NAME");
        public final static Property Map = new Property(2, byte[].class, "map", false, "MAP");
        public final static Property P000 = new Property(3, Float.class, "P000", false, "P000");
        public final static Property P001 = new Property(4, Float.class, "P001", false, "P001");
        public final static Property P002 = new Property(5, Float.class, "P002", false, "P002");
        public final static Property P003 = new Property(6, Float.class, "P003", false, "P003");
        public final static Property P004 = new Property(7, Float.class, "P004", false, "P004");
        public final static Property P005 = new Property(8, Float.class, "P005", false, "P005");
        public final static Property P006 = new Property(9, Float.class, "P006", false, "P006");
        public final static Property P007 = new Property(10, Float.class, "P007", false, "P007");
        public final static Property P008 = new Property(11, Float.class, "P008", false, "P008");
        public final static Property Scale = new Property(12, Double.class, "scale", false, "SCALE");
    };


    public MapImageEntityDao(DaoConfig config) {
        super(config);
    }
    
    public MapImageEntityDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'MAP_IMAGE_ENTITY' (" + //
                "'_id' INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "'NAME' TEXT UNIQUE ," + // 1: name
                "'MAP' BLOB," + // 2: map
                "'P000' REAL," + // 3: P000
                "'P001' REAL," + // 4: P001
                "'P002' REAL," + // 5: P002
                "'P003' REAL," + // 6: P003
                "'P004' REAL," + // 7: P004
                "'P005' REAL," + // 8: P005
                "'P006' REAL," + // 9: P006
                "'P007' REAL," + // 10: P007
                "'P008' REAL," + // 11: P008
                "'SCALE' REAL);"); // 12: scale
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'MAP_IMAGE_ENTITY'";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, MapImageEntity entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String name = entity.getName();
        if (name != null) {
            stmt.bindString(2, name);
        }
 
        byte[] map = entity.getMap();
        if (map != null) {
            stmt.bindBlob(3, map);
        }
 
        Float P000 = entity.getP000();
        if (P000 != null) {
            stmt.bindDouble(4, P000);
        }
 
        Float P001 = entity.getP001();
        if (P001 != null) {
            stmt.bindDouble(5, P001);
        }
 
        Float P002 = entity.getP002();
        if (P002 != null) {
            stmt.bindDouble(6, P002);
        }
 
        Float P003 = entity.getP003();
        if (P003 != null) {
            stmt.bindDouble(7, P003);
        }
 
        Float P004 = entity.getP004();
        if (P004 != null) {
            stmt.bindDouble(8, P004);
        }
 
        Float P005 = entity.getP005();
        if (P005 != null) {
            stmt.bindDouble(9, P005);
        }
 
        Float P006 = entity.getP006();
        if (P006 != null) {
            stmt.bindDouble(10, P006);
        }
 
        Float P007 = entity.getP007();
        if (P007 != null) {
            stmt.bindDouble(11, P007);
        }
 
        Float P008 = entity.getP008();
        if (P008 != null) {
            stmt.bindDouble(12, P008);
        }
 
        Double scale = entity.getScale();
        if (scale != null) {
            stmt.bindDouble(13, scale);
        }
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public MapImageEntity readEntity(Cursor cursor, int offset) {
        MapImageEntity entity = new MapImageEntity( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // name
            cursor.isNull(offset + 2) ? null : cursor.getBlob(offset + 2), // map
            cursor.isNull(offset + 3) ? null : cursor.getFloat(offset + 3), // P000
            cursor.isNull(offset + 4) ? null : cursor.getFloat(offset + 4), // P001
            cursor.isNull(offset + 5) ? null : cursor.getFloat(offset + 5), // P002
            cursor.isNull(offset + 6) ? null : cursor.getFloat(offset + 6), // P003
            cursor.isNull(offset + 7) ? null : cursor.getFloat(offset + 7), // P004
            cursor.isNull(offset + 8) ? null : cursor.getFloat(offset + 8), // P005
            cursor.isNull(offset + 9) ? null : cursor.getFloat(offset + 9), // P006
            cursor.isNull(offset + 10) ? null : cursor.getFloat(offset + 10), // P007
            cursor.isNull(offset + 11) ? null : cursor.getFloat(offset + 11), // P008
            cursor.isNull(offset + 12) ? null : cursor.getDouble(offset + 12) // scale
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, MapImageEntity entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setName(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setMap(cursor.isNull(offset + 2) ? null : cursor.getBlob(offset + 2));
        entity.setP000(cursor.isNull(offset + 3) ? null : cursor.getFloat(offset + 3));
        entity.setP001(cursor.isNull(offset + 4) ? null : cursor.getFloat(offset + 4));
        entity.setP002(cursor.isNull(offset + 5) ? null : cursor.getFloat(offset + 5));
        entity.setP003(cursor.isNull(offset + 6) ? null : cursor.getFloat(offset + 6));
        entity.setP004(cursor.isNull(offset + 7) ? null : cursor.getFloat(offset + 7));
        entity.setP005(cursor.isNull(offset + 8) ? null : cursor.getFloat(offset + 8));
        entity.setP006(cursor.isNull(offset + 9) ? null : cursor.getFloat(offset + 9));
        entity.setP007(cursor.isNull(offset + 10) ? null : cursor.getFloat(offset + 10));
        entity.setP008(cursor.isNull(offset + 11) ? null : cursor.getFloat(offset + 11));
        entity.setScale(cursor.isNull(offset + 12) ? null : cursor.getDouble(offset + 12));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(MapImageEntity entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(MapImageEntity entity) {
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
