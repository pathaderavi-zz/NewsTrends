package adapters;

import android.Manifest;
import android.app.Activity;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.SQLException;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.daimajia.swipe.SwipeLayout;
import com.example.ravikiranpathade.newstrends.R;
import com.example.ravikiranpathade.newstrends.activities.AlertedNewsActivity;
import com.example.ravikiranpathade.newstrends.activities.NewsDetailActivity;
import com.github.thunder413.datetimeutils.DateTimeUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import data.NewsContract;

/**
 * Created by ravikiranpathade on 12/26/17.
 */

public class NewsCursorAdapter extends CursorAdapter {
    String check;
    long cursor_insert;

    public interface checkEmpty {
        void onCheckEmpty(boolean checkBoolean);
    }

    public checkEmpty checkListener;

    public NewsCursorAdapter(Context context, Cursor c, String from, checkEmpty checkClick) {
        super(context, c, 0);
        check = from;
        checkListener = checkClick;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return LayoutInflater.from(context).inflate(R.layout.swipe_layout_file, viewGroup, false);
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {

        ImageView newsCardImage = view.findViewById(R.id.news_card_image);
        TextView headline = view.findViewById(R.id.headline_card);
        TextView author = view.findViewById(R.id.author_card);
        CardView cardView = view.findViewById(R.id.news_card);
        final WebView webViewAdapter = view.findViewById(R.id.adapterWeb);

        final String cursorId = String.valueOf(cursor.getInt(cursor.getColumnIndex(context.getResources().getString(R.string._id))));
        final String title_string = cursor.getString(cursor.getColumnIndex(context.getResources().getString(R.string.TITLE)));
        final String desc_string = cursor.getString(cursor.getColumnIndex(context.getResources().getString(R.string.DESCRIPTION)));
        final String imageUrl_string = context.getFilesDir().getAbsolutePath()
                + File.separator + context.getResources().getString(R.string.images) + File.separator + String.valueOf(cursorId) + context.getResources().getString(R.string.JPG);
        final String urlArticle_string = cursor.getString(cursor.getColumnIndex(context.getResources().getString(R.string.URL)));
        final String author_string = cursor.getString(cursor.getColumnIndex(context.getResources().getString(R.string.AUTHOR)));
        final String publishedAt_string = cursor.getString(cursor.getColumnIndex(context.getResources().getString(R.string.PUBLISHEDAT)));
        final String source_id_string = cursor.getString(cursor.getColumnIndex(context.getResources().getString(R.string.SOURCEID)));
        final String source_name_string = cursor.getString(cursor.getColumnIndex(context.getResources().getString(R.string.SOURCENAME)));
        final String keyword_string;
        if (check != null) {
            keyword_string = cursor.getString(cursor.getColumnIndex(context.getResources().getString(R.string.KEYWORDCOLUMN)));
        }

        //TODO Load Images for Alerts and Favorites Separately

        final String imageUrl_string_web = cursor.getString(cursor.getColumnIndex(context.getResources().getString(R.string.URLTOIMAGE)));


        if (author_string != null) {
            author.setText(context.getResources().getString(R.string.by) + author_string + context.getResources().getString(R.string.at)+ source_name_string);

        }

        if (check == null) {
            Glide.with(context).load(imageUrl_string).centerCrop().diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).into(newsCardImage);

        } else {
            Glide.with(context).load(imageUrl_string_web).centerCrop().diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).into(newsCardImage);

        }


