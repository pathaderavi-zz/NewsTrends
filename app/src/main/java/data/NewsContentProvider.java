package data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by ravikiranpathade on 12/16/17.
 */

public class NewsContentProvider extends ContentProvider {

    public static final int FAVORITES = 100;

    public static final int FAVORITES_ID = 101;

    public static final int ALERTS = 200;

    public static final int ALERTS_ID = 201;


    public static final UriMatcher uriMatcher = buildUriMatcher();
    private FavoriteNewsDBHelper favoriteNewsDBHelper;


    private static UriMatcher buildUriMatcher() {
        UriMatcher match = new UriMatcher(UriMatcher.NO_MATCH);

        match.addURI(NewsContract.AUTHORITY, NewsContract.PATH_FAVORITES, FAVORITES);
        match.addURI(NewsContract.AUTHORITY, NewsContract.PATH_FAVORITES + "/id", FAVORITES_ID);

        match.addURI(NewsContract.AUTHORITY, NewsContract.PATH_ALERTS, ALERTS);
        match.addURI(NewsContract.AUTHORITY, NewsContract.PATH_ALERTS + "/id", ALERTS_ID);

        return match;
    }

    @Override
    public boolean onCreate() {
        Context context = getContext();

        favoriteNewsDBHelper = new FavoriteNewsDBHelper(context);

        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        int match = uriMatcher.match(uri);

        final SQLiteDatabase database = favoriteNewsDBHelper.getReadableDatabase();

        Cursor returnCursor = null;

        switch (match) {
            case FAVORITES_ID:
                try {
                    selection = "TITLE=" + "\"" + selection + "\"";


                    returnCursor = database.query(
                            NewsContract.NewsFavoritesEntry.TABLE_NAME,
                            null,
                            selection,
                            null,
                            null,
                            null,
                            sortOrder
                    );

                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;
            case FAVORITES:
                returnCursor = database.query(
                        NewsContract.NewsFavoritesEntry.TABLE_NAME,
                        projection,
                        selection,
                        null,
                        null,
                        null,
                        null
                );

                break;
            case ALERTS:
                returnCursor = database.query(
                        NewsContract.NewsAlertsEntry.TABLE_NAME,
                        projection,
                        selection,
                        null,
                        null,
                        null,
                        sortOrder
                );
                break;
            case ALERTS_ID:
                try {
                    selection = "TITLE=" + "\"" + selection + "\"";


                    returnCursor = database.query(
                            NewsContract.NewsAlertsEntry.TABLE_NAME,
                            null,
                            selection,
                            null,
                            null,
                            null,
                            null
                    );

                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            default:
                throw new UnsupportedOperationException("Unable to find" + uri);

        }
        return returnCursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {

        int matchUri = uriMatcher.match(uri);

        final SQLiteDatabase database = favoriteNewsDBHelper.getWritableDatabase();

        Uri returnUri = null;
        long id;
        switch (matchUri) {
            case FAVORITES:
                id = 0;
                try {
                    id = database.insert(
                            NewsContract.NewsFavoritesEntry.TABLE_NAME,
                            null,
                            contentValues
                    );
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (id > 0) {
                    returnUri = ContentUris.withAppendedId(NewsContract.NewsFavoritesEntry.FINAL_URI, id);
                    Log.d("Check Insert ID", returnUri.toString());
                }
                break;

            case ALERTS:
                id = 0;
                try {
                    id = database.insert(
                            NewsContract.NewsAlertsEntry.TABLE_NAME,
                            null,
                            contentValues
                    );
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (id > 0) {
                    returnUri = ContentUris.withAppendedId(NewsContract.NewsAlertsEntry.FINAL_URI, id);
                    Log.d("Check Insert ID", returnUri.toString());
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown Uri " + uri);
        }
        return returnUri;

    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        int matchUri = uriMatcher.match(uri);
        int id = 0;
        final SQLiteDatabase database = favoriteNewsDBHelper.getWritableDatabase();

        s = "TITLE=" + "\"" + s + "\"";

        switch (matchUri) {
            case FAVORITES_ID:
                id = database.delete(NewsContract.NewsFavoritesEntry.TABLE_NAME, s, null);
                break;
            case FAVORITES:
                id = database.delete(NewsContract.NewsFavoritesEntry.TABLE_NAME, null, null);
                break;
            case ALERTS_ID:
                id = database.delete(NewsContract.NewsAlertsEntry.TABLE_NAME, s, null);
                break;
            case ALERTS:
                id = database.delete(NewsContract.NewsAlertsEntry.TABLE_NAME, null, null);
                break;
            default:
                throw new UnsupportedOperationException("Unable to find " + uri);
        }
        return id;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        int match = uriMatcher.match(uri);

        final SQLiteDatabase database = favoriteNewsDBHelper.getWritableDatabase();
        int numInserted = 0;
        switch (match){
            case ALERTS:
                database.beginTransaction();
                try {
                    for (ContentValues cv : values) {
                        long newID = database.insertOrThrow(NewsContract.NewsAlertsEntry.TABLE_NAME, null, cv);
                        if (newID <= 0) {
                            throw new SQLException("Failed to insert row into " + uri);
                        }
                    }
                    database.setTransactionSuccessful();
                    getContext().getContentResolver().notifyChange(uri, null);
                    numInserted = values.length;
                } catch (Exception e){
                    e.printStackTrace();
                }finally {
                    database.endTransaction();
                }
                break;

        }
        return numInserted;
    }
}
