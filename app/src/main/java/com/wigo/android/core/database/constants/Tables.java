package com.wigo.android.core.database.constants;

/**
 * Created with IntelliJ IDEA.
 * User: Oleksii Khom
 * Date: 29.08.13
 * Time: 16:57
 * To change this template use File | Settings | File Templates.
 */
public final class Tables {

    public static interface Table {
        public static final String ID = "id";
    }

    public static final class STATUS_TABLE implements Table {
        public static String TABLE_NAME = "status_table";
        public static String USER_ID = "user_id";
        public static String LATITUDE = "latitude";
        public static String LONGTITUDE = "longitude";
        public static String NAME = "name";
        public static String TEXT = "text";
        public static String START_DATE = "start_date";
        public static String END_DATE = "end_date";
        public static String KIND = "kind";
        public static String LAST_OPEN_DATE = "last_open_date";
    }

}
