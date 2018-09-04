package com.example.mirodone.inventoryappv01.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public class BooksContract {

    public static final String CONTENT_AUTHORITY = "com.example.mirodone.inventoryappv01";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_BOOKS = "books";

    private BooksContract() {
    }

    public static class BooksEntry implements BaseColumns {

        // the MIME type of the CONTENT_URI for a list of books

        public static final String CONTENT_LIST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BOOKS;

        // the MIME type of the CONTENT_URI for a single book

        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BOOKS;


        /**
         * The content URI to access the pet data in the provider
         */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_BOOKS);

        public static final String TABLE_NAME = "books";
        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_BOOK_TITLE = "title";
        public static final String COLUMN_BOOK_PRICE = "price";
        public static final String COLUMN_BOOK_QTY = "qty";
        public static final String COLUMN_BOOK_SUPPNAME = "suppliername";
        public static final String COLUMN_BOOK_SUPPPHONE = "supplierphone";

        public static final String SQL_CREATE_ENTRIES = "CREATE TABLE " + BooksEntry.TABLE_NAME + "("
                + BooksEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + BooksEntry.COLUMN_BOOK_TITLE + " TEXT NOT NULL, "
                + BooksEntry.COLUMN_BOOK_PRICE + " REAL NOT NULL DEFAULT 0, "
                + BooksEntry.COLUMN_BOOK_QTY + " INTEGER NOT NULL DEFAULT 0, "
                + BooksEntry.COLUMN_BOOK_SUPPNAME + " TEXT NOT NULL, "
                + BooksEntry.COLUMN_BOOK_SUPPPHONE + " INTEGER NOT NULL DEFAULT 0);";

    }
}
