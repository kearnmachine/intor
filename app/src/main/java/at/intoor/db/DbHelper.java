package at.intoor.db;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import java.text.SimpleDateFormat;

import at.intoor.db.generated.DaoMaster;
import at.intoor.db.generated.DaoSession;
import de.greenrobot.dao.query.QueryBuilder;

public abstract class DbHelper {
//    public static final String TAG = DbHelper.class.getName();

    public static final String DATABASE_NAME = "intoor-db";
    public static final SimpleDateFormat TIMESTAMPFORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    /**
     * call in onCreate
     *
     * @param context the this of used class
     * @return new Session
     */
    public static DaoSession getWritable(Context context) {
        // TODO DEBUG REMOVE
        QueryBuilder.LOG_SQL = true;
        QueryBuilder.LOG_VALUES = true;
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(context, DATABASE_NAME, null);
        SQLiteDatabase db = helper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        return daoMaster.newSession();
    }
}