        headline.setText(title_string);
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, NewsDetailActivity.class);

                i.putExtra(context.getResources().getString(R.string.list_id), Integer.parseInt(cursorId));
                i.putExtra(context.getResources().getString(R.string.title_cursor_adapter), title_string);
                i.putExtra(context.getResources().getString(R.string.description_cursor_adapter), desc_string);
                if (check == null) {
                    i.putExtra(context.getResources().getString(R.string.urlToImage_cursor_adapter), imageUrl_string);

                } else {
                    i.putExtra(context.getResources().getString(R.string.urlToImage_cursor_adapter), imageUrl_string_web);

                }
                i.putExtra(context.getResources().getString(R.string.url_cursor_adapter), urlArticle_string);
                i.putExtra(context.getResources().getString(R.string.author_cursor_adapter), author_string);
                i.putExtra(context.getResources().getString(R.string.publishedAt_cursor_adapter), publishedAt_string);
                i.putExtra(context.getResources().getString(R.string.source_id_cursor_adapter), source_id_string);
                i.putExtra(context.getResources().getString(R.string.source_name_cursor_adapter), source_name_string);

                context.startActivity(i);
            }
        });


        final SwipeLayout swipeLayout = (SwipeLayout) view.findViewById(R.id.swipe1);

//set show mode.
        swipeLayout.setShowMode(SwipeLayout.ShowMode.LayDown);

