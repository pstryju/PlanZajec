package com.stryju.planzajec;


import android.provider.BaseColumns;

final class DatabaseContract {

    private DatabaseContract() { }

    static class DBEntry implements BaseColumns {
        static final String TABLE_NAME = "activities";
        static final String COLUMN_NAME_ACTIVITY = "activity";
        static final String COLUMN_NAME_ROOM = "room";
        static final String COLUMN_NAME_DAY = "day";
        static final String COLUMN_NAME_TYPE = "type";
        static final String COLUMN_NAME_START_HOUR = "startHour";
        static final String COLUMN_NAME_START_MINUTE = "startMinute";
        static final String COLUMN_NAME_END_HOUR = "endHour";
        static final String COLUMN_NAME_END_MINUTE = "endMinute";
        static final String COLUMN_NAME_LECTURER = "lecturer";

    }


}
