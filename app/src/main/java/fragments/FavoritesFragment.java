package fragments;


import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.example.ravikiranpathade.newstrends.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import adapters.NewsCursorAdapter;
import adapters.NewsRecyclerAdapter;
import data.NewsContract;
import models.Articles;
import models.Source;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FavoritesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FavoritesFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    Cursor mCursor;
    List<Articles> articleList;
    NewsRecyclerAdapter adapter;
    RecyclerView recyclerView;
    LinearLayoutManager layoutManager;
    View view;
    TextView textView;
    ListView listView;
    Cursor cursor1;
    NewsCursorAdapter cursorAdapter;

    public FavoritesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FavoritesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FavoritesFragment newInstance(String param1, String param2) {
        FavoritesFragment fragment = new FavoritesFragment();
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
        cursorAdapter = new NewsCursorAdapter(getContext(), null);
        getActivity().getSupportLoaderManager().initLoader(0, null, this).forceLoad();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_favorites, container, false);
        textView = view.findViewById(R.id.favoritesTextView);


        listView = view.findViewById(R.id.favoriteRecycler);
        listView.setAdapter(cursorAdapter);

        return view;
    }


    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        return new CursorLoader(getContext(), NewsContract.NewsFavoritesEntry.FINAL_URI,
                null,
                null,
                null,
                "ID DESC");

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor1 != null) {
            cursor1 = null;
        }
        cursor1 = getContext().getContentResolver().query(
                NewsContract.NewsFavoritesEntry.FINAL_URI,
                null,
                null,
                null,
                "ID DESC"
        );
       if(cursor1==null||cursor1.getCount()==0){
           textView = view.findViewById(R.id.favoritesTextView);
           listView = view.findViewById(R.id.favoriteRecycler);

           textView.setVisibility(View.VISIBLE);
           listView.setVisibility(View.GONE);

       }
        cursorAdapter.swapCursor(cursor1);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        cursorAdapter.swapCursor(null);
    }

    @Override
    public void onStart() {
        super.onStart();
        getActivity().getSupportLoaderManager().initLoader(0, null, this);
    }
}
