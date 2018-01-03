package fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ravikiranpathade.newstrends.R;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.ArrayList;
import java.util.List;

import adapters.NewsRecyclerAdapter;
import models.Articles;
import models.CompleteResponse;
import rest.Client;
import rest.GetTopNewsWorldEnglish;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import utils.HelperFunctions;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SearchLatestNews.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SearchLatestNews#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchLatestNews extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    android.support.v7.widget.SearchView searchView;
    Spinner spinner;
    TextView textView;
    boolean isConnected;
    RecyclerView searchRecycler;
    RecyclerView.LayoutManager layoutManager;
    NewsRecyclerAdapter adapter;
    List<Articles> allArticles;
    ProgressBar progressBar;
    public final String KEY = "16a2ce7a435e4acb8482fae088ba6b9e";

    private OnFragmentInteractionListener mListener;
    private FirebaseAnalytics mFirebaseAnalytics;

    public SearchLatestNews() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SearchLatestNews.
     */
    // TODO: Rename and change types and number of parameters
    public static SearchLatestNews newInstance(String param1, String param2) {
        SearchLatestNews fragment = new SearchLatestNews();
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

        View view = inflater.inflate(R.layout.fragment_search_latest_news, container, false);
        int h = view.getWidth();

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(getContext());
        searchView = view.findViewById(R.id.searchViewLatest);
        spinner = view.findViewById(R.id.spinnerPriority);
        textView = view.findViewById(R.id.showingLatestSearchText);
        searchView.setMaxWidth(h);
        progressBar = view.findViewById(R.id.progressBarSearchNews);

        searchRecycler = view.findViewById(R.id.searchLatestRecycler);
        layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);

        String[] spinnerItems = {"Popularity", "Published At", "Relevancy"};
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(getContext(), R.layout.support_simple_spinner_dropdown_item, spinnerItems);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spinner.setAdapter(spinnerAdapter);

        searchView.setOnQueryTextListener(new android.support.v7.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                String spinnerText = spinner.getItemAtPosition(spinner.getSelectedItemPosition()).toString();

                //searchView.setIconified(true);
                isConnected = new HelperFunctions().getConnectionInfo(getContext());
                searchView.onActionViewCollapsed();
                if (isConnected) {

                    textView.setText("Showing results for " + query.toUpperCase() + " by " + spinnerText);
                    if (spinnerText.equals("Published At")) {
                        spinnerText = "publishedAt";
                    }

                    Bundle bundle = new Bundle();

                    bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "Search");
                    bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, query);

                    mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);


                    query = query.replaceAll("\\s+","+");
                    progressBar.setVisibility(View.VISIBLE);
                    makeSearchCall(query, spinnerText);

                } else {
                    textView.setText("Please Connect to Internet First");
                }

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });


        return view;
    }

    private void makeSearchCall(String query, String spinnerText) {

        allArticles = new ArrayList<>();
        GetTopNewsWorldEnglish service = Client.getClient().create(GetTopNewsWorldEnglish.class);
        Call<CompleteResponse> call = service.getSearchEverything(KEY,spinnerText,query);

        call.enqueue(new Callback<CompleteResponse>() {
            @Override
            public void onResponse(Call<CompleteResponse> call, Response<CompleteResponse> response) {
                allArticles = response.body().getArticles();
                adapter = new NewsRecyclerAdapter(allArticles);
                searchRecycler.setLayoutManager(layoutManager);
                searchRecycler.setAdapter(adapter);
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<CompleteResponse> call, Throwable t) {
                textView.setText("Sorry, News Could not be searched due to some Error.");
            }
        });
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
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
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (!isVisibleToUser && searchView != null) {
            searchView.onActionViewCollapsed();
            searchView.clearFocus();
        }

    }
}