//add drag edge.(If the BottomView has 'layout_gravity' attribute, this line is unnecessary)
        swipeLayout.addDrag(SwipeLayout.DragEdge.Left, view.findViewById(R.id.bottom_wrapper));

        ImageView sampleClick = view.findViewById(R.id.deleteFromCursorAdater);
        sampleClick.setVisibility(View.VISIBLE
        );
        sampleClick.setOnClickListener(new View.OnClickListener() {
                                           @Override
                                           public void onClick(View view) {
                                               ContentValues cv = new ContentValues();
                                               cv.put(NewsContract.NewsDeletedAlerts.COLUMN_NAME_TITLE, title_string);
                                               cv.put(NewsContract.NewsDeletedAlerts.COLUMN_NAME_DESCRIPTION, desc_string);
                                               cv.put(NewsContract.NewsDeletedAlerts.COLUMN_NAME_URL, urlArticle_string);
                                               cv.put(NewsContract.NewsDeletedAlerts.COLUMN_NAME_URL_TO_IMAGE, imageUrl_string);
                                               cv.put(NewsContract.NewsDeletedAlerts.COLUMN_NAME_AUTHOR, author_string);
                                               cv.put(NewsContract.NewsDeletedAlerts.COLUMN_NAME_PUBLISHED_AT, publishedAt_string);
                                               cv.put(NewsContract.NewsDeletedAlerts.COLUMN_NAME_SOURCE_ID, source_id_string);
                                               cv.put(NewsContract.NewsDeletedAlerts.COLUMN_NAME_SOURCE_NAME, source_name_string);

                                               Date dateInsert = DateTimeUtils.formatDate(publishedAt_string);
                                               SimpleDateFormat simpleDateFormat = new SimpleDateFormat(context.getResources().getString(R.string.dateFormatToInsert));

                                               cv.put(NewsContract.NewsDeletedAlerts.COLUMN_NAME_DATE, simpleDateFormat.format(dateInsert));

                                               Uri uri = null;
                                               try {
                                                   uri = context.getContentResolver().insert(
                                                           NewsContract.NewsDeletedAlerts.FINAL_URI, cv);

                                               } catch (SQLException e) {
                                                   e.printStackTrace();
                                               }
                                               Uri delete = null;
                                               if (check != null) {
                                                   delete = NewsContract.NewsAlertsEntry.FINAL_URI.buildUpon().appendPath(context.getResources().getString(R.string.id_append_string)).build();
                                                   context.getContentResolver().delete(delete, title_string, null);


                                                   swapCursor(context.getContentResolver().query(NewsContract.NewsAlertsEntry.FINAL_URI,
                                                           null,
                                                           null,
                                                           null,
                                                           context.getResources().getString(R.string.DATE_DESC)));
                                                   //TODO Move in new table called deletedAlerts


                                                   notifyDataSetChanged();
                                                   if (context.getContentResolver().query(NewsContract.NewsAlertsEntry.FINAL_URI,
                                                           null,
                                                           null,
                                                           null,
                                                           context.getResources().getString(R.string.DATE_DESC)).getCount() == 0) {
                                                       checkListener.onCheckEmpty(true);
                                                   }
                                                   swipeLayout.close();
                                                   Snackbar.make(view, context.getResources().getString(R.string.snackbar_delete_messsege), Snackbar.LENGTH_SHORT).show();
                                               } else {
                                                   delete = NewsContract.NewsFavoritesEntry.FINAL_URI.buildUpon().appendPath(context.getResources().getString(R.string.id_append_string)).build();
                                                   context.getContentResolver().delete(delete, title_string, null);


                                                   swapCursor(context.getContentResolver().query(NewsContract.NewsFavoritesEntry.FINAL_URI,
                                                           null,
                                                           null,
                                                           null,
                                                           context.getResources().getString(R.string.DATE_DESC)));
                                                   //TODO Move in new table called deletedAlerts
                                                   notifyDataSetChanged();
                                                   if (context.getContentResolver().query(NewsContract.NewsFavoritesEntry.FINAL_URI,
                                                           null,
                                                           null,
                                                           null,
                                                           context.getResources().getString(R.string.DATE_DESC)).getCount() == 0) {
                                                       checkListener.onCheckEmpty(true);
                                                   }
                                                   swipeLayout.close();
                                                   Snackbar.make(view, context.getResources().getString(R.string.snackbar_delete_messsege), Snackbar.LENGTH_SHORT).show();
                                               }


                                           }
                                       }
        );


        if (check != null) {
            ImageView fav = view.findViewById(R.id.deleteFromCursorAdater2);
            fav.setVisibility(View.VISIBLE);
            fav.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (ActivityCompat.checkSelfPermission((Activity) context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                            AlertDialog.Builder builder = new AlertDialog.Builder((Activity) context);
                            builder.setTitle(context.getResources().getString(R.string.need_storage_permission));
                            builder.setMessage(context.getResources().getString(R.string.permission_rationale));
                            builder.setPositiveButton(context.getResources().getString(R.string.grant), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                    ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 22);
                                }
                            });
                            builder.setNegativeButton(context.getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();


                                }
                            });
                            builder.show();
                        } else if (PreferenceManager.getDefaultSharedPreferences(context).getBoolean(context.getResources().getString(R.string.denied), false)) {
                            AlertDialog.Builder builder = new AlertDialog.Builder((Activity) context);
                            builder.setTitle(context.getResources().getString(R.string.need_storage_permission));
                            builder.setMessage(context.getResources().getString(R.string.permission_rationale));
                            builder.setPositiveButton(context.getResources().getString(R.string.grant), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                    Uri uri = Uri.fromParts(context.getResources().getString(R.string.package_permission), context.getPackageName(), null);
                                    intent.setData(uri);
                                    ((Activity) context).startActivityForResult(intent, 22);
                                    Toast.makeText(context, context.getResources().getString(R.string.toast_permissions), Toast.LENGTH_LONG).show();
                                }
                            });
                            builder.setNegativeButton(context.getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });
                            builder.show();
                        } else {
                            //just request the permission
                            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 22);
                        }
                        SharedPreferences r = PreferenceManager.getDefaultSharedPreferences(context);
                        SharedPreferences.Editor editor = r.edit();

                        editor.putBoolean(context.getResources().getString(R.string.denied), true);
                        editor.commit();
                        swipeLayout.close();
                    } else {
                        //TODO Add to favorites
                        ContentValues cv = new ContentValues();
                        cv.put(NewsContract.NewsFavoritesEntry.COLUMN_NAME_TITLE, title_string);
                        cv.put(NewsContract.NewsFavoritesEntry.COLUMN_NAME_DESCRIPTION, desc_string);
                        cv.put(NewsContract.NewsFavoritesEntry.COLUMN_NAME_URL, urlArticle_string);
                        cv.put(NewsContract.NewsFavoritesEntry.COLUMN_NAME_URL_TO_IMAGE, imageUrl_string);
                        cv.put(NewsContract.NewsFavoritesEntry.COLUMN_NAME_AUTHOR, author_string);
                        cv.put(NewsContract.NewsFavoritesEntry.COLUMN_NAME_PUBLISHED_AT, publishedAt_string);
                        cv.put(NewsContract.NewsFavoritesEntry.COLUMN_NAME_SOURCE_ID, source_id_string);
                        cv.put(NewsContract.NewsFavoritesEntry.COLUMN_NAME_SOURCE_NAME, source_name_string);

                        Date dateInsert = DateTimeUtils.formatDate(publishedAt_string);
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(context.getResources().getString(R.string.dateFormatToInsert));

                        cv.put(NewsContract.NewsFavoritesEntry.COLUMN_NAME_DATE, simpleDateFormat.format(dateInsert));
                        Uri uri = null;
                        try {
                            uri = context.getContentResolver().insert(
                                    NewsContract.NewsFavoritesEntry.FINAL_URI, cv);
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                        if (uri != null) {
                            cursor_insert = ContentUris.parseId(uri);


                            Uri delete = NewsContract.NewsAlertsEntry.FINAL_URI.buildUpon().appendPath(context.getResources().getString(R.string.id_append_string)).build();
                            context.getContentResolver().delete(delete, title_string, null);



                            Glide.with(context).load(imageUrl_string_web).asBitmap().override(400, 300).centerCrop().diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).into(new SimpleTarget<Bitmap>() {
                                @Override
                                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                                    File dir = new File(context.getFilesDir().getAbsolutePath()
                                            + File.separator + context.getResources().getString(R.string.images));
                                    if (!dir.exists()) {
                                        dir.mkdir();
                                    }
                                    File ff = new File(dir, String.valueOf(cursor_insert) + context.getResources().getString(R.string.JPG));
                                    try {
                                        FileOutputStream fileOutputStream = new FileOutputStream(ff);
                                        resource.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
                                        fileOutputStream.flush();
                                        fileOutputStream.close();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }


                            });




                            WebViewClient wClient = new CustomWebView(context, String.valueOf(cursor_insert));
                            webViewAdapter.setWebViewClient(wClient);
                            webViewAdapter.loadUrl(urlArticle_string);




                            swapCursor(context.getContentResolver().query(NewsContract.NewsAlertsEntry.FINAL_URI,
                                    null,
                                    null,
                                    null,
                                    context.getResources().getString(R.string.DATE_DESC)));


                            notifyDataSetChanged();
                        }
                        if (context.getContentResolver().query(NewsContract.NewsAlertsEntry.FINAL_URI,
                                null,
                                null,
                                null,
                                context.getResources().getString(R.string.DATE_DESC)).getCount() == 0) {
                            checkListener.onCheckEmpty(true);
                        }
                        swipeLayout.close();
                        Snackbar.make(view,context.getResources().getString(R.string.snackbar_moved_messege),Snackbar.LENGTH_SHORT).show();
                    }
                }
            });
        }
        if (context.getContentResolver().query(NewsContract.NewsAlertsEntry.FINAL_URI,
                null,
                null,
                null,
                context.getResources().getString(R.string.DATE_DESC)).getCount() == 0) {
            checkListener.onCheckEmpty(true);
        }
    }

    public class CustomWebView extends WebViewClient {
        Context customContext;
        String fname;

        public CustomWebView(Context context1, String filename) {
            customContext = context1;
            fname = filename;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            String id_file = String.valueOf(fname);

            File dir = new File(customContext.getFilesDir().getAbsolutePath()
                    + File.separator);
            if (!dir.exists()) {
                dir.mkdir();
            }
            view.saveWebArchive(customContext.getFilesDir().getAbsolutePath()
                    + File.separator + id_file + customContext.getResources().getString(R.string.MHT));
        }
    }

}
