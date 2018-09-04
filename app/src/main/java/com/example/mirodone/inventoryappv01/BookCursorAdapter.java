package com.example.mirodone.inventoryappv01;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.mirodone.inventoryappv01.data.BooksContract;

public class BookCursorAdapter extends CursorAdapter {

    public BookCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, 0 /*flags*/);
    }

    /**
     * Makes a new blank list item view. No data is set (or bound) to the views yet.
     *
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already
     *                moved to the correct position.
     * @param parent  The parent to which the new view is attached to
     * @return the newly created list item view.
     */

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        // Find fields to populate in inflated template
        TextView titleView = (TextView) view.findViewById(R.id.list_title);
        TextView priceView = (TextView) view.findViewById(R.id.list_price);
        TextView qtyView = (TextView) view.findViewById(R.id.list_qty);

        Button sellButton = view.findViewById(R.id.btn_sell);

        // Extract properties from cursor
        String bookTitle = cursor.getString(cursor.getColumnIndexOrThrow("title"));
        String bookPrice = cursor.getString(cursor.getColumnIndexOrThrow("price"));
        String bookQty = cursor.getString(cursor.getColumnIndexOrThrow("qty"));

        // populate fields with extracted properties
        titleView.setText(bookTitle);
        priceView.setText(bookPrice);
        qtyView.setText(bookQty);

        //get current qty from the db

        final int idColumnIndex = cursor.getInt(cursor.getColumnIndex(BooksContract.BooksEntry._ID));
        int qtyColumnIndex = cursor.getColumnIndex(BooksContract.BooksEntry.COLUMN_BOOK_QTY);
        final int currentQty = Integer.valueOf(cursor.getString(qtyColumnIndex));


        //sell button listener
        sellButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentQty > 0) {
                    int newCurrentQty = currentQty - 1;
                    Uri qtyUri = ContentUris.withAppendedId(BooksContract.BooksEntry.CONTENT_URI, idColumnIndex);

                    ContentValues values = new ContentValues();
                    values.put(BooksContract.BooksEntry.COLUMN_BOOK_QTY, newCurrentQty);
                    context.getContentResolver().update(qtyUri, values, null, null);
                }
            }
        });

    }
}
