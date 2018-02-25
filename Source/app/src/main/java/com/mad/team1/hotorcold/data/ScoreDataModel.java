package com.mad.team1.hotorcold.data;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * @author Zack Pollard
 */

public class ScoreDataModel {

    public static final String CONTENT_AUTHORITY = "com.mad.team1.hotorcold";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_SCORE = "score";

    public static final class scoreEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_SCORE).build();

        public static final String CONTENT_TYPE_DIR = "vnd.android.cursor.dir/"+CONTENT_AUTHORITY+"/"+ PATH_SCORE;

        public static final String TABLE_NAME = "SCORE_TABLE";

        public static final String COLUMN_SCORE_TIME = "SCORE_TIME";

        public static final String COLUMN_SCORE_VALUE = "SCORE_VALUE";
    }
}