package fragments;

import android.app.Activity;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.ravikiranpathade.newstrends.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import data.NewsContract;
import models.Articles;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_news_description, container, false);

        android.support.v7.widget.Toolbar toolbar = view.findViewById(R.id.toolbarDetail);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
//        final Drawable upArrow = getResources().getDrawable(R.drawable.);
//        upArrow.setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
        setHasOptionsMenu(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        int j = getActivity().getIntent().getIntExtra("list_id", 0);
        final String title = getActivity().getIntent().getStringExtra("title");
        final String imageUrl = getActivity().getIntent().getStringExtra("urlToImage");
        final String desc = getActivity().getIntent().getStringExtra("description");
        final String urlArticle = getActivity().getIntent().getStringExtra("url");

        existing = getContext().getContentResolver().query(

                NewsContract.NewsFavoritesEntry.FINAL_URI.buildUpon().appendPath("id").build(),
                null,
                title,
                null, null, null
        );

        //TODO Change Fab Button Background based on Cursor Result

        ImageView imageView = view.findViewById(R.id.detailImage);
        TextView descCard = view.findViewById(R.id.descDetail);
        TextView textView = view.findViewById(R.id.titleDetail);
        textView.setText(title);
        Glide.with(getContext()).load(imageUrl).into(imageView);
        descCard.setText(desc);


        Button webLink = view.findViewById(R.id.webLinkButton);
        webLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onButtonPressed(urlArticle);
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
                        onClickFab(title, desc, imageUrl, urlArticle);
                    }
                }
        );

        return view;
    }

    private void onClickFab(String title, String desc, String imageUrl, String urlArticle) {

        //TODO Save Image
        Snackbar snackbar;

        existing = getContext().getContentResolver().query(

                NewsContract.NewsFavoritesEntry.FINAL_URI.buildUpon().appendPath("id").build(),
                null,
                title,
                null, null, null
        );

        if (existing != null && existing.getCount() > 0) {
            new File(getContext().getFilesDir().getAbsolutePath()
                    + File.separator + String.valueOf(id)+".mht").delete();

            Uri delete = NewsContract.NewsFavoritesEntry.FINAL_URI.buildUpon().appendPath("id").build();
            getContext().getContentResolver().delete(delete, title, null);
            //Toast.makeText(getContext(), "Deleted", Toast.LENGTH_SHORT).show();
            fav.setImageResource(R.drawable.ic_star_border_white_24px);
            snackbar = Snackbar.make(view, "News Deleted", Snackbar.LENGTH_SHORT);

        } else {
            w = view.findViewById(R.id.detWeb);
            WebViewClient wClient = new CustomWebViewClientForDownload();
            w.setWebViewClient(wClient);
            w.loadUrl(urlArticle);

            ContentValues cv = new ContentValues();
            cv.put(NewsContract.NewsFavoritesEntry.COLUMN_NAME_TITLE, title);
            cv.put(NewsContract.NewsFavoritesEntry.COLUMN_NAME_DESCRIPTION, desc);
            cv.put(NewsContract.NewsFavoritesEntry.COLUMN_NAME_URL, urlArticle);
            cv.put(NewsContract.NewsFavoritesEntry.COLUMN_NAME_URL_TO_IMAGE, imageUrl);

            Uri uri = getContext().getContentResolver().insert(
                    NewsContract.NewsFavoritesEntry.FINAL_URI, cv
            );
            id = ContentUris.parseId(uri);
            Log.d("Check ID",String.valueOf(id));
            fav.setImageResource(R.drawable.ic_star_white_24px);
            //Toast.makeText(getContext(), uri.toString(), Toast.LENGTH_SHORT).show();
            snackbar = Snackbar.make(view, "News Added to Favorites", Snackbar.LENGTH_SHORT);

        }

        snackbar.show();
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(String uri) {
        if (mListener != null) {
            mListener.onLinkButtonPressed(uri);
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
        void onLinkButtonPressed(String url);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        super.onCreateOptionsMenu(menu, inflater);
    }
    private class CustomWebViewClientForDownload extends WebViewClient{
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            String id_file = String.valueOf(id);
            w.saveWebArchive(getContext().getFilesDir().getAbsolutePath()
                  + File.separator + id_file+".mht");
        }

    }
}
