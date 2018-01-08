package fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.androidfung.geoip.IpApiService;
import com.androidfung.geoip.ServicesManager;
import com.androidfung.geoip.model.GeoIpResponseModel;

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.RetryStrategy;
import com.firebase.jobdispatcher.Trigger;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import adapters.NewsRecyclerAdapter;
import models.Articles;
import models.CompleteResponse;
import ravikiran.pathade.ravikiranpathade.newstrends.R;
import ravikiran.pathade.ravikiranpathade.newstrends.activities.SettingsActivity;
import rest.Client;
import rest.GetTopNewsWorldEnglish;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import services.FetchTopNewsService;
import services.WidgetUpdateService;
import utils.HelperFunctions;

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

    public String KEY;
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
    private FirebaseAnalytics mFirebaseAnalytics;
    TextView changeSettingsTextView;

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
        KEY = getActivity().getResources().getString(R.string.API_KEY);
        a1 = new List[]{new ArrayList<>()};
        view = inflater.inflate(R.layout.fragment_top_news, container, false);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(getContext());
        isConnected = new HelperFunctions().getConnectionInfo(getContext());
        viewEnd = view.findViewById(R.id.textViewEnd);
        layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        topNewsRecycler = view.findViewById(R.id.topNewsRecycler);
        changeSettingsTextView = view.findViewById(R.id.topNewsChangeSettingsTextView);

        final Intent settingsIntent = new Intent(getContext(), SettingsActivity.class);
        settingsIntent.putExtra( PreferenceActivity.EXTRA_SHOW_FRAGMENT, SettingsActivity.GeneralPreferenceFragment.class.getName() );
        settingsIntent.putExtra( PreferenceActivity.EXTRA_NO_HEADERS, true );
        changeSettingsTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(settingsIntent);
            }
        });

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
        changeSettingsTextView.setVisibility(View.INVISIBLE);


        prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        editor = prefs.edit();

        country = prefs.getString(getContext().getResources().getString(R.string.pref_country_key), getContext().getResources().getString(R.string.empty_string));

        language = prefs.getString(getContext().getResources().getString(R.string.pref_language_key), getContext().getResources().getString(R.string.empty_string));

        category = prefs.getString(getContext().getResources().getString(R.string.pref_category_key), getContext().getResources().getString(R.string.empty_string));

        if (String.valueOf(language).equals(getContext().getResources().getString(R.string.null_value_string)) || String.valueOf(language).equals(getContext().getResources().getString(R.string.empty_string))
                || String.valueOf(language).equals(getContext().getResources().getString(R.string.zero))) {
            if (String.valueOf(language).equals(getContext().getResources().getString(R.string.null_value_string))) {
                language = getContext().getResources().getString(R.string.empty_string);
            } else {
                language = getContext().getResources().getString(R.string.english);
            }
            editor.putString(getContext().getResources().getString(R.string.pref_language_key), language);
            editor.commit();
        }
        if (String.valueOf(category).equals(getContext().getResources().getString(R.string.null_value_string)) || String.valueOf(category).equals(getContext().getResources().getString(R.string.zero)) || category.equals(getContext().getResources().getString(R.string.empty_string))) {
            category = getContext().getResources().getString(R.string.empty_string);
            editor.putString(getContext().getResources().getString(R.string.pref_category_key), getContext().getResources().getString(R.string.empty_string));
        }
        final String[] countryList = getActivity().getResources().getStringArray(R.array.preferenceCountryValues);
        gson = new Gson();
        final Context contextCheck = getContext();
        a1[0] = new ArrayList<>();
        if (isConnected) {
            if (String.valueOf(country).equals(getContext().getResources().getString(R.string.null_value_string)) || String.valueOf(country).equals(getContext().getResources().getString(R.string.zero)) || country.equals(getContext().getResources().getString(R.string.empty_string))) { // Code Not Set
                if (country.equals(getContext().getResources().getString(R.string.null_value_string))) {
                    country = getContext().getResources().getString(R.string.empty_string);
                    responseCall(language, country, category);
                } else {
                    IpApiService ipApiService = ServicesManager.getGeoIpService();
                    ipApiService.getGeoIp().enqueue(new Callback<GeoIpResponseModel>() {
                        @Override
                        public void onResponse(Call<GeoIpResponseModel> call, Response<GeoIpResponseModel> response) {
                            country = response.body().getCountryCode();

                            Bundle bundle = new Bundle();

                            bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, contextCheck.getResources().getString(R.string.firebase_country));
                            bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, response.body().getCountry());

                            bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, contextCheck.getResources().getString(R.string.firebase_city));
                            bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, response.body().getCity());

                            mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);


                            boolean checkCountryMatch = false;
                            for (String c : countryList) {
                                if (c.equals(country.toLowerCase())) {
                                    checkCountryMatch = true;
                                }
                            }
                            if (checkCountryMatch) {
                                responseCall(language, country, category);
                            } else {
                                responseCall(language, contextCheck.getResources().getString(R.string.empty_string), category);
                            }
                        }

                        @Override
                        public void onFailure(Call<GeoIpResponseModel> call, Throwable t) {

                        }
                    });

                }
            } else { // Code is Set
                responseCall(language, country, category);
            }
        } else {
            String resp = prefs.getString(getContext().getResources().getString(R.string.topnews_key), getContext().getResources().getString(R.string.empty_string));
            if ((!resp.equals(getContext().getResources().getString(R.string.empty_string)) && !resp.equals(getContext().getResources().getString(R.string.empty_array)))) {
                Type type = new TypeToken<List<Articles>>() {
                }.getType();

                a1[0] = gson.fromJson(resp, type);

                adapter = new NewsRecyclerAdapter(a1[0]);
                topNewsRecycler.setAdapter(adapter);

                spinningProgress.setVisibility(View.GONE);
                topNewsRecycler.setVisibility(View.VISIBLE);
                view.findViewById(R.id.textViewEnd).setVisibility(View.VISIBLE);
                showingTopNewsFor.setVisibility(View.VISIBLE);
                changeSettingsTextView.setVisibility(View.VISIBLE);
                if (country.equals(getContext().getResources().getString(R.string.empty_string)) || country.equals(getContext().getResources().getString(R.string.null_value_string)) || country.equals(getContext().getResources().getString(R.string.zero))) {
                    country = getContext().getResources().getString(R.string.World);
                } else {
                    showingTopNewsFor.setText(getContext().getResources().getString(R.string.showing_results_for) + new Locale(getContext().getResources().getString(R.string.empty_string), country).getDisplayCountry().toUpperCase());
                }
            } else {
                spinningProgress.setVisibility(View.GONE);
                topNewsRecycler.setVisibility(View.VISIBLE);

                viewEnd.setVisibility(View.VISIBLE);
                viewEnd.setText(getContext().getResources().getString(R.string.no_internet_topnews));
                viewEnd.setTextSize(16);
                viewEnd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        getActivity().recreate();
                    }
                });
                showingTopNewsFor.setVisibility(View.GONE);
                changeSettingsTextView.setVisibility(View.GONE);

            }

        }

        return view;
    }

    public void responseCall(String lan, String cou, String cate) {

        a1[0] = new ArrayList<>();
        gson = new Gson();
        service = Client.getClient().create(GetTopNewsWorldEnglish.class);
        Call<CompleteResponse> call = service.getTopNewsArticles(KEY, lan, cou, cate);

        String requestUrl = call.request().url().toString();
        boolean matchesUrl = requestUrl.equals(prefs.getString(getContext().getResources().getString(R.string.previousUrl), getContext().getResources().getString(R.string.empty_string)));
        String resp = prefs.getString(getContext().getResources().getString(R.string.topnews_key), getContext().getResources().getString(R.string.empty_string));
        boolean tCheck = (System.currentTimeMillis() - prefs.getLong(getContext().getResources().getString(R.string.topNewsFetchedAt), 0) < 10800000);

        if (!matchesUrl) {
            resp = getContext().getResources().getString(R.string.empty_string);
            tCheck = false;
        }
        a1 = new List[]{new ArrayList<>()};

        if ((!resp.equals(getContext().getResources().getString(R.string.empty_string)) && !resp.equals(getContext().getResources().getString(R.string.empty_array))) || (tCheck)) {

            Type type = new TypeToken<List<Articles>>() {
            }.getType();

            a1[0] = gson.fromJson(resp, type);

            adapter = new NewsRecyclerAdapter(a1[0]);
            topNewsRecycler.setAdapter(adapter);

            spinningProgress.setVisibility(View.GONE);
            topNewsRecycler.setVisibility(View.VISIBLE);
            view.findViewById(R.id.textViewEnd).setVisibility(View.VISIBLE);
            showingTopNewsFor.setVisibility(View.VISIBLE);
            changeSettingsTextView.setVisibility(View.VISIBLE);

            if (cou.equals(getContext().getResources().getString(R.string.empty_string)) || cou.equals(getContext().getResources().getString(R.string.empty_array)) || cou.equals(getContext().getResources().getString(R.string.zero))) {
                showingTopNewsFor.setText(getContext().getResources().getString(R.string.showing_news_for_world));
            } else {
                showingTopNewsFor.setText(getContext().getResources().getString(R.string.showing_results_for) + new Locale(getContext().getResources().getString(R.string.empty_string), cou).getDisplayCountry().toUpperCase());
            }

        } else {
            final String countryCheckString = cou;
            call.enqueue(new Callback<CompleteResponse>() {
                @Override
                public void onResponse(Call<CompleteResponse> call, Response<CompleteResponse> response) {

                    editor.putString(getContext().getResources().getString(R.string.previousUrl), call.request().url().toString());
                    editor.commit();

                    a1[0] = response.body().getArticles();
                    dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(getContext()));

                    spinningProgress.setVisibility(View.GONE);
                    topNewsRecycler.setVisibility(View.VISIBLE);
                    view.findViewById(R.id.textViewEnd).setVisibility(View.VISIBLE);
                    showingTopNewsFor.setVisibility(View.VISIBLE);
                    changeSettingsTextView.setVisibility(View.VISIBLE);


                    if (a1[0].size() == 0) {
                        viewEnd.setText(getContext().getResources().getString(R.string.settings_0_results));
                        viewEnd.setVisibility(View.VISIBLE);
                        dispatcher.cancel(JOB_TAG);
                        editor.putString(getContext().getResources().getString(R.string.topnews_key), getContext().getResources().getString(R.string.empty_string));
                        editor.putLong(getContext().getResources().getString(R.string.topNewsFetchedAt), 108000000);
                        editor.commit();
                        showingTopNewsFor.setVisibility(View.GONE);
                        changeSettingsTextView.setVisibility(View.GONE);

                    } else {
                        editor.putBoolean(getContext().getResources().getString(R.string.no_news_key), false);
                        editor.commit();
                        adapter = new NewsRecyclerAdapter(a1[0]);
                        topNewsRecycler.setAdapter(adapter);


                        String json = gson.toJson(a1[0]);
                        editor.putString(getContext().getResources().getString(R.string.topnews_key), json);
                        editor.putLong(getContext().getResources().getString(R.string.topNewsFetchedAt), System.currentTimeMillis());
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
                        if (countryCheckString.equals(getContext().getResources().getString(R.string.empty_string)) || countryCheckString.equals(getContext().getResources().getString(R.string.null_value_string)) || countryCheckString.equals(getContext().getResources().getString(R.string.zero))) {
                            showingTopNewsFor.setText(getContext().getResources().getString(R.string.showing_news_for_world));
                        } else {
                            showingTopNewsFor.setText(getContext().getResources().getString(R.string.showing_results_for) + new Locale(getContext().getResources().getString(R.string.empty_string), countryCheckString).getDisplayCountry().toUpperCase());
                        }

                    }
                    WidgetUpdateService updateWidget = new WidgetUpdateService();
                    updateWidget.updateWidget(getActivity());
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

        //TODO Try to implement to load data while Settings Changed and Service Runs
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

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
