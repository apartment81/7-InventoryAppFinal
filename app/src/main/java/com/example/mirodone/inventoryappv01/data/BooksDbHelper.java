package com.example.mirodone.inventoryappv01.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.example.mirodone.inventoryappv01.data.BooksContract.BooksEntry.SQL_CREATE_ENTRIES;

public class BooksDbHelper extends SQLiteOpenHelper {

    //Database version. If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    //Name of the database file
    public static final String DATABASE_NAME = "books.db";

    public BooksDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    //This is called when the database is created for the first time.
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {

    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
