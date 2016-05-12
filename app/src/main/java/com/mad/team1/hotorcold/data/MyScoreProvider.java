package com.mad.team1.hotorcold.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

/**
 * @author Zack Pollard
 */

public class MyScoreProvider extends ContentProvider {

    public static final int SCORE = 0;
    private static final UriMatcher myUriMatcher = buildUriMatcher();
    public static ScoreDBHelper myDBHelper;

    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);

        matcher.addURI(ScoreDataModel.CONTENT_AUTHORITY, ScoreDataModel.PATH_SCORE, SCORE);

        return matcher;
    }

    @Override
    public String getType(Uri uri) {
        // TODO: Implement this to handle requests for the MIME type of the data
        // at the given URI.
        int match_code = myUriMatcher.match(uri);

        switch(match_code){
            case SCORE: return ScoreDataModel.scoreEntry.CONTENT_TYPE_DIR;
            default: throw new UnsupportedOperationException("Not yet implemented");
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        // TODO: Implement this to handle requests to insert a new row.
        int match_code = myUriMatcher.match(uri);
        Uri retUri = null;

        switch(match_code){
            case SCORE:{
                SQLiteDatabase db = myDBHelper.getWritableDatabase();
                long _id = db.insert(ScoreDataModel.scoreEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    retUri = uri;
                else
                    throw new SQLException("failed to insert");
                break;
            }
            default:
                throw new UnsupportedOperationException("Not yet implemented");
        }

        return retUri;
    }

    @Override
    public boolean onCreate() {
        // TODO: Implement this to initialize your content provider on startup.
        myDBHelper = new ScoreDBHelper(getContext(), ScoreDBHelper.DB_NAME, null, ScoreDBHelper.DB_VERSION);
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        // TODO: Implement this to handle query requests from clients.
        int match_code = myUriMatcher.match(uri);
        Cursor myCursor;

        switch(match_code){
            case SCORE:{
                SQLiteDatabase db = myDBHelper.getWritableDatabase();
                myCursor = db.query(
                        ScoreDataModel.scoreEntry.TABLE_NAME, // Table to Query
                        projection,//Columns
                        selection, // Columns for the "where" clause
                        selectionArgs, // Values for the "where" clause
                        null, // columns to group by
                        null, // columns to filter by row groups
                        sortOrder // sort order
                );
                Log.i("MyScoreProvider", "querying for scores");
                Log.i("MyScoreProvider", "amount of rows = " + myCursor.getCount());
                break;
            }
            default:
                throw new UnsupportedOperationException("Not yet implemented");
        }

        myCursor.setNotificationUri(getContext().getContentResolver(),uri);
        return myCursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {

        throw new UnsupportedOperationException("Updating scores is not supported.");
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // TODO: Implement this to handle requests to update one or more rows.

        throw new UnsupportedOperationException("Deleting scores is not supported.");
    }

}
