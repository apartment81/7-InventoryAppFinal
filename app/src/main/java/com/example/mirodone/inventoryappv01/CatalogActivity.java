package com.example.mirodone.inventoryappv01;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.BaseColumns;
import android.support.v7.app.AppCompatActivity;
import android.support.design.widget.*;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.mirodone.inventoryappv01.data.BooksContract;
import com.example.mirodone.inventoryappv01.data.BooksDbHelper;

public class CatalogActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final short BOOK_LOADER = 0;
    BookCursorAdapter bookCursorAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        // Setup FAB to open EditorActivity
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CatalogActivity.this, EditActivity.class);
                startActivity(intent);
            }
        });

        // Find the ListView which will be populated with the pet data
        ListView displayView = (ListView) findViewById(R.id.text_view_book);

        // Find and set empty view on the ListView, so that it only shows when the list has 0 items.
        View emptyView = findViewById(R.id.empty_view);
        displayView.setEmptyView(emptyView);

        // setup an Adapter to create a list item for each row of pet data in the Cursor.
        //there is no pet date yet (until the loader finishes) so pass in null for the Cursor.
        bookCursorAdapter = new BookCursorAdapter(this, null, 0);
        displayView.setAdapter(bookCursorAdapter);

        //setup the  click listener
        displayView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                // create a new intent to go to EditActivity
                Intent intent = new Intent(CatalogActivity.this, EditActivity.class);

                // form the content URI that represents the specific book that was clicked on,
                //by appending the "id" (passed as input to this method) on CONTENT_URI
                Uri currentBookUri = ContentUris.withAppendedId(BooksContract.BooksEntry.CONTENT_URI, id);

                //set the uri on the data field of the intent
                intent.setData(currentBookUri);
                //launch EditorActivity to display the date for the current book
                startActivity(intent);

            }
        });

        //start loader
        getLoaderManager().initLoader(BOOK_LOADER, null, this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.action_insert_dummy_data:
                insertDummyData();
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                deleteAllBooks();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void insertDummyData() {

        ContentValues values = new ContentValues();
        values.put(BooksContract.BooksEntry.COLUMN_BOOK_TITLE, "Dummy book");
        values.put(BooksContract.BooksEntry.COLUMN_BOOK_PRICE, 25);
        values.put(BooksContract.BooksEntry.COLUMN_BOOK_QTY, 99);
        values.put(BooksContract.BooksEntry.COLUMN_BOOK_SUPPNAME, " BookSupplier");
        values.put(BooksContract.BooksEntry.COLUMN_BOOK_SUPPPHONE, 1234567);

        Uri insertUri = getContentResolver().insert(BooksContract.BooksEntry.CONTENT_URI, values);

    }

    // Helper method to delete all pets in the database.

    private void deleteAllBooks() {
        int rowsDeleted = getContentResolver().delete(BooksContract.BooksEntry.CONTENT_URI, null, null);
        Log.v("CatalogActivity", rowsDeleted + " rows deleted from database");
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // projection that specifies the columns we want to use, from the table
        String[] projection = {
                BaseColumns._ID,
                BooksContract.BooksEntry.COLUMN_BOOK_TITLE,
                BooksContract.BooksEntry.COLUMN_BOOK_PRICE,
                BooksContract.BooksEntry.COLUMN_BOOK_QTY
        };

        // this loader will execute the ContentProvider query method on a background thread
        return new CursorLoader(this,
                BooksContract.BooksEntry.CONTENT_URI,
                projection,
                null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
// update BookCursorAdapter with new cursor containing updated book data
        bookCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
//callback called when the data needs to be deleted
        bookCursorAdapter.swapCursor(null);
    }
}
