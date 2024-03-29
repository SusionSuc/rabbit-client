package com.susion.rabbit.base.greendao;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.susion.rabbit.base.entities.RabbitHttpLogInfo;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "RABBIT_HTTP_LOG_INFO".
*/
public class RabbitHttpLogInfoDao extends AbstractDao<RabbitHttpLogInfo, Long> {

    public static final String TABLENAME = "RABBIT_HTTP_LOG_INFO";

    /**
     * Properties of entity RabbitHttpLogInfo.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property Host = new Property(1, String.class, "host", false, "HOST");
        public final static Property Path = new Property(2, String.class, "path", false, "PATH");
        public final static Property RequestBody = new Property(3, String.class, "requestBody", false, "REQUEST_BODY");
        public final static Property ResponseStr = new Property(4, String.class, "responseStr", false, "RESPONSE_STR");
        public final static Property Size = new Property(5, String.class, "size", false, "SIZE");
        public final static Property RequestType = new Property(6, String.class, "requestType", false, "REQUEST_TYPE");
        public final static Property ResponseContentType = new Property(7, String.class, "responseContentType", false, "RESPONSE_CONTENT_TYPE");
        public final static Property ResponseCode = new Property(8, String.class, "responseCode", false, "RESPONSE_CODE");
        public final static Property RequestParamsMapString = new Property(9, String.class, "requestParamsMapString", false, "REQUEST_PARAMS_MAP_STRING");
        public final static Property Time = new Property(10, Long.class, "time", false, "TIME");
        public final static Property TookTime = new Property(11, Long.class, "tookTime", false, "TOOK_TIME");
        public final static Property IsSuccessRequest = new Property(12, boolean.class, "isSuccessRequest", false, "IS_SUCCESS_REQUEST");
        public final static Property IsExceptionRequest = new Property(13, boolean.class, "isExceptionRequest", false, "IS_EXCEPTION_REQUEST");
    }


    public RabbitHttpLogInfoDao(DaoConfig config) {
        super(config);
    }
    
    public RabbitHttpLogInfoDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"RABBIT_HTTP_LOG_INFO\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "\"HOST\" TEXT," + // 1: host
                "\"PATH\" TEXT," + // 2: path
                "\"REQUEST_BODY\" TEXT," + // 3: requestBody
                "\"RESPONSE_STR\" TEXT," + // 4: responseStr
                "\"SIZE\" TEXT," + // 5: size
                "\"REQUEST_TYPE\" TEXT," + // 6: requestType
                "\"RESPONSE_CONTENT_TYPE\" TEXT," + // 7: responseContentType
                "\"RESPONSE_CODE\" TEXT," + // 8: responseCode
                "\"REQUEST_PARAMS_MAP_STRING\" TEXT," + // 9: requestParamsMapString
                "\"TIME\" INTEGER," + // 10: time
                "\"TOOK_TIME\" INTEGER," + // 11: tookTime
                "\"IS_SUCCESS_REQUEST\" INTEGER NOT NULL ," + // 12: isSuccessRequest
                "\"IS_EXCEPTION_REQUEST\" INTEGER NOT NULL );"); // 13: isExceptionRequest
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"RABBIT_HTTP_LOG_INFO\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, RabbitHttpLogInfo entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String host = entity.getHost();
        if (host != null) {
            stmt.bindString(2, host);
        }
 
        String path = entity.getPath();
        if (path != null) {
            stmt.bindString(3, path);
        }
 
        String requestBody = entity.getRequestBody();
        if (requestBody != null) {
            stmt.bindString(4, requestBody);
        }
 
        String responseStr = entity.getResponseStr();
        if (responseStr != null) {
            stmt.bindString(5, responseStr);
        }
 
        String size = entity.getSize();
        if (size != null) {
            stmt.bindString(6, size);
        }
 
        String requestType = entity.getRequestType();
        if (requestType != null) {
            stmt.bindString(7, requestType);
        }
 
        String responseContentType = entity.getResponseContentType();
        if (responseContentType != null) {
            stmt.bindString(8, responseContentType);
        }
 
        String responseCode = entity.getResponseCode();
        if (responseCode != null) {
            stmt.bindString(9, responseCode);
        }
 
        String requestParamsMapString = entity.getRequestParamsMapString();
        if (requestParamsMapString != null) {
            stmt.bindString(10, requestParamsMapString);
        }
 
        Long time = entity.getTime();
        if (time != null) {
            stmt.bindLong(11, time);
        }
 
        Long tookTime = entity.getTookTime();
        if (tookTime != null) {
            stmt.bindLong(12, tookTime);
        }
        stmt.bindLong(13, entity.getIsSuccessRequest() ? 1L: 0L);
        stmt.bindLong(14, entity.getIsExceptionRequest() ? 1L: 0L);
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, RabbitHttpLogInfo entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String host = entity.getHost();
        if (host != null) {
            stmt.bindString(2, host);
        }
 
        String path = entity.getPath();
        if (path != null) {
            stmt.bindString(3, path);
        }
 
        String requestBody = entity.getRequestBody();
        if (requestBody != null) {
            stmt.bindString(4, requestBody);
        }
 
        String responseStr = entity.getResponseStr();
        if (responseStr != null) {
            stmt.bindString(5, responseStr);
        }
 
        String size = entity.getSize();
        if (size != null) {
            stmt.bindString(6, size);
        }
 
        String requestType = entity.getRequestType();
        if (requestType != null) {
            stmt.bindString(7, requestType);
        }
 
        String responseContentType = entity.getResponseContentType();
        if (responseContentType != null) {
            stmt.bindString(8, responseContentType);
        }
 
        String responseCode = entity.getResponseCode();
        if (responseCode != null) {
            stmt.bindString(9, responseCode);
        }
 
        String requestParamsMapString = entity.getRequestParamsMapString();
        if (requestParamsMapString != null) {
            stmt.bindString(10, requestParamsMapString);
        }
 
        Long time = entity.getTime();
        if (time != null) {
            stmt.bindLong(11, time);
        }
 
        Long tookTime = entity.getTookTime();
        if (tookTime != null) {
            stmt.bindLong(12, tookTime);
        }
        stmt.bindLong(13, entity.getIsSuccessRequest() ? 1L: 0L);
        stmt.bindLong(14, entity.getIsExceptionRequest() ? 1L: 0L);
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public RabbitHttpLogInfo readEntity(Cursor cursor, int offset) {
        RabbitHttpLogInfo entity = new RabbitHttpLogInfo( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // host
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // path
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // requestBody
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // responseStr
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // size
            cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6), // requestType
            cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7), // responseContentType
            cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8), // responseCode
            cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9), // requestParamsMapString
            cursor.isNull(offset + 10) ? null : cursor.getLong(offset + 10), // time
            cursor.isNull(offset + 11) ? null : cursor.getLong(offset + 11), // tookTime
            cursor.getShort(offset + 12) != 0, // isSuccessRequest
            cursor.getShort(offset + 13) != 0 // isExceptionRequest
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, RabbitHttpLogInfo entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setHost(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setPath(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setRequestBody(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setResponseStr(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setSize(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setRequestType(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
        entity.setResponseContentType(cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7));
        entity.setResponseCode(cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8));
        entity.setRequestParamsMapString(cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9));
        entity.setTime(cursor.isNull(offset + 10) ? null : cursor.getLong(offset + 10));
        entity.setTookTime(cursor.isNull(offset + 11) ? null : cursor.getLong(offset + 11));
        entity.setIsSuccessRequest(cursor.getShort(offset + 12) != 0);
        entity.setIsExceptionRequest(cursor.getShort(offset + 13) != 0);
     }
    
    @Override
    protected final Long updateKeyAfterInsert(RabbitHttpLogInfo entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(RabbitHttpLogInfo entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(RabbitHttpLogInfo entity) {
        return entity.getId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
