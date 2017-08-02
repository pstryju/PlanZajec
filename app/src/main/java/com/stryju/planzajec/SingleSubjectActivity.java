package com.stryju.planzajec;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import static com.stryju.planzajec.DatabaseHelper.getSeparator;
import static com.stryju.planzajec.DatabaseHelper.getDuration;

public class SingleSubjectActivity extends AppCompatActivity {
    TextView dayTextView, nameTextView, roomTextView, typeTextView, startTimeTextView, endTimeTextView, durationTextView, lecturerTextView;
    int mDayId, mPositionId, dbRowId;
    DatabaseHelper mDbHelper;
    SQLiteDatabase db;
    Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_subject);

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                mDayId = -1;
                mPositionId = -1;
            } else {
                mDayId = extras.getInt("day");
                mPositionId = extras.getInt("position");
            }
        }

        dayTextView = (TextView) findViewById(R.id.single_subject_day_textview);
        nameTextView = (TextView) findViewById(R.id.subject_name_textview);
        roomTextView = (TextView) findViewById(R.id.subject_room_textview);
        typeTextView = (TextView) findViewById(R.id.singleSubjectTypeTextView);
        startTimeTextView = (TextView) findViewById(R.id.singleSubjectStartTimeTextView);
        endTimeTextView = (TextView) findViewById(R.id.singleSubjectEndTimeTextView);
        durationTextView = (TextView) findViewById(R.id.singleSubjectDurationTextView);
        lecturerTextView = (TextView) findViewById(R.id.singleSubjectLecturerTextView);
        dayTextView.setText(getResources().getStringArray(R.array.daysOfTheWeek)[mDayId]);

        prepareDatabase();
        updateViews();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.single_subject_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.remove:
                showDeleteDialog();
                return true;
            case R.id.edit:
                editPosition();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        updateViews();
    }

    private void prepareDatabase() {
        mDbHelper = new DatabaseHelper(this);
        db = mDbHelper.getReadableDatabase();
    }

    private void queryDatabase() {
        String[] projection = {
                DatabaseContract.DBEntry._ID,
                DatabaseContract.DBEntry.COLUMN_NAME_ACTIVITY,
                DatabaseContract.DBEntry.COLUMN_NAME_ROOM,
                DatabaseContract.DBEntry.COLUMN_NAME_DAY,
                DatabaseContract.DBEntry.COLUMN_NAME_TYPE,
                DatabaseContract.DBEntry.COLUMN_NAME_START_HOUR,
                DatabaseContract.DBEntry.COLUMN_NAME_START_MINUTE,
                DatabaseContract.DBEntry.COLUMN_NAME_END_HOUR,
                DatabaseContract.DBEntry.COLUMN_NAME_END_MINUTE,
                DatabaseContract.DBEntry.COLUMN_NAME_LECTURER,
        };
        String selection = DatabaseContract.DBEntry.COLUMN_NAME_DAY + " = ? ";
        String[] selectionArgs = { Integer.toString(mDayId) };
        String sortOrder = DatabaseContract.DBEntry.COLUMN_NAME_START_HOUR + " ASC";
        cursor = db.query(DatabaseContract.DBEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
        cursor.moveToPosition(mPositionId);
        dbRowId = cursor.getInt(cursor.getColumnIndex(DatabaseContract.DBEntry._ID));
    }

    private void updateViews() {
        queryDatabase();

        int startTimeHour = cursor.getInt(cursor.getColumnIndex(DatabaseContract.DBEntry.COLUMN_NAME_START_HOUR));
        int startTimeMinutes = cursor.getInt(cursor.getColumnIndex(DatabaseContract.DBEntry.COLUMN_NAME_START_MINUTE));
        int endTimeHour = cursor.getInt(cursor.getColumnIndex(DatabaseContract.DBEntry.COLUMN_NAME_END_HOUR));
        int endTimeMinutes = cursor.getInt(cursor.getColumnIndex(DatabaseContract.DBEntry.COLUMN_NAME_END_MINUTE));

        nameTextView.setText(cursor.getString(cursor.getColumnIndex(DatabaseContract.DBEntry.COLUMN_NAME_ACTIVITY)));
        roomTextView.setText(cursor.getString(cursor.getColumnIndex(DatabaseContract.DBEntry.COLUMN_NAME_ROOM)));
        typeTextView.setText(getResources().getStringArray(R.array.subjectTypes)[cursor.getInt(cursor.getColumnIndex(DatabaseContract.DBEntry.COLUMN_NAME_TYPE))]);
        startTimeTextView.setText(Integer.toString(startTimeHour) + getSeparator(startTimeMinutes) + Integer.toString(startTimeMinutes));
        endTimeTextView.setText(Integer.toString(endTimeHour) + getSeparator(endTimeMinutes) + Integer.toString(endTimeMinutes));
        durationTextView.setText(getDuration(cursor));
        lecturerTextView.setText(cursor.getString(cursor.getColumnIndex(DatabaseContract.DBEntry.COLUMN_NAME_LECTURER)));
    }


    private void showDeleteDialog() {
        new AlertDialog.Builder(this)
                .setMessage(getResources().getString(R.string.delete_desc))
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                db.delete(DatabaseContract.DBEntry.TABLE_NAME, "_ID = ? ", new String[] {Integer.toString(dbRowId)});
                finish();
            }
        })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert).show();
    }

    private void editPosition() {
        Intent intent = new Intent(this, AddEntryActivity.class);
        intent.putExtra("dbRowId", dbRowId);
        startActivity(intent);
    }
}
