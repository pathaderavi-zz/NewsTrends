package adapters;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.Image;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.ravikiranpathade.newstrends.R;
import com.example.ravikiranpathade.newstrends.activities.NewsDetailActivity;

import java.io.File;

/**
 * Created by ravikiranpathade on 12/26/17.
 */

public class NewsCursorAdapter extends CursorAdapter {
    String check;
    public NewsCursorAdapter(Context context, Cursor c,String from) {
        super(context, c, 0);
        check = from;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return LayoutInflater.from(context).inflate(R.layout.news_item_cardview, viewGroup, false);
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        ImageView newsCardImage = view.findViewById(R.id.news_card_image);
        TextView headline = view.findViewById(R.id.headline_card);
        TextView author = view.findViewById(R.id.author_card);
        CardView cardView = view.findViewById(R.id.news_card);

        final String cursorId = String.valueOf(cursor.getInt(cursor.getColumnIndex("_id")));
        final String title_string = cursor.getString(cursor.getColumnIndex("TITLE"));
        final String desc_string= cursor.getString(cursor.getColumnIndex("DESCRIPTION"));
        final String imageUrl_string = context.getFilesDir().getAbsolutePath()
                + File.separator + "images"+File.separator+ String.valueOf(cursorId) + ".jpg";
        final String urlArticle_string = cursor.getString(cursor.getColumnIndex("URL"));
        final String author_string = cursor.getString(cursor.getColumnIndex("AUTHOR"));
        final String publishedAt_string = cursor.getString(cursor.getColumnIndex("PUBLISHEDAT"));
        final String source_id_string = cursor.getString(cursor.getColumnIndex("SOURCEID"));
        final String source_name_string = cursor.getString(cursor.getColumnIndex("SOURCENAME"));

        //TODO Load Images for Alerts and Favorites Separately

        final String imageUrl_string_web = cursor.getString(cursor.getColumnIndex("URL"));


        if(author_string!=null){
            author.setText("by "+author_string+" at "+source_name_string);
        }
        if (imageUrl_string != null) {
            if(check==null) {
                Glide.with(context).load(imageUrl_string).centerCrop().diskCacheStrategy(DiskCacheStrategy.NONE).into(newsCardImage);
            }else{
                Glide.with(context).load(imageUrl_string_web).centerCrop().diskCacheStrategy(DiskCacheStrategy.NONE).into(newsCardImage);

            }
        }

        headline.setText(title_string);
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context,NewsDetailActivity.class);

                i.putExtra("list_id", Integer.parseInt(cursorId));
                i.putExtra("title",title_string);
                i.putExtra("description",desc_string);
                i.putExtra("urlToImage",imageUrl_string);
                i.putExtra("url",urlArticle_string);
                i.putExtra("author",author_string);
                i.putExtra("publishedAt",publishedAt_string);
                i.putExtra("source_id",source_id_string);
                i.putExtra("source_name",source_name_string);

                context.startActivity(i);
            }
        });
    }
}
