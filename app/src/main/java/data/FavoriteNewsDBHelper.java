package data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by ravikiranpathade on 12/15/17.
 */

public class FavoriteNewsDBHelper extends SQLiteOpenHelper{

    private static final String FAVORITE_DATABASE_NAME = "favorites.db";

    private static final int VERSION = 7;

    public FavoriteNewsDBHelper(Context context) {
        super(context, FAVORITE_DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        Log.d("Favorite Table","Create");

        final String CREATE_TABLE = "CREATE TABLE "+ NewsContract.NewsAlertsEntry.TABLE_NAME+" ( "+
                NewsContract.NewsFavoritesEntry.COLUMN_ID+" INTEGER PRIMARY KEY, "+
                NewsContract.NewsFavoritesEntry.COLUMN_NAME_TITLE+" TEXT NOT NULL,"
                + NewsContract.NewsFavoritesEntry.COLUMN_NAME_AUTHOR+" TEXT, "
                + NewsContract.NewsFavoritesEntry.COLUMN_NAME_DESCRIPTION+" TEXT, "
                + NewsContract.NewsFavoritesEntry.COLUMN_NAME_PUBLISHED_AT+" TEXT, "
                + NewsContract.NewsFavoritesEntry.COLUMN_NAME_SOURCE_ID+" TEXT, "
                + NewsContract.NewsFavoritesEntry.COLUMN_NAME_SOURCE_NAME+" TEXT, "
                + NewsContract.NewsFavoritesEntry.COLUMN_NAME_URL+" TEXT UNIQUE , "
                + NewsContract.NewsFavoritesEntry.COLUMN_NAME_URL_TO_IMAGE+" TEXT, "
                + NewsContract.NewsFavoritesEntry.COLUMN_NAME_DATE+" DATE);";

        final String CREATE_TABLE_ALERTS= "CREATE TABLE "+ NewsContract.NewsFavoritesEntry.TABLE_NAME+" ( "+
                NewsContract.NewsFavoritesEntry.COLUMN_ID+" INTEGER PRIMARY KEY, "+
                NewsContract.NewsFavoritesEntry.COLUMN_NAME_TITLE+" TEXT NOT NULL,"
                + NewsContract.NewsFavoritesEntry.COLUMN_NAME_AUTHOR+" TEXT, "
                + NewsContract.NewsFavoritesEntry.COLUMN_NAME_DESCRIPTION+" TEXT, "
                + NewsContract.NewsFavoritesEntry.COLUMN_NAME_PUBLISHED_AT+" TEXT, "
                + NewsContract.NewsFavoritesEntry.COLUMN_NAME_SOURCE_ID+" TEXT, "
                + NewsContract.NewsFavoritesEntry.COLUMN_NAME_SOURCE_NAME+" TEXT, "
                + NewsContract.NewsFavoritesEntry.COLUMN_NAME_URL+" TEXT UNIQUE , "
                + NewsContract.NewsFavoritesEntry.COLUMN_NAME_URL_TO_IMAGE+" TEXT, "
                + NewsContract.NewsFavoritesEntry.COLUMN_NAME_DATE+" DATE);";

        sqLiteDatabase.execSQL(CREATE_TABLE);
        sqLiteDatabase.execSQL(CREATE_TABLE_ALERTS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+ NewsContract.NewsAlertsEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+ NewsContract.NewsFavoritesEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
