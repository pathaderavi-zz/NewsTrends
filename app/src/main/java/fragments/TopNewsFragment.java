package fragments;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.androidfung.geoip.IpApiService;
import com.androidfung.geoip.ServicesManager;
import com.androidfung.geoip.model.GeoIpResponseModel;
import com.example.ravikiranpathade.newstrends.R;
import com.example.ravikiranpathade.newstrends.activities.MainActivity;
import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.RetryStrategy;
import com.firebase.jobdispatcher.Trigger;
import com.github.thunder413.datetimeutils.DateTimeUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import adapters.NewsRecyclerAdapter;
import models.Articles;
import models.CompleteResponse;
import rest.Client;
import rest.GetTopNewsWorldEnglish;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import services.FetchTopNewsService;
import utils.HelperFunctions;

import static android.provider.Contacts.SettingsColumns.KEY;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TopNewsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TopNewsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TopNewsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public final String KEY = "16a2ce7a435e4acb8482fae088ba6b9e";
    RecyclerView.LayoutManager layoutManager;
    RecyclerView topNewsRecycler;
    NewsRecyclerAdapter adapter;
    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    private String JOB_TAG = "fetch_top_news";
    FirebaseJobDispatcher dispatcher;
    boolean isConnected;
    ProgressBar spinningProgress;
    TextView showingTopNewsFor;
    Gson gson;
    GetTopNewsWorldEnglish service;
    List<Articles>[] a1;
    String resp;
    TextView viewEnd;
    View view;
    String country;
    String language;
    String category;

    public TopNewsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TopNewsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TopNewsFragment newInstance(String param1, String param2) {
        TopNewsFragment fragment = new TopNewsFragment();
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
        // getActivity().getSupportLoaderManager().initLoader(0, savedInstanceState, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final LayoutInflater finalInflater = inflater;
        final ViewGroup finalViewgroup = container;


        view = inflater.inflate(R.layout.fragment_top_news, container, false);


        isConnected = new HelperFunctions().getConnectionInfo(getContext());
        viewEnd = view.findViewById(R.id.textViewEnd);
        layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        topNewsRecycler = view.findViewById(R.id.topNewsRecycler);
        boolean isTablet = getActivity().getResources().getBoolean(R.bool.isTablet);

        if (isTablet) {
            topNewsRecycler.setLayoutManager(new GridLayoutManager(view.getContext(), 2));
        } else {
            topNewsRecycler.setLayoutManager(layoutManager);
        }

        spinningProgress = view.findViewById(R.id.progressBarTopNews);
        showingTopNewsFor = view.findViewById(R.id.showingTopNewsText);

        spinningProgress.setVisibility(View.VISIBLE);
        topNewsRecycler.setVisibility(View.INVISIBLE);
        view.findViewById(R.id.textViewEnd).setVisibility(View.INVISIBLE);
        showingTopNewsFor.setVisibility(View.INVISIBLE);


        prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        editor = prefs.edit();

        country = prefs.getString("countryList", "");

        language = prefs.getString("languageList", "");

        category = prefs.getString("categoriesList", "");

        if (String.valueOf(language).equals("null") || String.valueOf(language).equals("")
                || String.valueOf(language).equals("0")) {
            language = "en";
            editor.putString("languageList", "en");
            editor.commit();
        }
        if (String.valueOf(category).equals("null") || String.valueOf(category).equals("0") || category.equals("")) {
            category = "";
            editor.putString("categoriesList", "");
        }
        final String[] countryList = getActivity().getResources().getStringArray(R.array.preferenceCountryValues);
        if (isConnected) {
            if (String.valueOf(country).equals("null") || String.valueOf(country).equals("0") || country.equals("")) { // Code Not Set
                IpApiService ipApiService = ServicesManager.getGeoIpService();
                ipApiService.getGeoIp().enqueue(new Callback<GeoIpResponseModel>() {
                    @Override
                    public void onResponse(Call<GeoIpResponseModel> call, Response<GeoIpResponseModel> response) {
                        country = response.body().getCountryCode();
                        boolean checkCountryMatch = false;
                        for (String c : countryList) {
                            if (c.equals(country.toLowerCase())) {
                                checkCountryMatch = true;
                            }
                        }
                        if (checkCountryMatch) {
                            responseCall(language, country, category);
                        } else {
                            responseCall(language, "", category);
                        }
                    }

                    @Override
                    public void onFailure(Call<GeoIpResponseModel> call, Throwable t) {

                    }
                });


            } else { // Code is Set
                responseCall(language, country, category);
            }
        } else {
            String resp = prefs.getString("topnews", "");
            if ((!resp.equals("") && !resp.equals("[]"))) {
                Type type = new TypeToken<List<Articles>>() {
                }.getType();

                a1[0] = gson.fromJson(resp, type);

                adapter = new NewsRecyclerAdapter(a1[0]);
                topNewsRecycler.setAdapter(adapter);

                spinningProgress.setVisibility(View.GONE);
                topNewsRecycler.setVisibility(View.VISIBLE);
                view.findViewById(R.id.textViewEnd).setVisibility(View.VISIBLE);
                showingTopNewsFor.setVisibility(View.VISIBLE);
            } else {
                spinningProgress.setVisibility(View.GONE);
                topNewsRecycler.setVisibility(View.VISIBLE);

                viewEnd.setVisibility(View.VISIBLE);
                viewEnd.setText("Please Connect to Internet and Tap Here to Start Fetching News");
                viewEnd.setTextSize(16);
                viewEnd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        getActivity().recreate();
                    }
                });
                showingTopNewsFor.setVisibility(View.GONE);

            }

        }

        return view;
    }

    public void responseCall(String lan, String cou, String cate) {
        //TODO Implement if changed URL over last time

        gson = new Gson();
        service = Client.getClient().create(GetTopNewsWorldEnglish.class);
        Call<CompleteResponse> call = service.getTopNewsArticles(KEY, lan, cou, cate);
        String requestUrl = call.request().url().toString();
        boolean matchesUrl = requestUrl.equals(prefs.getString("previousUrl", ""));
        String resp = prefs.getString("topnews", "");
        boolean tCheck = (System.currentTimeMillis() - prefs.getLong("topNewsFetchedAt", 0) < 10800000);

        if (!matchesUrl) {
            resp = "";
            tCheck = false;
        }
        a1 = new List[]{new ArrayList<>()};

        if ((!resp.equals("") && !resp.equals("[]")) || (tCheck)) {

            Type type = new TypeToken<List<Articles>>() {
            }.getType();

            a1[0] = gson.fromJson(resp, type);

            adapter = new NewsRecyclerAdapter(a1[0]);
            topNewsRecycler.setAdapter(adapter);

            spinningProgress.setVisibility(View.GONE);
            topNewsRecycler.setVisibility(View.VISIBLE);
            view.findViewById(R.id.textViewEnd).setVisibility(View.VISIBLE);
            showingTopNewsFor.setVisibility(View.VISIBLE);

        } else {

            call.enqueue(new Callback<CompleteResponse>() {
                @Override
                public void onResponse(Call<CompleteResponse> call, Response<CompleteResponse> response) {
                    Log.d("Check Response", String.valueOf(call.request().url()));
                    editor.putString("previousUrl", call.request().url().toString());
                    editor.commit();

                    a1[0] = response.body().getArticles();
                    dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(getContext()));

                    spinningProgress.setVisibility(View.GONE);
                    topNewsRecycler.setVisibility(View.VISIBLE);
                    view.findViewById(R.id.textViewEnd).setVisibility(View.VISIBLE);
                    showingTopNewsFor.setVisibility(View.VISIBLE);

                    if (a1[0].size() == 0) {
                        viewEnd.setText("Settings for Top News might be fetching 0 results. Please try changing Top News settings");
                        viewEnd.setVisibility(View.VISIBLE);
                        dispatcher.cancel(JOB_TAG);
                        editor.putString("topnews", "");
                        editor.putLong("topNewsFetchedAt", 108000000);
                        editor.commit();

                    } else {
                        editor.putBoolean("noNews", false);
                        editor.commit();
                        adapter = new NewsRecyclerAdapter(a1[0]);
                        topNewsRecycler.setAdapter(adapter);


                        String json = gson.toJson(a1[0]);
                        editor.putString("topnews", json);
                        editor.putLong("topNewsFetchedAt", System.currentTimeMillis());
                        editor.commit();


                        try {
                            dispatcher.cancel(JOB_TAG);  // TODO if articles are zero
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        Job job = dispatcher.newJobBuilder().
                                setService(FetchTopNewsService.class)
                                .setLifetime(Lifetime.FOREVER)
                                .setRecurring(true)
                                .setTag(JOB_TAG)
                                .setTrigger(Trigger.executionWindow(0, 10800))
                                .setRetryStrategy(RetryStrategy.DEFAULT_EXPONENTIAL)
                                .setReplaceCurrent(false).setConstraints(Constraint.ON_ANY_NETWORK)
                                .build();

                        dispatcher.mustSchedule(job);

                    }
                }

                @Override
                public void onFailure(Call<CompleteResponse> call, Throwable t) {
                    t.printStackTrace();
                }
            });

        }
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

    @Override
    public void onStart() {
        super.onStart();
        Log.d("onStart Check", "Here");
        //TODO Try to implement to load data while Settings Changed and Service Runs
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("onStop Check", "Here");
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

}
