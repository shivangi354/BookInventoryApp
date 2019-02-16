package com.example.android.inventoryapp;


import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.android.inventoryapp.data.BookContract.BookEntry;

public class MainActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {
    private final int Book_Loader = 0;
    private BookCursorAdapter mCursorAdapter;
    private ListView list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        list = (ListView) findViewById(R.id.list);
        mCursorAdapter = new BookCursorAdapter(this, null);
        list.setEmptyView(findViewById(R.id.empty_view));

        // Handling item clicks.
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent i = new Intent(MainActivity.this, DetailActivity.class);
                i.setData(ContentUris.withAppendedId(BookEntry.CONTENT_URI, id));
                startActivity(i);
            }
        });

        list.setAdapter(mCursorAdapter);
        list.setDescendantFocusability(ListView.FOCUS_BLOCK_DESCENDANTS);
        // Setup FAB to open EditorActivity
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        // Kick off the loader
        getLoaderManager().initLoader(Book_Loader, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.action_insert_dummy_data:
                insertBook();
                break;
            case R.id.action_delete_all_entries:
                // Displaying confirmation dialog to confirm delete all.
                showDeleteConfirmationDialog();
                break;
        }
        return true;
    }

    private void deleteALlData() {
        int i = getContentResolver().delete(BookEntry.CONTENT_URI, null, null);
        Toast.makeText(this, i + " " + getString(R.string.main_delete_all), Toast.LENGTH_SHORT).show();
    }

    private void showDeleteConfirmationDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.delete_confirm_text));
        // Handling positive response.
        builder.setPositiveButton(getString(R.string.delete_confirm_positive), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                deleteALlData();
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

    private void insertBook() {

        ContentValues values = new ContentValues();
        values.put(BookEntry.COLUMN_BOOK_NAME, "Android Development");
        values.put(BookEntry.COLUMN_BOOK_PRICE, 100);
        values.put(BookEntry.COLUMN_BOOK_QUANTITY, 5);
        values.put(BookEntry.COLUMN_BOOK_SUPPLIER, "Shivangi");
        values.put(BookEntry.COLUMN_BOOK_CONTACT, 987456324);
        getContentResolver().insert(BookEntry.CONTENT_URI, values);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {


        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,   // Parent activity context
                BookEntry.CONTENT_URI,   // Provider content URI to query
                null,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                BookEntry._ID + " desc");                  // Default sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // Callback called when the data needs to be deleted
        mCursorAdapter.swapCursor(null);
    }
}