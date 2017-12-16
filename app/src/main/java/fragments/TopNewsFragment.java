package fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ravikiranpathade.newstrends.R;
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_top_news, container, false);

        layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        topNewsRecycler = view.findViewById(R.id.topNewsRecycler);

        topNewsRecycler.setLayoutManager(layoutManager);

        List<Articles> test = new ArrayList<>();
        //test.add(new Articles("Ravi","Check Title","Check Description","","https://imgssl.constantcontact.com/kb/next-gen-image-link-editor-steps34.png","Chekc Date"));

        prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        editor = prefs.edit();
        final Gson gson = new Gson();

        final GetTopNewsWorldEnglish service = Client.getClient().create(GetTopNewsWorldEnglish.class);
        Call<CompleteResponse> call = service.getTopNewsArticles(KEY, "en");

        final List<Articles>[] a1 = new List[]{new ArrayList<>()};
//        String resp = prefs.getString("topnews","");
//        Type type = new TypeToken<List<Articles>>(){}.getType();
//        a1[0] = gson.fromJson(resp,type);
//        adapter = new NewsRecyclerAdapter(a1[0]);
//        topNewsRecycler.setAdapter(adapter);


        call.enqueue(new Callback<CompleteResponse>() {
            @Override
            public void onResponse(Call<CompleteResponse> call, Response<CompleteResponse> response) {
                a1[0] = response.body().getArticles();

                for (int i = 0; i < a1[0].size(); i++) {
                    Articles ar = a1[0].get(i);
                    if (ar.getPublishedAt() == null) {
                        a1[0].remove(i);
                        i--;
                    }
                }

                for (int i = 0; i < a1[0].size(); i++) {
                    if (a1[0].get(i).getPublishedAt() != null ) {
                        Articles ar = a1[0].get(i);
                        Date date = DateTimeUtils.formatDate(ar.getPublishedAt());
                        ar.setPublishedDate(date);

                    }
                }

                Collections.sort(a1[0]);
                adapter = new NewsRecyclerAdapter(a1[0]);
                topNewsRecycler.setAdapter(adapter);
                String json = gson.toJson(a1[0]);
                editor.putString("topnews", json);
                editor.commit();

            }

            @Override
            public void onFailure(Call<CompleteResponse> call, Throwable t) {
                t.printStackTrace();
            }
        });


        return view;
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
}
