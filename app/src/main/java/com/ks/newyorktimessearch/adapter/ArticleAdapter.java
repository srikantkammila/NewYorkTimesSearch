package com.ks.newyorktimessearch.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ks.newyorktimessearch.activities.DetailsActivity;
import com.ks.newyorktimessearch.R;
import com.ks.newyorktimessearch.model.Article;
import com.ks.newyorktimessearch.model.Media;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by skammila on 2/9/16.
 */
public class ArticleAdapter extends RecyclerView.Adapter<ArticleAdapter.ViewHolder> {
    private List<Article> articles;

    public ArticleAdapter(List<Article> articles) {
        this.articles = articles;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
//        @Bind(R.id.srImage)ImageView srImage;
//        @Bind(R.id.srText)TextView srText;
        ImageView srImage;
        TextView srText;

        public ViewHolder(View itemView) {
            super(itemView);

            srImage = (ImageView) itemView.findViewById(R.id.srImage);
            srText = (TextView) itemView.findViewById(R.id.srText);

//            ButterKnife.bind(itemView);
        }
    }

    @Override
    public ArticleAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View articleView = inflater.inflate(R.layout.item_search_result, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(articleView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        //Get the data model based on position
        final Article article = articles.get(position);

        // Set item views based on the data model
        TextView textView = viewHolder.srText;
        if (textView != null) {
            textView.setText(article.getHeadline());
        }

        Media srMedia = article.getRVMedia();
        ImageView imageView = viewHolder.srImage;
        if (srMedia != null) {
            String url = srMedia != null ? "http://www.nytimes.com/" + srMedia.getUrl() : "";
            Picasso.with(imageView.getContext()).load(url).placeholder(R.drawable.progress_large).into(imageView);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent in = new Intent(v.getContext(), DetailsActivity.class);
                    in.putExtra("web_url", article.getWebUrl());
                    v.getContext().startActivity(in);
                }
            });


        }

        viewHolder.srText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(v.getContext(), DetailsActivity.class);
                in.putExtra("web_url", article.getWebUrl());
                v.getContext().startActivity(in);
            }
        });
    }

    @Override
    public int getItemCount() {
        return articles.size();
    }
}
