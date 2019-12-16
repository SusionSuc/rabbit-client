package com.susion.rabbit.greendao;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.susion.rabbit.entities.RabbitFPSInfo;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "RABBIT_FPSINFO".
*/
public class RabbitFPSInfoDao extends AbstractDao<RabbitFPSInfo, Long> {

    public static final String TABLENAME = "RABBIT_FPSINFO";

    /**
     * Properties of entity RabbitFPSInfo.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property PageName = new Property(1, String.class, "pageName", false, "PAGE_NAME");
        public final static Property MaxFps = new Property(2, int.class, "maxFps", false, "MAX_FPS");
        public final static Property MinFps = new Property(3, int.class, "minFps", false, "MIN_FPS");
        public final static Property AvgFps = new Property(4, int.class, "avgFps", false, "AVG_FPS");
        public final static Property Time = new Property(5, Long.class, "time", false, "TIME");
    }


    public RabbitFPSInfoDao(DaoConfig config) {
        super(config);
    }
    
    public RabbitFPSInfoDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"RABBIT_FPSINFO\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "\"PAGE_NAME\" TEXT," + // 1: pageName
                "\"MAX_FPS\" INTEGER NOT NULL ," + // 2: maxFps
                "\"MIN_FPS\" INTEGER NOT NULL ," + // 3: minFps
                "\"AVG_FPS\" INTEGER NOT NULL ," + // 4: avgFps
                "\"TIME\" INTEGER);"); // 5: time
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"RABBIT_FPSINFO\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, RabbitFPSInfo entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String pageName = entity.getPageName();
        if (pageName != null) {
            stmt.bindString(2, pageName);
        }
        stmt.bindLong(3, entity.getMaxFps());
        stmt.bindLong(4, entity.getMinFps());
        stmt.bindLong(5, entity.getAvgFps());
 
        Long time = entity.getTime();
        if (time != null) {
            stmt.bindLong(6, time);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, RabbitFPSInfo entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String pageName = entity.getPageName();
        if (pageName != null) {
            stmt.bindString(2, pageName);
        }
        stmt.bindLong(3, entity.getMaxFps());
        stmt.bindLong(4, entity.getMinFps());
        stmt.bindLong(5, entity.getAvgFps());
 
        Long time = entity.getTime();
        if (time != null) {
            stmt.bindLong(6, time);
        }
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public RabbitFPSInfo readEntity(Cursor cursor, int offset) {
        RabbitFPSInfo entity = new RabbitFPSInfo( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // pageName
            cursor.getInt(offset + 2), // maxFps
            cursor.getInt(offset + 3), // minFps
            cursor.getInt(offset + 4), // avgFps
            cursor.isNull(offset + 5) ? null : cursor.getLong(offset + 5) // time
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, RabbitFPSInfo entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setPageName(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setMaxFps(cursor.getInt(offset + 2));
        entity.setMinFps(cursor.getInt(offset + 3));
        entity.setAvgFps(cursor.getInt(offset + 4));
        entity.setTime(cursor.isNull(offset + 5) ? null : cursor.getLong(offset + 5));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(RabbitFPSInfo entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(RabbitFPSInfo entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(RabbitFPSInfo entity) {
        return entity.getId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
