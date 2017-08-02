package com.stryju.planzajec;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.CursorAdapter;
import android.support.v7.app.AlertDialog;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import static com.stryju.planzajec.DatabaseHelper.getSeparator;

public class SingleDay extends Fragment {

    public static final String ARG_DAY = "dayId";
    int mDayId;
    ListView singleDayListView;
    DayCursorAdapter adapter;
    DatabaseHelper mDbHelper;
    Cursor cursor;
    SQLiteDatabase db;

    public SingleDay() {}

    public static SingleDay newInstance(int param1) {
        SingleDay fragment = new SingleDay();
        Bundle args = new Bundle();
        args.putInt(ARG_DAY, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDayId = getArguments().getInt(ARG_DAY);
        mDbHelper = new DatabaseHelper(getContext());
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
        };
        String selection = DatabaseContract.DBEntry.COLUMN_NAME_DAY + " = ?";
        String[] selectionArgs = { Integer.toString(mDayId) };
        String sortOrder = DatabaseContract.DBEntry.COLUMN_NAME_START_HOUR + " ASC";
        cursor = db.query(DatabaseContract.DBEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_single_day, container, false);

        TextView singleDayTextView = (TextView) view.findViewById(R.id.single_day_textview);
        singleDayTextView.setText(getResources().getStringArray(R.array.daysOfTheWeek)[mDayId]);


        queryDatabase();

        singleDayListView = (ListView) view.findViewById(R.id.single_day_listview);
        adapter = new DayCursorAdapter(getContext(), cursor);
        singleDayListView.setAdapter(adapter);
        singleDayListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3)
            {
                Intent intent = new Intent(getContext(), SingleSubjectActivity.class);
                intent.putExtra("position", position);
                intent.putExtra("day", mDayId);
                startActivity(intent);

            }
        });
        registerForContextMenu(singleDayListView);

        return view;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.subject_item_context, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.action_edit:
                Intent intent = new Intent(getActivity(), AddEntryActivity.class);
                Cursor cursor = adapter.getCursor();
                cursor.moveToPosition(info.position);
                intent.putExtra("dbRowId", cursor.getInt(cursor.getColumnIndex(DatabaseContract.DBEntry._ID)));
                startActivity(intent);
                return true;
            case R.id.action_delete:
                showDeleteDialog(info.position);
                return true;
            default:
                return super.onContextItemSelected(item);
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        new LoadTask().execute();
    }

    private void showDeleteDialog(final int position) {
        new AlertDialog.Builder(getContext())
                .setMessage(getResources().getString(R.string.delete_desc))
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Cursor cursor = adapter.getCursor();
                        cursor.moveToPosition(position);
                        int dbRowId = cursor.getInt(cursor.getColumnIndex(DatabaseContract.DBEntry._ID));
                        db.delete(DatabaseContract.DBEntry.TABLE_NAME, "_ID = ? ", new String[] {Integer.toString(dbRowId)});
                        new LoadTask().execute();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert).show();
    }

    /*public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }*/

    /*public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }*/

    private class DayCursorAdapter extends CursorAdapter {

        DayCursorAdapter(Context context, Cursor cursor) {
            super(context, cursor, 0);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            return LayoutInflater.from(context).inflate(R.layout.single_list_item, parent, false);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            int startHour = cursor.getInt(cursor.getColumnIndex(DatabaseContract.DBEntry.COLUMN_NAME_START_HOUR));
            int startMinutes = cursor.getInt(cursor.getColumnIndex(DatabaseContract.DBEntry.COLUMN_NAME_START_MINUTE));
            int endHour = cursor.getInt(cursor.getColumnIndex(DatabaseContract.DBEntry.COLUMN_NAME_END_HOUR));
            int endMinutes = cursor.getInt(cursor.getColumnIndex(DatabaseContract.DBEntry.COLUMN_NAME_END_MINUTE));
            int type = cursor.getInt(cursor.getColumnIndex(DatabaseContract.DBEntry.COLUMN_NAME_TYPE));
            LinearLayout itemLayout = (LinearLayout) view.findViewById(R.id.subjectListItem);
            TextView subjectName = (TextView) view.findViewById(R.id.activity_textview);
            TextView subjectRoom = (TextView) view.findViewById(R.id.room_textview);
            TextView subjectType = (TextView) view.findViewById(R.id.typeTextView);
            TextView subjectStartTime = (TextView) view.findViewById(R.id.startTimeTextView);
            TextView subjectEndTime = (TextView) view.findViewById(R.id.endTimeTextView);

            itemLayout.setBackgroundColor(Color.parseColor(getResources().getStringArray(R.array.typesColors)[type]));
            /*ViewGroup.LayoutParams params=itemLayout.getLayoutParams();
            params.height=getDurationInMinutes(mCursor) * 10;
            itemLayout.setLayoutParams(params);*/
            subjectName.setText(cursor.getString(cursor.getColumnIndex(DatabaseContract.DBEntry.COLUMN_NAME_ACTIVITY)));
            subjectRoom.setText(cursor.getString(cursor.getColumnIndex(DatabaseContract.DBEntry.COLUMN_NAME_ROOM)));
            subjectType.setText(getResources().getStringArray(R.array.subjectTypes)[cursor.getInt(cursor.getColumnIndex(DatabaseContract.DBEntry.COLUMN_NAME_TYPE))]);
            subjectStartTime.setText(Integer.toString(startHour) + getSeparator(startMinutes) + Integer.toString(startMinutes));
            subjectEndTime.setText(Integer.toString(endHour) + getSeparator(endMinutes) + Integer.toString(endMinutes));
        }
    }

    private class LoadTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            queryDatabase();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            adapter.changeCursor(cursor);
            adapter.notifyDataSetChanged();
            super.onPostExecute(aVoid);
        }
    }


}
