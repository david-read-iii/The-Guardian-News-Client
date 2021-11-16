package com.davidread.newsfeed;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * {@link ArticleViewHolder} is a model class that describes a single item view and metadata about
 * its place within a {@link RecyclerView}.
 */
public class ArticleViewHolder extends RecyclerView.ViewHolder {

    /**
     * {@link TextView} to hold the title of an article.
     */
    private final TextView titleTextView;

    /**
     * {@link TextView} to hold the name of the section the article is from.
     */
    private final TextView sectionNameTextView;

    /**
     * {@link TextView} to hold the date the article was published.
     */
    private final TextView datePublishedTextView;

    /**
     * Constructs a new {@link ArticleViewHolder} object given a {@link View} object
     *
     * @param itemView  {@link View} to be held in the {@link ArticleViewHolder}.
     */
    public ArticleViewHolder(@NonNull View itemView) {
        super(itemView);
        titleTextView = itemView.findViewById(R.id.title_text_view);
        sectionNameTextView = itemView.findViewById(R.id.section_name_text_view);
        datePublishedTextView = itemView.findViewById(R.id.date_published_text_view);
    }

    /**
     * Returns the {@link TextView} holding the title of an article.
     */
    public TextView getTitleTextView() {
        return titleTextView;
    }

    /**
     * Returns the {@link TextView} holding the name of the section the article is from.
     */
    public TextView getSectionNameTextView() {
        return sectionNameTextView;
    }

    /**
     * Returns the {@link TextView} holding the date the article was published.
     */
    public TextView getDatePublishedTextView() {
        return datePublishedTextView;
    }
}
