package com.example.android.inventoryapp;

/**
 * Created by SWEEKASH on 10-12-2018.
 */

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
import android.widget.Toast;

import com.example.android.inventoryapp.data.BookContract;

public class BookCursorAdapter extends CursorAdapter {

    /**
     * Constructs a new {@link BookCursorAdapter}.
     *
     * @param context The context
     * @param c       The cursor from which to get the data.
     */
    public BookCursorAdapter(Context context, Cursor c) {
        super(context, c);
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
        // Inflate a list item view using the layout specified in list_item.xml
        return LayoutInflater.from(context).inflate(R.layout.list, parent, false);
    }

    /**
     * This method binds the book data (in the current row pointed to by cursor) to the given
     * list item layout. For example, the name for the current book can be set on the name TextView
     * in the list item layout.
     *
     * @param view    Existing view, returned earlier by newView() method
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already moved to the
     *                correct row.
     */
    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {
        final int BookID = cursor.getInt(cursor.getColumnIndex(BookContract.BookEntry._ID));
        final int currentQuantity = cursor.getInt(cursor.getColumnIndex(BookContract.BookEntry.COLUMN_BOOK_QUANTITY));
        // Find individual views that we want to modify in the list item layout
        TextView nameTextView = (TextView) view.findViewById(R.id.prdName);
        TextView PriceTextView = (TextView) view.findViewById(R.id.prdPrice);

        // Find the columns of book attributes that we're interested in
        int nameColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_BOOK_NAME);
        int supplierColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_BOOK_SUPPLIER);

        // Read the book attributes from the Cursor for the current pet
        String bookName = cursor.getString(nameColumnIndex);
        String bookSupplier = cursor.getString(supplierColumnIndex);

        // Update the TextViews with the attributes for the current book
        nameTextView.setText(bookName);
        PriceTextView.setText(bookSupplier);
        final TextView txtQuantity = ((TextView) view.findViewById(R.id.prdQty));
        txtQuantity.setText(context.getString(R.string.quantityAvailableText) + " " + String.valueOf(currentQuantity));

        Button btnSale = (Button) view.findViewById(R.id.btnSale);
        btnSale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentQuantity > 0) {
                    // Decrementing the quantity.
                    int newQuantity = currentQuantity - 1;

                    // Creating URI for specific product for updating new Quantity.
                    Uri productUri = ContentUris.withAppendedId(BookContract.BookEntry.CONTENT_URI, BookID);

                    // Creating contentValue to update Quantity only.
                    ContentValues values = new ContentValues();
                    values.put(BookContract.BookEntry.COLUMN_BOOK_QUANTITY, newQuantity);

                    // Updating product by using contentResolver.
                    context.getContentResolver().update(productUri, values, null, new String[]{String.valueOf(BookID)});
                } else
                    Toast.makeText(context, context.getString(R.string.main_negative_quantity), Toast.LENGTH_SHORT).show();
            }
        });
    }
}

