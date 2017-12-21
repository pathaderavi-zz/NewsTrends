package adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ravikiranpathade.newstrends.R;

import java.util.List;

import models.Articles;

/**
 * Created by ravikiranpathade on 12/20/17.
 */

public class NewsCursorAdapter extends RecyclerView.Adapter<NewsCursorAdapter.NewsCursorHolder> {
    List<Articles> articles;
    Context adapterContext;
    Context holderContext;

    public NewsCursorAdapter(List<Articles> articlesList) {
        articles = articlesList;
    }

    @Override
    public NewsCursorHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layoutID = R.layout.news_item_cardview;
        adapterContext = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(adapterContext);
        View view = inflater.inflate(layoutID,parent);
        NewsCursorHolder holder = new NewsCursorHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(NewsCursorHolder holder, int position) {
            holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return articles.size();
    }
    public void setCursorDataChanged(List<Articles> cursorDataChanged){
        articles = cursorDataChanged;
        notifyDataSetChanged();
    }

    class NewsCursorHolder extends RecyclerView.ViewHolder {
        ImageView newsCardImage;
        TextView headline;
        TextView author;
        CardView cardView;

        public NewsCursorHolder(View itemView) {
            super(itemView);
            holderContext = itemView.getContext();

            newsCardImage = itemView.findViewById(R.id.news_card_image);
            headline = itemView.findViewById(R.id.headline_card);
            author = itemView.findViewById(R.id.author_card);
            cardView = itemView.findViewById(R.id.news_card);
        }

        public void bind(int position) {
        }
    }
}
