package com.zoromatic.widgets;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class LocationsEdit extends Activity {

    private EditText mTitleText;
    private EditText mBodyText;
    private Long mRowId;
    private SQLiteDbAdapter mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.locations_edit);

        mTitleText = (EditText) findViewById(R.id.title);
        mBodyText = (EditText) findViewById(R.id.body);

        Button confirmButton = (Button) findViewById(R.id.confirm);

        mRowId = (savedInstanceState == null) ? null :
                (Long) savedInstanceState.getSerializable(SQLiteDbAdapter.KEY_ROWID);
        if (mRowId == null) {
            Bundle extras = getIntent().getExtras();
            mRowId = extras != null ? extras.getLong(SQLiteDbAdapter.KEY_ROWID)
                    : null;
        }

        mDbHelper = new SQLiteDbAdapter(this);
        mDbHelper.open();
        populateFields();
        mDbHelper.close();

        confirmButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                setResult(RESULT_OK);
                finish();
            }

        });
    }

    @SuppressWarnings("deprecation")
    private void populateFields() {
        if (mRowId != null) {
            Cursor location = mDbHelper.fetchLocation(mRowId);
            startManagingCursor(location);
            mTitleText.setText(location.getString(
                    location.getColumnIndexOrThrow(SQLiteDbAdapter.KEY_NAME)));
            mBodyText.setText(location.getString(
                    location.getColumnIndexOrThrow(SQLiteDbAdapter.KEY_LOCATION_ID)));
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        saveState();
        outState.putSerializable(SQLiteDbAdapter.KEY_ROWID, mRowId);
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveState();
    }

    @Override
    protected void onResume() {
        super.onResume();
        populateFields();
    }

    private void saveState() {
        /*String title = mTitleText.getText().toString();
        String body = mBodyText.getText().toString();

        if (mRowId == null) {
        	if ((title != null && title.length() > 0) || (body != null && body.length() > 0)) {
	            long id = mDbHelper.createLocation(title, body);
	            if (id > 0) {
	                mRowId = id;
	            }
        	}
        } else {
            mDbHelper.updateNote(mRowId, title, body);
        }*/
    }
}
