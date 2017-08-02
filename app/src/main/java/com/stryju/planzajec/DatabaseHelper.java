package com.stryju.planzajec;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.stryju.planzajec.DatabaseContract.DBEntry;

class DatabaseHelper extends SQLiteOpenHelper {

    private static final String TEXT_TYPE = " TEXT";
    private static final String INTEGER_TYPE = " INTEGER";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + DBEntry.TABLE_NAME + " (" +
                    DBEntry._ID + " INTEGER PRIMARY KEY," +
                    DBEntry.COLUMN_NAME_ACTIVITY + TEXT_TYPE + COMMA_SEP +
                    DBEntry.COLUMN_NAME_ROOM + TEXT_TYPE + COMMA_SEP +
                    DBEntry.COLUMN_NAME_DAY + INTEGER_TYPE + COMMA_SEP +
                    DBEntry.COLUMN_NAME_TYPE + INTEGER_TYPE + COMMA_SEP +
                    DBEntry.COLUMN_NAME_START_HOUR + INTEGER_TYPE + COMMA_SEP +
                    DBEntry.COLUMN_NAME_START_MINUTE + INTEGER_TYPE + COMMA_SEP+
                    DBEntry.COLUMN_NAME_END_HOUR + INTEGER_TYPE + COMMA_SEP +
                    DBEntry.COLUMN_NAME_END_MINUTE + INTEGER_TYPE + COMMA_SEP +
                    DBEntry.COLUMN_NAME_LECTURER + TEXT_TYPE + " )";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + DBEntry.TABLE_NAME;

    private static final int DATABASE_VERSION = 5;
    private static final String DATABASE_NAME = "ActivityList.db";

    DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_DELETE_ENTRIES);
        db.execSQL(SQL_CREATE_ENTRIES);
        db.execSQL("INSERT INTO " + DBEntry.TABLE_NAME + " VALUES (0, 'Architektura Systemów Komputerowych', 'A13', 0, 0, 8, 30, 10, 00, 'Dr inż. Szustak Łukasz');");
        db.execSQL("INSERT INTO " + DBEntry.TABLE_NAME + " VALUES (1, 'Rachunek prawdopodob. i statystyka', 'A12', 0, 1, 10, 15, 11, 45, 'Dr inż. Derda Tomasz');");
        db.execSQL("INSERT INTO " + DBEntry.TABLE_NAME + " VALUES (2, 'Architektura Systemów Komputerowych', 'A13', 0, 1, 12, 00, 12, 45, 'Dr inż. Szustak Łukasz');");
        db.execSQL("INSERT INTO " + DBEntry.TABLE_NAME + " VALUES (3, 'Technika cyfrowa', 'B1', 1, 0, 8, 00, 9, 45, 'Dr inż. Smoląg Jacek');");
        db.execSQL("INSERT INTO " + DBEntry.TABLE_NAME + " VALUES (4, 'Podstawy sieci komputerowych', 'B1', 1, 0, 10, 00, 11, 30, 'Dr hab. inż. Nowicki Robert');");
        db.execSQL("INSERT INTO " + DBEntry.TABLE_NAME + " VALUES (5, 'Język Obcy', 'SJO', 1, 1, 12, 00, 13, 30, '');");
        db.execSQL("INSERT INTO " + DBEntry.TABLE_NAME + " VALUES (6, 'Podstawy sieci komputerowych', '510 IISI', 2, 2, 8, 30, 10, 00, 'Dr inż. Łapa Krystian');");
        db.execSQL("INSERT INTO " + DBEntry.TABLE_NAME + " VALUES (7, 'Technika cyfrowa', '514 IISI', 2, 2, 10, 15, 11, 45, 'Dr hab. inż. Bilski Jarosław');");
        db.execSQL("INSERT INTO " + DBEntry.TABLE_NAME + " VALUES (8, 'Metody programowania', '140 IITiS', 2, 2, 12, 00, 13, 30, 'Dr inż. Kuczyński Łukasz');");
        db.execSQL("INSERT INTO " + DBEntry.TABLE_NAME + " VALUES (9, 'Układy elektroniczne i technika pomiarowa', 'A2', 3, 0, 12, 15, 13, 00, 'Dr inż. Gruca Michał');");
        db.execSQL("INSERT INTO " + DBEntry.TABLE_NAME + " VALUES (10, 'Rachunek prawdopodob. i statystyka', 'A2', 3, 0, 13, 15, 14, 45, 'Dr Borowska Jolanta');");
        db.execSQL("INSERT INTO " + DBEntry.TABLE_NAME + " VALUES (11, 'Metody programowania', 'A3', 4, 0, 8, 15, 9, 00, 'Dr inż. Piątkowski Jacek');");
        db.execSQL("INSERT INTO " + DBEntry.TABLE_NAME + " VALUES (13, 'Układy elektroniczne i technika pomiarowa', 'H1-10 IMC', 4, 2, 9, 15, 10, 45, 'Dr inż. Jamrozik Arkadiusz');");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        super.onDowngrade(db, oldVersion, newVersion);
    }

    static String getSeparator(int minutes) {
        if(minutes < 10)
            return ":0";
        else
            return ":";
    }

    static String getDuration(Cursor cursor) {
        int startHour = cursor.getInt(cursor.getColumnIndex(DatabaseContract.DBEntry.COLUMN_NAME_START_HOUR));
        int endHour = cursor.getInt(cursor.getColumnIndex(DatabaseContract.DBEntry.COLUMN_NAME_END_HOUR));
        int startMinutes = cursor.getInt(cursor.getColumnIndex(DatabaseContract.DBEntry.COLUMN_NAME_START_MINUTE));
        int endMinutes = cursor.getInt(cursor.getColumnIndex(DatabaseContract.DBEntry.COLUMN_NAME_END_MINUTE));
        int durationHours = endHour - startHour;
        int durationMinutes = endMinutes - startMinutes;
        if(durationMinutes < 0)  {
            durationMinutes += 60;
            durationHours -= 1;

        }
        return Integer.toString(durationHours) + "h " + Integer.toString(durationMinutes) + "min";
    }

    static int getDurationInMinutes(Cursor cursor) {
        int startHour = cursor.getInt(cursor.getColumnIndex(DatabaseContract.DBEntry.COLUMN_NAME_START_HOUR));
        int endHour = cursor.getInt(cursor.getColumnIndex(DatabaseContract.DBEntry.COLUMN_NAME_END_HOUR));
        int startMinutes = cursor.getInt(cursor.getColumnIndex(DatabaseContract.DBEntry.COLUMN_NAME_START_MINUTE));
        int endMinutes = cursor.getInt(cursor.getColumnIndex(DatabaseContract.DBEntry.COLUMN_NAME_END_MINUTE));
        int durationHours = endHour - startHour;
        int durationMinutes = endMinutes - startMinutes;
        if(durationMinutes < 0)  {
            durationMinutes += 60;
            durationHours -= 1;

        }
        return durationMinutes + durationHours * 60;
    }
}
