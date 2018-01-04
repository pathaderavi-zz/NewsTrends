package fragments;

import android.Manifest;
import android.app.Activity;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import com.example.ravikiranpathade.newstrends.R;
import com.github.thunder413.datetimeutils.DateTimeUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Date;
import java.util.List;

import data.NewsContract;
import models.Articles;
import utils.HelperFunctions;

//TODO Implement to request Permissions during Runtime

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link NewsDescriptionFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link NewsDescriptionFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NewsDescriptionFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    Cursor existing;
    FloatingActionButton fav;
    View view;
    WebView w;
    long id;
    String cursorID;

    String authorName;
    String publishedAt;
    String sourceId;
    String sourceName;
    String title;
    String imageUrl;
    String desc;
    String urlArticle;
    int j;
    CoordinatorLayout coordinatorLayout;
    Context context;

    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    public NewsDescriptionFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NewsDescriptionFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NewsDescriptionFragment newInstance(String param1, String param2) {
        NewsDescriptionFragment fragment = new NewsDescriptionFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        //TODO Optimize this with Parcelabel later

        if (title != null) {
            outState.putString(getContext().getResources().getString(R.string.title_pref), title);

        }
        if (desc != null) {
            outState.putString(getContext().getResources().getString(R.string.desc_pref), desc);
        }
        if (imageUrl != null) {
            outState.putString(getContext().getResources().getString(R.string.imageUrl_pref), imageUrl);
        }
        if (urlArticle != null) {
            outState.putString(getContext().getResources().getString(R.string.urlArticle_pref), urlArticle);
        }
        if (authorName != null) {
            outState.putString(getContext().getResources().getString(R.string.authorName_pref), authorName);
        }
        if (sourceId != null) {
            outState.putString(getContext().getResources().getString(R.string.sourceId_pref), sourceId);
        }
        if (sourceName != null) {
            outState.putString(getContext().getResources().getString(R.string.sourceName_pref), sourceName);
        }
        if (publishedAt != null) {
            outState.putString(getContext().getResources().getString(R.string.publishedAt_pref), publishedAt);
        }
        if (j != 0) {
            outState.putInt(getContext().getResources().getString(R.string.id_pref), j);
        }
        outState.putInt(getContext().getResources().getString(R.string.scrollXRelative), coordinatorLayout.getScrollX());
        outState.putInt(getContext().getResources().getString(R.string.scrollYRelative), coordinatorLayout.getScrollY());


    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        editor = preferences.edit();

        view = inflater.inflate(R.layout.fragment_news_description, container, false);
        coordinatorLayout = view.findViewById(R.id.coordinatorDetail);
        android.support.v7.widget.Toolbar toolbar = view.findViewById(R.id.toolbarDetail);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        setHasOptionsMenu(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        if (savedInstanceState == null) {
            j = getActivity().getIntent().getIntExtra(getContext().getResources().getString(R.string.list_id), 0);
            title = getActivity().getIntent().getStringExtra(getContext().getResources().getString(R.string.title_cursor_adapter));
            imageUrl = getActivity().getIntent().getStringExtra(getContext().getResources().getString(R.string.urlToImage_cursor_adapter));
            //TODO Check Internet Connectivity and set image accordingly
            desc = getActivity().getIntent().getStringExtra(getContext().getResources().getString(R.string.description_cursor_adapter));
            urlArticle = getActivity().getIntent().getStringExtra(getContext().getResources().getString(R.string.url_cursor_adapter));
            authorName = getActivity().getIntent().getStringExtra(getContext().getResources().getString(R.string.author_cursor_adapter));
            publishedAt = getActivity().getIntent().getStringExtra(getContext().getResources().getString(R.string.publishedAt_cursor_adapter));
            sourceId = getActivity().getIntent().getStringExtra(getContext().getResources().getString(R.string.source_id_cursor_adapter));
            sourceName = getActivity().getIntent().getStringExtra(getContext().getResources().getString(R.string.source_name_cursor_adapter));
        } else {
            j = savedInstanceState.getInt(getContext().getResources().getString(R.string.id_pref));
            title = savedInstanceState.getString(getContext().getResources().getString(R.string.title_pref));
            imageUrl = savedInstanceState.getString(getContext().getResources().getString(R.string.imageUrl_pref));
            //TODO Check Internet Connectivity and set image accordingly
            desc = savedInstanceState.getString(getContext().getResources().getString(R.string.desc_pref));
            urlArticle = savedInstanceState.getString(getContext().getResources().getString(R.string.urlArticle_pref));
            authorName = savedInstanceState.getString(getContext().getResources().getString(R.string.authorName_pref));
            publishedAt = savedInstanceState.getString(getContext().getResources().getString(R.string.publishedAt_pref));
            sourceId = savedInstanceState.getString(getContext().getResources().getString(R.string.sourceId_pref));
            sourceName = savedInstanceState.getString(getContext().getResources().getString(R.string.sourceName_pref));
        }


        existing = getContext().getContentResolver().query(

                NewsContract.NewsFavoritesEntry.FINAL_URI.buildUpon().appendPath(getContext().getResources().getString(R.string.id_append_string)).build(),
                null,
                title,
                null, null, null
        );
        if (existing != null && existing.getCount() > 0) {
            existing.moveToFirst();
            cursorID = String.valueOf(existing.getInt(existing.getColumnIndex(getContext().getResources().getString(R.string._id))));
        }

        //TODO Change Fab Button Background based on Cursor Result

        ImageView imageView = view.findViewById(R.id.detailImage);
        TextView descCard = view.findViewById(R.id.descDetail);
        TextView textView = view.findViewById(R.id.titleDetail);
        textView.setText(title);

        Glide.with(getContext()).load(imageUrl).override(400, 300).centerCrop().diskCacheStrategy(DiskCacheStrategy.NONE).error(R.drawable.noimageavailable).skipMemoryCache(true).into(imageView);

        descCard.setText(desc);


        Button webLink = view.findViewById(R.id.webLinkButton);
        webLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onButtonPressed(urlArticle, title);
            }
        });
        fav = view.findViewById(R.id.favoritFloat);
        if (existing != null && existing.getCount() > 0) {
            fav.setImageResource(R.drawable.ic_star_white_24px);
        }
        fav.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        requestPermissions(title, desc, imageUrl, urlArticle);
                    }
                }
        );
        if (savedInstanceState != null) {
            coordinatorLayout.scrollTo(savedInstanceState.getInt(getContext().getResources().getString(R.string.scrollXRelative)), savedInstanceState.getInt(getContext().getResources().getString(R.string.scrollYRelative)));
        }
        ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) descCard
                .getLayoutParams();
        int x = getActivity().getResources().getConfiguration().orientation;
        boolean isTablet = getActivity().getResources().getBoolean(R.bool.isTablet);
        ViewGroup.MarginLayoutParams imageGroup = (ViewGroup.MarginLayoutParams) imageView
                .getLayoutParams();
        Resources r = getResources();
        if (!isTablet) {
            if (x == Configuration.ORIENTATION_LANDSCAPE) {

                mlp.setMargins(100, 0, 100, 0);
                float pxImage = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 160, r.getDisplayMetrics());
                imageGroup.height = (int) pxImage;

            } else {
                mlp.setMargins(50, 0, 50, 0);

            }
        } else {
            mlp.setMargins(200, 50, 200, 0);
            float pxImage = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 410, r.getDisplayMetrics());
            imageGroup.height = (int) pxImage;

        }

        if (!isTablet && x == Configuration.ORIENTATION_LANDSCAPE) {
            //TODO

        }

        return view;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return super.onOptionsItemSelected(item);
    }

    private void onClickFab(String title, String desc, String imageUrl, String urlArticle) {

        //TODO Save Image
        Snackbar snackbar;

        existing = getContext().getContentResolver().query(

                NewsContract.NewsFavoritesEntry.FINAL_URI.buildUpon().appendPath(getContext().getResources().getString(R.string.id_append_string)).build(),
                null,
                title,
                null, null, null
        );
        boolean isConnected = new HelperFunctions().getConnectionInfo(getContext());
        if (isConnected) {
            if (existing != null && existing.getCount() > 0) {
                existing.moveToFirst();
                cursorID = String.valueOf(existing.getInt(existing.getColumnIndex(getContext().getResources().getString(R.string._id))));


                File mht = new File(getContext().getFilesDir().getAbsolutePath()
                        + File.separator, String.valueOf(cursorID) + getContext().getResources().getString(R.string.MHT));
                mht.setWritable(true);
                File jpg = new File(getContext().getFilesDir().getAbsolutePath()
                        + File.separator + getContext().getResources().getString(R.string.images), String.valueOf(cursorID) + getContext().getResources().getString(R.string.JPG));

                //boolean mhtDel = mht.delete();
                //boolean jpgDel = jpg.delete();

                editor.putString(getContext().getResources().getString(R.string.delete_image_boolean_key), String.valueOf(jpg));
                editor.putString(getContext().getResources().getString(R.string.delete_mht_boolean_key), String.valueOf(mht));
                editor.putBoolean(getContext().getResources().getString(R.string.delete_files), true);
                editor.commit();

                Uri delete = NewsContract.NewsFavoritesEntry.FINAL_URI.buildUpon().appendPath(getContext().getResources().getString(R.string.id_append_string)).build();
                getContext().getContentResolver().delete(delete, title, null);
                //Toast.makeText(getContext(), "Deleted", Toast.LENGTH_SHORT).show();
                fav.setImageResource(R.drawable.ic_star_border_white_24px);
                snackbar = Snackbar.make(view, getContext().getResources().getString(R.string.snackbar_delete_messsege), Snackbar.LENGTH_SHORT);


            } else {
                w = view.findViewById(R.id.detWeb);
                WebViewClient wClient = new CustomWebViewClientForDownload(getContext());
                w.setWebViewClient(wClient);
                w.loadUrl(urlArticle);
                Date dateInsert = DateTimeUtils.formatDate(publishedAt);

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(getContext().getResources().getString(R.string.dateFormatToInsert));

                ContentValues cv = new ContentValues();
                cv.put(NewsContract.NewsFavoritesEntry.COLUMN_NAME_TITLE, title);
                cv.put(NewsContract.NewsFavoritesEntry.COLUMN_NAME_DESCRIPTION, desc);
                cv.put(NewsContract.NewsFavoritesEntry.COLUMN_NAME_URL, urlArticle);
                cv.put(NewsContract.NewsFavoritesEntry.COLUMN_NAME_URL_TO_IMAGE, imageUrl);
                cv.put(NewsContract.NewsFavoritesEntry.COLUMN_NAME_AUTHOR, authorName);
                cv.put(NewsContract.NewsFavoritesEntry.COLUMN_NAME_PUBLISHED_AT, publishedAt);
                cv.put(NewsContract.NewsFavoritesEntry.COLUMN_NAME_SOURCE_ID, sourceId);
                cv.put(NewsContract.NewsFavoritesEntry.COLUMN_NAME_SOURCE_NAME, sourceName);
                cv.put(NewsContract.NewsFavoritesEntry.COLUMN_NAME_DATE, simpleDateFormat.format(dateInsert));
                Uri uri = getContext().getContentResolver().insert(
                        NewsContract.NewsFavoritesEntry.FINAL_URI, cv
                );
                id = ContentUris.parseId(uri);

                Glide.with(getContext()).load(imageUrl).asBitmap().override(400, 300).centerCrop().diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        File dir = new File(getContext().getFilesDir().getAbsolutePath()
                                + File.separator + getContext().getResources().getString(R.string.images));
                        if (!dir.exists()) {
                            dir.mkdir();
                        }
                        File ff = new File(dir, String.valueOf(id) + getContext().getResources().getString(R.string.JPG));
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
                fav.setImageResource(R.drawable.ic_star_white_24px);

                snackbar = Snackbar.make(view, getContext().getResources().getString(R.string.news_added_to_favorites), Snackbar.LENGTH_SHORT);

            }

            snackbar.show();
        } else {
            if (existing != null && existing.getCount() > 0) {
                existing.moveToFirst();
                cursorID = String.valueOf(existing.getInt(existing.getColumnIndex(getContext().getResources().getString(R.string._id))));


                File mht = new File(getContext().getFilesDir().getAbsolutePath()
                        + File.separator, String.valueOf(cursorID) + getContext().getResources().getString(R.string.MHT));
                mht.setWritable(true);
                File jpg = new File(getContext().getFilesDir().getAbsolutePath()
                        + File.separator + getContext().getResources().getString(R.string.images), String.valueOf(cursorID) + getContext().getResources().getString(R.string.JPG));



                editor.putString(getContext().getResources().getString(R.string.delete_image_boolean_key), String.valueOf(jpg));
                editor.putString(getContext().getResources().getString(R.string.delete_mht_boolean_key), String.valueOf(mht));
                editor.putBoolean(getContext().getResources().getString(R.string.delete_files), true);
                editor.commit();
                Uri delete = NewsContract.NewsFavoritesEntry.FINAL_URI.buildUpon().appendPath(getContext().getResources().getString(R.string.id_append_string)).build();
                getContext().getContentResolver().delete(delete, title, null);

                fav.setImageResource(R.drawable.ic_star_border_white_24px);
                snackbar = Snackbar.make(view, getContext().getResources().getString(R.string.snackbar_delete_messsege), Snackbar.LENGTH_SHORT);
            } else {
                Snackbar.make(view, getContext().getResources().getString(R.string.no_internet_snackbar), Snackbar.LENGTH_SHORT).show();
            }
        }

    }


    public void onButtonPressed(String uri, String t) {
        if (mListener != null) {
            mListener.onLinkButtonPressed(uri, t);
        }
    }

    public void onBackDetail(boolean s, File i, File m) {
        if (mListener != null) {
            mListener.onBackPressedFromDetail(s, i, m);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onLinkButtonPressed(String url, String title);

        void onBackPressedFromDetail(boolean status, File image, File mht);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        super.onCreateOptionsMenu(menu, inflater);
    }

    private class CustomWebViewClientForDownload extends WebViewClient {
        Context customContext;

        public CustomWebViewClientForDownload(Context context1) {
            customContext = context1;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            String id_file = String.valueOf(id);

            File dir = new File(customContext.getFilesDir().getAbsolutePath()
                    + File.separator);
            if (!dir.exists()) {
                dir.mkdir();
            }
            w.saveWebArchive(customContext.getFilesDir().getAbsolutePath()
                    + File.separator + id_file + customContext.getResources().getString(R.string.MHT));


        }

    }

    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);


    }

    public void requestPermissions(String titleP, String descP, String imageUrlP, String urlArticleP) {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(getContext().getResources().getString(R.string.need_storage_permission));
                builder.setMessage(getContext().getResources().getString(R.string.permission_rationale));
                builder.setPositiveButton(getContext().getResources().getString(R.string.grant), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 22);
                    }
                });
                builder.setNegativeButton(getContext().getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();


                    }
                });
                builder.show();
            } else if (PreferenceManager.getDefaultSharedPreferences(getContext()).getBoolean(getContext().getResources().getString(R.string.denied), false)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(getContext().getResources().getString(R.string.need_storage_permission));
                builder.setMessage(getContext().getResources().getString(R.string.permission_rationale));
                builder.setPositiveButton(getContext().getResources().getString(R.string.grant), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts(getContext().getResources().getString(R.string.package_permission), getActivity().getPackageName(), null);
                        intent.setData(uri);
                        startActivityForResult(intent, 22);
                        Toast.makeText(getContext(), getContext().getResources().getString(R.string.toast_permissions), Toast.LENGTH_LONG).show();
                    }
                });
                builder.setNegativeButton(getContext().getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            } else {
                //just request the permission
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 22);
            }
            SharedPreferences r = PreferenceManager.getDefaultSharedPreferences(getContext());
            SharedPreferences.Editor editor = r.edit();

            editor.putBoolean(getContext().getResources().getString(R.string.denied), true);
            editor.commit();
        } else {
            onClickFab(title, desc, imageUrl, urlArticle);
        }
    }
}
