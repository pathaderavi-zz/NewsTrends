package adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ravikiranpathade.newstrends.R;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by ravikiranpathade on 12/21/17.
 */

public class KeywordsAdapter extends ArrayAdapter<String> {
    List<String> allString;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    public KeywordsAdapter(@NonNull Context context, List<String> strings) {

        super(context, R.layout.custom_row_keywords, strings);
        allString = strings;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.custom_row_keywords, parent, false);
        preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        editor = preferences.edit();

        String text = getItem(position);
        TextView t = view.findViewById(R.id.keywordText);
        t.setText(text);
        ImageView imageView = view.findViewById(R.id.deleteImage);
        imageView.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NewApi")
            @Override
            public void onClick(View view) {
                allString.remove(position);

                JSONArray j = new JSONArray();
                for (int i = 0; i < allString.size(); i++) {
                    j.put(allString.get(i));
                }
                editor.putString(getContext().getResources().getString(R.string.jArrayWords), j.toString());
                editor.commit();


                notifyDataSetChanged();
            }
        });

        return view;
    }
}
