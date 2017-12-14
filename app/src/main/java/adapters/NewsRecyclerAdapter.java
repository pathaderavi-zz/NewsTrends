package adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.ravikiranpathade.newstrends.R;
import com.example.ravikiranpathade.newstrends.activities.NewsDetailActivity;

import java.util.ArrayList;
import java.util.List;

import models.Articles;

/**
 * Created by ravikiranpathade on 12/13/17.
 */

public class NewsRecyclerAdapter extends RecyclerView.Adapter<NewsRecyclerAdapter.NewsListViewHolder> {

    List<Articles> articles;

    Context holderContext;
    Context recyclerContext;

    public NewsRecyclerAdapter(List<Articles> articlesList) {
        this.articles = articlesList;
        Log.d("Check List",String.valueOf(articlesList.get(0).getSource()==null)
        );
    }

    @Override
    public NewsListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layoutId = R.layout.news_item_cardview;
        recyclerContext = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(recyclerContext);

        View view = inflater.inflate(layoutId, parent, false);

        NewsListViewHolder holder = new NewsListViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(NewsListViewHolder holder, int position) {
        holder.bind(position);

    }

    @Override
    public int getItemCount() {
        return articles.size();
    }

    public void setDataArticles(List<Articles> articles) {
        this.articles = articles;
        notifyDataSetChanged();
    }

    class NewsListViewHolder extends RecyclerView.ViewHolder {
        ImageView newsCardImage;
        TextView headline;
        TextView author;
        TextView description;
        CardView cardView;

        public NewsListViewHolder(View itemView) {
            super(itemView);
            holderContext = itemView.getContext();

            newsCardImage = itemView.findViewById(R.id.news_card_image);
            headline = itemView.findViewById(R.id.headline_card);
            author = itemView.findViewById(R.id.author_card);
            description = itemView.findViewById(R.id.description_card);
            cardView = itemView.findViewById(R.id.news_card);
        }

        public void bind(final int position) {
            final Articles article = articles.get(position);

            if (article.getUrlToImage() != null) {

                Glide.with(holderContext).load(article.getUrlToImage()).into(newsCardImage);
            }
            headline.setText(article.getTitle());
            if(article.getAuthor()!=null){
                author.setText("by "+article.getAuthor());
            }


            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(holderContext,NewsDetailActivity.class);
                    i.putExtra("list_id",position);
                    i.putExtra("title",article.getTitle());
                    i.putExtra("description",article.getDescription());
                    i.putExtra("urlToImage",article.getUrlToImage());
                    holderContext.startActivity(i);
                }
            });
           // description.setText(article.getDescription());
        }
    }
}
