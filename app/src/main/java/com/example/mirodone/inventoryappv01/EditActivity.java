package com.example.mirodone.inventoryappv01;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.mirodone.inventoryappv01.data.BooksContract;


public class EditActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int EXISTING_BOOK_LOADER = 0;
    private EditText titleEditText;
    private EditText priceEditText;
    private EditText qtyEditText;
    private EditText suppNameEditText;
    private EditText suppPhoneEditText;

    private Uri currentBookUri;
    private boolean bookChanged = false;

    // OnTouchListener that listens for any user touches on a View, implying that they are modifying
    // the view, and we change the bookChanged boolean to true.

    private View.OnTouchListener touchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            bookChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        // Examine the intent used to launch the activity to see if we create or edit the book
        Intent intent = getIntent();
        currentBookUri = intent.getData();

        if (currentBookUri == null) {
            setTitle(getString(R.string.editor_activity_title_new_book));
            invalidateOptionsMenu();
        } else {
            //Existing book, so we change the app bar to " Edit Book"
            setTitle(getString(R.string.editor_activity_title_edit_book));
            // Initialize a loader to read book data from the database
            // and display the current values in the editActivity
            getLoaderManager().initLoader(EXISTING_BOOK_LOADER, null, this);
        }

        // Find all relevant views that we will need to read user input from
        titleEditText = findViewById(R.id.edit_book_title);
        priceEditText = findViewById(R.id.edit_book_price);
        qtyEditText = findViewById(R.id.edit_book_qty);
        suppNameEditText = findViewById(R.id.edit_supp_name);
        suppPhoneEditText = findViewById(R.id.edit_supp_phone);
        Button increaseQty = findViewById(R.id.increase_button);
        Button decreaseQty = findViewById(R.id.decrease_button);
        Button order = findViewById(R.id.order_button);

        // Setup OnTouchListeners on all the input fields, so we can determine if the user
        // has touched or modified them. This will let us know if there are unsaved changes
        // or not, if the user tries to leave the editor without saving.

        titleEditText.setOnTouchListener(touchListener);
        priceEditText.setOnTouchListener(touchListener);
        qtyEditText.setOnTouchListener(touchListener);
        suppNameEditText.setOnTouchListener(touchListener);
        suppPhoneEditText.setOnTouchListener(touchListener);
        increaseQty.setOnTouchListener(touchListener);
        decreaseQty.setOnTouchListener(touchListener);


        increaseQty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String currentQty = qtyEditText.getText().toString();
                if (TextUtils.isEmpty(currentQty)) {
                    qtyEditText.setText("0");
                } else {
                    int plusQty = Integer.parseInt(qtyEditText.getText().toString().trim());
                    plusQty++;
                    qtyEditText.setText(String.valueOf(plusQty));
                }

            }
        });


        decreaseQty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String currentQty = qtyEditText.getText().toString();
                if (TextUtils.isEmpty(currentQty)) {
                    qtyEditText.setText("0");
                } else {

                    int minusQty = Integer.parseInt(qtyEditText.getText().toString().trim());
                    if (minusQty > 0) {
                        minusQty--;
                        qtyEditText.setText(String.valueOf(minusQty));
                    } else {
                        Toast.makeText(EditActivity.this, "Quantity cannot be negative !", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });


        order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = suppPhoneEditText.getText().toString().trim();
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + phone));
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
            }
        });


    }

    private void showUnsavedChangesDialog(DialogInterface.OnClickListener discardButtonClickListener
    ) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (dialogInterface != null) {
                    dialogInterface.dismiss();
                }
            }
        });

        //create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

    private void saveBook() {

        // Read from input fields
        // Use trim to eliminate leading or trailing white space
        String titleInput = titleEditText.getText().toString().trim();
        String priceInput = priceEditText.getText().toString().trim();
        String qtyInput = qtyEditText.getText().toString().trim();
        String supplierNameInput = suppNameEditText.getText().toString().trim();
        String supplierPhoneInput = suppPhoneEditText.getText().toString().trim();

        // Create a ContentValue obj where the column names are the keys,
        // and book inputs from editor are the values
        ContentValues values = new ContentValues();
        values.put(BooksContract.BooksEntry.COLUMN_BOOK_TITLE, titleInput);

        int price = 1;
        if (!TextUtils.isEmpty(priceInput)) {
            price = Integer.parseInt(priceInput);
        }
        values.put(BooksContract.BooksEntry.COLUMN_BOOK_PRICE, price);
        int qty = 1;
        if (!TextUtils.isEmpty(qtyInput)) {
            qty = Integer.parseInt(qtyInput);
        }
        values.put(BooksContract.BooksEntry.COLUMN_BOOK_QTY, qty);
        values.put(BooksContract.BooksEntry.COLUMN_BOOK_SUPPNAME, supplierNameInput);
        values.put(BooksContract.BooksEntry.COLUMN_BOOK_SUPPPHONE, supplierPhoneInput);

        // if the String fields are empty, return to activity without adding anything in the db.

        if (currentBookUri == null && TextUtils.isEmpty(titleInput)
                ) {
            // Since no fields were modified, we can return early without creating a new book.
            // No need to create ContentValues and no need to do any ContentProvider operations.
            Toast.makeText(this, getString(R.string.toast_no_save), Toast.LENGTH_SHORT).show();
            return;
        }

        //determine if this is a new or existing book by checking if currentBookUri is null or not
        if (currentBookUri == null) {
            //this is for new book, so we insert book
            Uri newUri = getContentResolver().insert(BooksContract.BooksEntry.CONTENT_URI, values);

            if (newUri == null) {
                // If the new content URI is null, then there was an error with insertion.
                Toast.makeText(this, getString(R.string.toast_error),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the insertion was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.toast_saved),
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            // this is for existing book, so we update the book with the content URI currentBookUri
            // and pass the ContentValues
            int rowsAffected = getContentResolver().update(currentBookUri, values, null, null);

            Log.d("EditActivity", ">>>>>>>>>>" + currentBookUri);

            // Show a toast message depending on whether or not the update was successful.
            if (rowsAffected == 0) {
                // If no rows were affected, then there was an error with the update.
                Toast.makeText(this, getString(R.string.toast_edit_error),
                        +Toast.LENGTH_SHORT).show();

            } else {
                // Otherwise, the update was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.toast_edit_saved),
                        +Toast.LENGTH_SHORT).show();

            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_edit.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_edit, menu);
        return true;
    }

    /**
     * This method is called after invalidateOptionsMenu(),
     * so that the* menu can be updated (some menu items can be hidden or made visible).
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If this adds a new book, hide the "Delete" menu item.
        if (currentBookUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                saveBook();
                finish();
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:

                if (!bookChanged) {
                    // Navigate back to parent activity (CatalogActivity)
                    NavUtils.navigateUpFromSameTask(EditActivity.this);
                    return true;
                }
                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                // Create a click listener to handle the user confirming that
                // changes should be discarded.
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(EditActivity.this);
                            }
                        };

                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        // If book hasn't changed, continue with handling back button press
        if (!bookChanged) {
            super.onBackPressed();
            return;
        }
        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };

        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        String[] projection = {
                BooksContract.BooksEntry._ID,
                BooksContract.BooksEntry.COLUMN_BOOK_TITLE,
                BooksContract.BooksEntry.COLUMN_BOOK_PRICE,
                BooksContract.BooksEntry.COLUMN_BOOK_QTY,
                BooksContract.BooksEntry.COLUMN_BOOK_SUPPNAME,
                BooksContract.BooksEntry.COLUMN_BOOK_SUPPPHONE};

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,   // Parent activity context
                currentBookUri,         // Query the content URI for the current pet
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        if (cursor == null || cursor.getCount() < 1) {
            return;
        }
        // Proceed with moving to the first row of the cursor and reading data from it
        // (This should be the only row in the cursor)
        if (cursor.moveToFirst()) {
            int titleColumnIndex = cursor.getColumnIndex(BooksContract.BooksEntry.COLUMN_BOOK_TITLE);
            int priceColumnIndex = cursor.getColumnIndex(BooksContract.BooksEntry.COLUMN_BOOK_PRICE);
            int qtyColumnIndex = cursor.getColumnIndex(BooksContract.BooksEntry.COLUMN_BOOK_QTY);
            int suppnColumnIndex = cursor.getColumnIndex(BooksContract.BooksEntry.COLUMN_BOOK_SUPPNAME);
            int supppColumnIndex = cursor.getColumnIndex(BooksContract.BooksEntry.COLUMN_BOOK_SUPPPHONE);

            // Extract out the value from the Cursor for the given column index
            String title = cursor.getString(titleColumnIndex);
            int price = cursor.getInt(priceColumnIndex);
            int qty = cursor.getInt(qtyColumnIndex);
            String sName = cursor.getString(suppnColumnIndex);
            //int sPhone = cursor.getInt(supppColumnIndex);
            String sPhone = cursor.getString(supppColumnIndex);

            // Update the views on the screen with the values from the database
            titleEditText.setText(title);
            priceEditText.setText(Integer.toString(price));
            qtyEditText.setText(Integer.toString(qty));
            suppNameEditText.setText(sName);
            suppPhoneEditText.setText(sPhone);

        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        // If the loader is invalidated, clear out all the data from the input fields.
        titleEditText.setText("");
        priceEditText.setText("");
        qtyEditText.setText("");
        suppNameEditText.setText("");
        suppPhoneEditText.setText("");

    }

    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete book.
                deleteBook();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing book.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    // method for delete a book from db
    private void deleteBook() {
        // Only perform the delete if this is an existing book.
        if (currentBookUri != null) {
            // Call the ContentResolver to delete book at the given content URI.
            // Pass in null for the selection and selection args because the currentBookUri
            // content URI already identifies the book that we want.
            int rowsDeleted = getContentResolver().delete(currentBookUri, null, null);

            // Show a toast message depending on whether or not the delete was successful.
            if (rowsDeleted == 0) {
                // If no rows were deleted, then there was an error with the delete.
                Toast.makeText(this, getString(R.string.editor_delete_book_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_delete_book_successful),
                        Toast.LENGTH_SHORT).show();
            }

        }
        // Close the activity
        finish();
    }
}
