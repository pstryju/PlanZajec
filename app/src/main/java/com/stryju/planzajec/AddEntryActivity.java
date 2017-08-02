package com.stryju.planzajec;

import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import static com.stryju.planzajec.DatabaseHelper.getSeparator;

public class AddEntryActivity extends AppCompatActivity {


    DatabaseHelper mDbHelper;
    SQLiteDatabase db;
    boolean editing;
    int dbRowId;
    Cursor cursor;
    TimePickerDialog.OnTimeSetListener startTimePickerListener, endTimePickerListener;
    int subjectStartHour, subjectStartMinute, subjectEndHour, subjectEndMinute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_entry);
        mDbHelper = new DatabaseHelper(this);
        db = mDbHelper.getWritableDatabase();
        Button saveButton = (Button) findViewById(R.id.save_button);

        Bundle extras = getIntent().getExtras();
        if(extras != null && extras.containsKey("dbRowId")) {
                editing = true;
                dbRowId = extras.getInt("dbRowId");
        }
        else
            editing = false;

        final EditText activityEditText = (EditText) findViewById(R.id.activity_edittext);
        final EditText roomEditText = (EditText) findViewById(R.id.room_edittext);
        final EditText lecturerEditText = (EditText) findViewById(R.id.lecturer_edittext);
        final TextView startHourTextView = (TextView) findViewById(R.id.startTimeTextView);
        final TextView endHourTextView = (TextView) findViewById(R.id.endTimeTextView);
        final Spinner daySpinner = (Spinner) findViewById(R.id.day_spinner);
        ArrayAdapter<CharSequence> daySpinnerAdapter = ArrayAdapter.createFromResource(this, R.array.daysOfTheWeek, android.R.layout.simple_spinner_item);
        daySpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        if (daySpinner != null)
            daySpinner.setAdapter(daySpinnerAdapter);
        final Spinner typeSpinner = (Spinner) findViewById(R.id.type_spinner);
        ArrayAdapter<CharSequence> typeSpinnerAdapter = ArrayAdapter.createFromResource(this, R.array.subjectTypes, android.R.layout.simple_spinner_item);
        typeSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        if(typeSpinner != null)
            typeSpinner.setAdapter(typeSpinnerAdapter);

        if(extras != null && extras.containsKey("day") && daySpinner != null) {
            daySpinner.setSelection(extras.getInt("day"));
        }
        startTimePickerListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet (TimePicker view,int hourOfDay, int minute) {
                subjectStartHour = hourOfDay;
                subjectStartMinute = minute;
                if(startHourTextView != null)
                    startHourTextView.setText(Integer.toString(subjectStartHour) + getSeparator(subjectStartMinute) + Integer.toString(subjectStartMinute));
            }
        };
        endTimePickerListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet (TimePicker view,int hourOfDay, int minute) {
                subjectEndHour = hourOfDay;
                subjectEndMinute = minute;
                if(endHourTextView != null)
                    endHourTextView.setText(Integer.toString(subjectEndHour) + getSeparator(subjectEndMinute) + Integer.toString(subjectEndMinute));
            }
        };

        if(startHourTextView != null) {
            startHourTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TimePickerDialog timePickerDialog = new TimePickerDialog(AddEntryActivity.this, startTimePickerListener, subjectStartHour, subjectStartMinute, true);
                    timePickerDialog.show();
                }
            });
        }
        if(endHourTextView != null) {
            endHourTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TimePickerDialog timePickerDialog = new TimePickerDialog(AddEntryActivity.this, endTimePickerListener, subjectEndHour, subjectEndMinute, true);
                    timePickerDialog.show();
                }
            });
        }

        //If editing query db for edited data and load it to UI
        if(editing) {
            queryDatabase();
            if(activityEditText != null)
                activityEditText.setText(cursor.getString(cursor.getColumnIndex(DatabaseContract.DBEntry.COLUMN_NAME_ACTIVITY)));
            if(roomEditText != null)
                roomEditText.setText(cursor.getString(cursor.getColumnIndex(DatabaseContract.DBEntry.COLUMN_NAME_ROOM)));
            if(lecturerEditText != null)
                lecturerEditText.setText(cursor.getString(cursor.getColumnIndex(DatabaseContract.DBEntry.COLUMN_NAME_LECTURER)));
            subjectStartHour = cursor.getInt(cursor.getColumnIndex(DatabaseContract.DBEntry.COLUMN_NAME_START_HOUR));
            subjectStartMinute = cursor.getInt(cursor.getColumnIndex(DatabaseContract.DBEntry.COLUMN_NAME_START_MINUTE));
            subjectEndHour = cursor.getInt(cursor.getColumnIndex(DatabaseContract.DBEntry.COLUMN_NAME_END_HOUR));
            subjectEndMinute = cursor.getInt(cursor.getColumnIndex(DatabaseContract.DBEntry.COLUMN_NAME_END_MINUTE));
            if(startHourTextView != null)
                startHourTextView.setText(Integer.toString(subjectStartHour) + getSeparator(subjectStartMinute) + Integer.toString(subjectStartMinute));
            if(endHourTextView != null)
                endHourTextView.setText(Integer.toString(subjectEndHour) + getSeparator(subjectEndMinute) + Integer.toString(subjectEndMinute));
            if(daySpinner != null)
                daySpinner.setSelection(cursor.getInt(cursor.getColumnIndex(DatabaseContract.DBEntry.COLUMN_NAME_DAY)));
            if(typeSpinner != null)
                typeSpinner.setSelection(cursor.getInt(cursor.getColumnIndex(DatabaseContract.DBEntry.COLUMN_NAME_TYPE)));
        }

        if(saveButton != null)
            saveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ContentValues values = new ContentValues();
                    if(activityEditText != null)
                        values.put(DatabaseContract.DBEntry.COLUMN_NAME_ACTIVITY, activityEditText.getText().toString());
                    if(roomEditText != null)
                        values.put(DatabaseContract.DBEntry.COLUMN_NAME_ROOM, roomEditText.getText().toString());
                    if(daySpinner != null)
                        values.put(DatabaseContract.DBEntry.COLUMN_NAME_DAY, daySpinner.getSelectedItemId());
                    if(typeSpinner != null)
                        values.put(DatabaseContract.DBEntry.COLUMN_NAME_TYPE, typeSpinner.getSelectedItemId());
                    values.put(DatabaseContract.DBEntry.COLUMN_NAME_START_HOUR, Integer.toString(subjectStartHour));
                    values.put(DatabaseContract.DBEntry.COLUMN_NAME_START_MINUTE, Integer.toString(subjectStartMinute));
                    values.put(DatabaseContract.DBEntry.COLUMN_NAME_END_HOUR, Integer.toString(subjectEndHour));
                    values.put(DatabaseContract.DBEntry.COLUMN_NAME_END_MINUTE, Integer.toString(subjectEndMinute));
                    if(lecturerEditText != null)
                        values.put(DatabaseContract.DBEntry.COLUMN_NAME_LECTURER, lecturerEditText.getText().toString());
                    if(editing)
                        db.update(DatabaseContract.DBEntry.TABLE_NAME, values, "_id=" + dbRowId, null);
                    else
                        db.insert(DatabaseContract.DBEntry.TABLE_NAME, null, values);
                    finish();
                    }
                });

    }

    //Query DB by row _ID
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
        String selection = DatabaseContract.DBEntry._ID + " = ? ";
        String[] selectionArgs = { Integer.toString(dbRowId) };
        String sortOrder = DatabaseContract.DBEntry.COLUMN_NAME_START_HOUR + " ASC";
        cursor = db.query(DatabaseContract.DBEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
        cursor.moveToFirst();
    }




}
