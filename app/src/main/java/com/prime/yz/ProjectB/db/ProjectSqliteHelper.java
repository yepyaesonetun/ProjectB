package com.prime.yz.ProjectB.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by KKT on 8/1/2017.
 **/

public class ProjectSqliteHelper extends SQLiteOpenHelper{
    private Context context;

    public static final String DATABASE_NAME = "PhotoDb";
    public static final int DATABASE_VERSION = 1;

    // Table and Columns
    // Photo Table
    public static final String TABLE_PHOTO = "photo";
    public static final String COLUMN_PHOTO_ID = "PhotoId";
    public static final String COLUMN_PHOTO_ALBUM_ID = "albumId";
    public static final String COLUMN_PHOTO_TITLE = "title";
    public static final String COLUMN_PHOTO_URL = "url";

    //TODO create Photo Table
    public static final String CRATE_TABLE_PHOTO = "CREATE TABLE "+TABLE_PHOTO+" (" +
            COLUMN_PHOTO_ID +" INTEGER PRIMARY KEY, " +
            COLUMN_PHOTO_ALBUM_ID+" INTEGER, " +
            COLUMN_PHOTO_TITLE+ " TEXT," +
            COLUMN_PHOTO_URL+ " TEXT" +
            ")";

    public ProjectSqliteHelper(Context context){
        super(context, DATABASE_NAME,null, DATABASE_VERSION);
        this.context = context;
    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CRATE_TABLE_PHOTO);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
