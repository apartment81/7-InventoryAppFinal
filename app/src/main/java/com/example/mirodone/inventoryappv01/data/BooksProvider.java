package com.example.mirodone.inventoryappv01.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

public class BooksProvider extends ContentProvider {

    //Tag for the log messages
    public static final String LOG_TAG = BooksProvider.class.getSimpleName();
    private static final int BOOKS = 100;
    private static final int BOOKS_ID = 101;
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    //creates a UriMatcher object

    static {
        sUriMatcher.addURI(BooksContract.CONTENT_AUTHORITY, BooksContract.PATH_BOOKS, BOOKS);
        sUriMatcher.addURI(BooksContract.CONTENT_AUTHORITY, BooksContract.PATH_BOOKS + "/#", BOOKS_ID);
    }

    //database helper object.
    private BooksDbHelper myBooksDbHelper;

    /**
     * Initialize the provider and the database helper object.
     */
    @Override
    public boolean onCreate() {
        // Create and initialize a PetDbHelper object to gain access to the pets database.
        // Make sure the variable is a global variable, so it can be referenced from other
        // ContentProvider methods.

        myBooksDbHelper = new BooksDbHelper(getContext());
        return true;
    }

    /**
     * Perform the query for the given URI. Use the given projection, selection, selection arguments, and sort order.
     */
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {

        // Get readable database
        SQLiteDatabase db = myBooksDbHelper.getReadableDatabase();

        // This cursor will hold the result of the query
        Cursor cursor;

        // Figure out if the URI matcher can match the URI to a specific code
        int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:

                cursor = db.query(BooksContract.BooksEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case BOOKS_ID:
                // For the  BOOKS_ID: code, extract out the ID from the URI.
                // For an example URI such as "content://com.example.android.books/books/2",
                // the selection will be "_id=?" and the selection argument will be a
                // String array containing the actual ID of 2 in this case.
                //
                // For every "?" in the selection, we need to have an element in the selection
                // arguments that will fill in the "?". Since we have 1 question mark in the
                // selection, we have 1 String in the selection arguments' String array.
                selection = BooksContract.BooksEntry._ID + "=?";
                selectionArgs = new String[]{
                        String.valueOf(ContentUris.parseId(uri))};


                cursor = db.query(BooksContract.BooksEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;

            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
        //set notification Uri on the Cursor, so we know what content Uri the Cursor was created for
        //if the date at this Uri changes, then we know we need to update the Cursor.

        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }


    // Insert new data into the provider with the given ContentValues.

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                return insertBook(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }


    // Insert a book into the database with the given content values. Return the new content URI
    // for that specific row in the database.

    private Uri insertBook(Uri uri, ContentValues values) {

        //Check that the title is not null
        String title = values.getAsString(BooksContract.BooksEntry.COLUMN_BOOK_TITLE);
        if (title == null) {
            throw new IllegalArgumentException("Book requires a title!");
        }

        // If the price is provided, check that it's greater than or equal to 0
        Integer price = values.getAsInteger(BooksContract.BooksEntry.COLUMN_BOOK_PRICE);
        if (price != null && price < 0) {
            throw new IllegalArgumentException("Book need a valid price");
        }

        // If the weight is provided, check that it's greater than or equal to 0 kg
        Integer qty = values.getAsInteger(BooksContract.BooksEntry.COLUMN_BOOK_QTY);
        if (qty != null && qty < 0) {
            throw new IllegalArgumentException("Book need a valid qty");
        }

        //Check that the supplier name is not null
        String s_name = values.getAsString(BooksContract.BooksEntry.COLUMN_BOOK_SUPPNAME);
        if (s_name == null) {
            throw new IllegalArgumentException("Supplier name missing!");
        }


        //Check that the supplier phone is not null
        String s_phone = values.getAsString(BooksContract.BooksEntry.COLUMN_BOOK_SUPPPHONE);
        if (s_phone == null) {
            throw new IllegalArgumentException("Supplier phone missing!");
        }

        // Get writeable database
        SQLiteDatabase database = myBooksDbHelper.getWritableDatabase();

        // Insert a new book into the books database table with the given ContentValues
        long id = database.insert(BooksContract.BooksEntry.TABLE_NAME, null, values);

        if (id == -1) {
            // If the ID is -1, then the insertion failed. Log an error and return null.
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        // notify all the listeners that the date has changed for the book content URI
        getContext().getContentResolver().notifyChange(uri, null);

        // Once we know the ID of the new row in the table,
        // return the new URI with the ID appended to the end of it
        return ContentUris.withAppendedId(uri, id);
    }

    /**
     * Updates the data at the given selection and selection arguments, with the new ContentValues.
     */
    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {

        final int match = sUriMatcher.match(uri);

        switch (match) {
            case BOOKS:
                return updateBook(uri, contentValues, selection, selectionArgs);
            case BOOKS_ID:
                selection = BooksContract.BooksEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateBook(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for" + uri);
        }
    }

    private int updateBook(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        // If the BooksEntry.COLUMN_BOOK_TITLE  key is present,
        // check that the title value is not null.

        if (values.containsKey(BooksContract.BooksEntry.COLUMN_BOOK_TITLE)) {
            String title = values.getAsString(BooksContract.BooksEntry.COLUMN_BOOK_TITLE);
            if (title == null) {
                throw new IllegalArgumentException("Book requires a title!");
            }
        }

        // If the BooksEntry.COLUMN_BOOK_PRICE  key is present,
        // check that the price value is not null.

        if (values.containsKey(BooksContract.BooksEntry.COLUMN_BOOK_PRICE)) {
            Integer price = values.getAsInteger(BooksContract.BooksEntry.COLUMN_BOOK_PRICE);
            if (price != null && price < 0) {
                throw new IllegalArgumentException("Book need a valid price");
            }
        }

        // If the BooksEntry.COLUMN_BOOK_QTY  key is present,
        // check that the qty value is not null.

        if (values.containsKey(BooksContract.BooksEntry.COLUMN_BOOK_PRICE)) {
            Integer qty = values.getAsInteger(BooksContract.BooksEntry.COLUMN_BOOK_QTY);
            if (qty != null && qty < 0) {
                throw new IllegalArgumentException("Book need a valid qty");
            }
        }

        if (values.containsKey(BooksContract.BooksEntry.COLUMN_BOOK_SUPPNAME)) {
            String s_name = values.getAsString(BooksContract.BooksEntry.COLUMN_BOOK_SUPPNAME);
            if (s_name == null) {
                throw new IllegalArgumentException("Supplier name missing!");
            }
        }

        if (values.containsKey(BooksContract.BooksEntry.COLUMN_BOOK_SUPPPHONE)) {
            String s_phone = values.getAsString(BooksContract.BooksEntry.COLUMN_BOOK_SUPPPHONE);
            if (s_phone == null) {
                throw new IllegalArgumentException("Supplier phone missing!");
            }
        }


        // If there are no values to update, then don't try to update the database
        if (values.size() == 0) {
            return 0;
        }

        // Get writeable database
        SQLiteDatabase database = myBooksDbHelper.getWritableDatabase();

        // Perform the update on the database and get the number of rows affected
        int rowsUpdated = database.update(BooksContract.BooksEntry.TABLE_NAME, values, selection, selectionArgs);

        // If 1 or more rows were updated, then notify all listeners that the data at the
        // given URI has changed
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Return the number of rows updated
        return rowsUpdated;
    }


    /**
     * Delete the data at the given selection and selection arguments.
     */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Get writeable database
        SQLiteDatabase database = myBooksDbHelper.getWritableDatabase();

        // track the number of rows deleted
        int rowsDeleted;

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                //delete all rows that match the selection and sel Args for BOOKS case
                rowsDeleted = database.delete(BooksContract.BooksEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case BOOKS_ID:
                // delete a single row given by the ID in the URI
                selection = BooksContract.BooksEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(BooksContract.BooksEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Cannot be deleted" + uri);
        }
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        //return the number of rows deleted
        return rowsDeleted;
    }

    /**
     * Returns the MIME type of data for the content URI.
     */
    @Override
    public String getType(Uri uri) {

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                return BooksContract.BooksEntry.CONTENT_LIST_TYPE;
            case BOOKS_ID:
                return BooksContract.BooksEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }
}
