package com.rss.lostfilm.lostfilmrss;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Hrundel on 22.02.2015.
 */
public class sql_class extends SQLiteOpenHelper {
    public static final String TABLE_NAME = "lostfilm";
    public static final String COL_ID = "id";
    public static final String COL_UPDATEDATE = "updatedate";
    public static final String COL_XML_FULL = "xml_full";
    public static final String COL_FAVORITES = "favorites";
    public static final String COL_SETTINGS = "settings";

    private static final String[] COLUMNS_XML = {COL_UPDATEDATE, COL_XML_FULL};

    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "BookDB";

    public sql_class(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // SQL statement to create book table
        String CREATE_BOOK_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " ( "
                + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_UPDATEDATE + " TEXT, " + COL_XML_FULL + " TEXT, "
                + COL_FAVORITES + " TEXT, " + COL_SETTINGS + " TEXT)";

        // create books table
        db.execSQL(CREATE_BOOK_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older books table if existed
        db.execSQL("DROP TABLE IF EXISTS books");
        // create fresh books table
        this.onCreate(db);
    }

    public void addUpdate(_sql_xml_full items) {
        SQLiteDatabase _db = this.getReadableDatabase();

        ContentValues _cv=new ContentValues();
        _cv.put(COL_UPDATEDATE,items.getLastUpdate());
        _cv.put(COL_XML_FULL,items.getXmlFull());

        _db.insert(TABLE_NAME,null,_cv);
        _db.close();
    }

    public _sql_xml_full getLastUpdateAndXML(_sql_xml_full _value ){

        // 1. get reference to readable DB
        SQLiteDatabase db = this.getReadableDatabase();
        // 2. build query
        Cursor cursor =
                db.query(TABLE_NAME, // a. table
                        COLUMNS_XML, // b. column names
                        " id = ?", // c. selections
                        new String[] { String.valueOf(0) }, // d. selections args
                        null, // e. group by
                        null, // f. having
                        null, // g. order by
                        null); // h. limit

        // 3. if we got results get the first one
        if (cursor != null)
            cursor.moveToFirst();
        // 4. build book object
        _sql_xml_full book = new _sql_xml_full();
        book.setLastUpdate(Integer.parseInt(cursor.getString(0)));
        book.setXmlFull(cursor.getString(1));
        // 5. return book
        return book;
    }

    public int updateLastUpdateAndXML(_sql_xml_full book) {

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put(COL_UPDATEDATE, book.getLastUpdate());
        values.put(COL_XML_FULL, book.getXmlFull());
        // 3. updating row
        int i = db.update(TABLE_NAME, //table
                values, // column/value
                COL_ID+" = ?", // selections
                new String[] { "0" }); //selection args

        // 4. close
        db.close();
        return i;
    }

}
