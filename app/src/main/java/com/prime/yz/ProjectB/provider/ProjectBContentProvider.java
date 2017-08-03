package com.prime.yz.ProjectB.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.prime.yz.ProjectB.db.ProjectSqliteHelper;

/**
 * Created by KKT on 8/1/2017.
 **/

public class ProjectBContentProvider extends ContentProvider {

    public static final int PHOTO = 1;
    static final UriMatcher uriMatcher;
    private static final String PROVIDER_NAME = "com.prime.yz.ProjectB";
    public static final String URI_PHOTO = "content://"+ PROVIDER_NAME +"/photos";
    public static final Uri CONTENT_URI_PHOTO = Uri.parse(URI_PHOTO);

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PROVIDER_NAME, "photos",PHOTO);
    }

    private SQLiteDatabase db;
    private ProjectSqliteHelper dbHelper;

    @Override
    public boolean onCreate() {

        Context context = getContext();
        dbHelper = new ProjectSqliteHelper(context);
        db = dbHelper.getWritableDatabase();

        return db != null;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor cursor;

        switch (uriMatcher.match(uri)){
            case PHOTO:
                cursor = db.query(ProjectSqliteHelper.TABLE_PHOTO, projection,selection,selectionArgs,
                        null,null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Unknown Uri "+uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        switch (uriMatcher.match(uri)) {
            case PHOTO:
                return "vnd.android.cursor.dir/vnd.example.photos";
            default:
                throw new IllegalArgumentException("Unknown Uri" + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {

        Uri _uri = null;
        switch (uriMatcher.match(uri)){
            case PHOTO:
                long photo_id = 0;
                try {
                    db.beginTransaction();
                    photo_id = db.insertWithOnConflict(ProjectSqliteHelper.TABLE_PHOTO, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
                    db.setTransactionSuccessful();
                }catch (Exception e){
                    e.printStackTrace();
                }finally {
                    db.endTransaction();
                }
                if (photo_id > 0 ){
                    _uri = ContentUris.withAppendedId(CONTENT_URI_PHOTO, photo_id);
                    getContext().getContentResolver().notifyChange(_uri, null);
                }
                break;
            default:
                throw new SQLException("Failed to insert row into "+uri);
        }
        return _uri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int count = 0;

        switch (uriMatcher.match(uri)){
            case PHOTO:
                count = db.delete(ProjectSqliteHelper.TABLE_PHOTO, selection, selectionArgs);
                break;
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        int numInserted = 0;
        switch (uriMatcher.match(uri)){
            case PHOTO:
                db.beginTransaction();
                try {
                    for (ContentValues contentValues: values){
                        long id = db.insertOrThrow(ProjectSqliteHelper.TABLE_PHOTO, null, contentValues);
                        if ( id < 0){
                            throw new SQLException("Failed to insert row into "+ uri);
                        }
                    }
                    db.setTransactionSuccessful();
                    numInserted = values.length;
                    getContext().getContentResolver().notifyChange(uri,null);
                }catch (SQLException e){
                    Log.e("Content Provider","bulkInsert: ",e);
                }finally {
                    db.endTransaction();
                }
        }
        return numInserted;
    }
}
