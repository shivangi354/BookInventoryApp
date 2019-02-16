package com.example.android.inventoryapp;

/**
 * Created by SWEEKASH on 11-12-2018.
 */

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.inventoryapp.data.BookContract;


public class DetailActivity extends AppCompatActivity {

    private Uri productUri;
    private TextView NameTextView;
    private TextView PriceTextView;
    private TextView QuantityTextView;
    private TextView SupplierTextView;
    private TextView ContactTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // Receiving the URI sent from MainActivity.
        productUri = getIntent().getData();

        // Getting cursor for given URI.
        updateUT();
    }

    private void updateUT() {
        Cursor cursor = getContentResolver().query(productUri, null, null, null, null);
        try {
            if (cursor.moveToNext()) {
                NameTextView = findViewById(R.id.bookName);
                QuantityTextView = findViewById(R.id.prdQty);
                PriceTextView = findViewById(R.id.prdPrice);
                ContactTextView = findViewById(R.id.supContact);
                SupplierTextView = findViewById(R.id.supName);

                // Setting values to respective TextViews.
                NameTextView.setText(cursor.getString(cursor.getColumnIndex(BookContract.BookEntry.COLUMN_BOOK_NAME)));
                QuantityTextView.setText(cursor.getString(cursor.getColumnIndex(BookContract.BookEntry.COLUMN_BOOK_QUANTITY)));
                PriceTextView.setText(getString(R.string.rupeeSymbol) + " " + cursor.getString(cursor.getColumnIndex(BookContract.BookEntry.COLUMN_BOOK_PRICE)));
                SupplierTextView.setText(cursor.getString(cursor.getColumnIndex(BookContract.BookEntry.COLUMN_BOOK_SUPPLIER)));
                ContactTextView.setText(cursor.getString(cursor.getColumnIndex(BookContract.BookEntry.COLUMN_BOOK_CONTACT)));
            }
        } finally {
            // Closing cursor.
            cursor.close();
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        updateUT();
    }

    public void increment(View view) {
        int currentQuantity = Integer.valueOf(QuantityTextView.getText().toString());
        ContentValues contentValues = new ContentValues();

        // Extracting SharedPreferance value for increment.
        String incrementValue = PreferenceManager.getDefaultSharedPreferences(DetailActivity.this).getString(getString(R.string.settings_default_quantityIncrement_key), getString(R.string.settings_default_quantityIncrement_value));
        contentValues.put(BookContract.BookEntry.COLUMN_BOOK_QUANTITY, currentQuantity + Integer.valueOf(incrementValue));

        // Updating database.
        getContentResolver().update(productUri, contentValues, null, new String[]{BookContract.BookEntry._ID});

        // updating new quantity to TextView.
        QuantityTextView.setText(String.valueOf(currentQuantity + Integer.valueOf(incrementValue)));
    }

    public void decrement(View view) {
        int currentQuantity = Integer.valueOf(QuantityTextView.getText().toString());
        if (currentQuantity != 0) {
            // Extracting SharedPreferance value for increment.
            String decrementValue = PreferenceManager.getDefaultSharedPreferences(DetailActivity.this).getString(getString(R.string.settings_default_quantityIncrement_key), getString(R.string.settings_default_quantityIncrement_value));

            ContentValues contentValues = new ContentValues();

            if ((currentQuantity - Integer.valueOf(decrementValue)) < 0)
                contentValues.put(BookContract.BookEntry.COLUMN_BOOK_QUANTITY, 0);
            else
                contentValues.put(BookContract.BookEntry.COLUMN_BOOK_QUANTITY, currentQuantity - Integer.valueOf(decrementValue));

            // Updating value in database.
            getContentResolver().update(productUri, contentValues, null, null);
            QuantityTextView.setText(String.valueOf(contentValues.getAsString(BookContract.BookEntry.COLUMN_BOOK_QUANTITY)));
        } else {
            Toast.makeText(DetailActivity.this, getString(R.string.detail_negative_quantity), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.action_delete:
                showDeleteConfirmationDialog();
                break;
            case R.id.action_edit:
                Intent i = new Intent(DetailActivity.this, EditorActivity.class);
                i.setData(productUri);
                startActivity(i);
                break;
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                break;
        }
        return true;
    }

    private void showDeleteConfirmationDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.delete_confirm_text));
        // Handling positive response.
        builder.setPositiveButton(getString(R.string.delete_confirm_positive), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                getContentResolver().delete(productUri, null, null);
                finish();
            }
        });
        // Handling Negative Response.
        builder.setNegativeButton(R.string.delete_confirm_negative, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return true;
    }

    // Handling order now button click.
    public void orderNow(View view) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + ContactTextView.getText().toString()));
        startActivity(intent);
    }
}
