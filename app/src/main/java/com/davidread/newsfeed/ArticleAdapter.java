package com.davidread.newsfeed;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link ArticleAdapter} is an adapter class that provides a binding from a {@link List} of
 * {@link Article} objects to views that are displayed within a {@link RecyclerView}.
 */
public class ArticleAdapter extends RecyclerView.Adapter<ArticleViewHolder> {

    /**
     * {@link List} of {@link Article} objects being adapted.
     */
    private List<Article> articles;

    /**
     * Constructs a new {@link ArticleAdapter} object.
     */
    public ArticleAdapter() {
        this.articles = new ArrayList<>();
    }

    /**
     * Called when the {@link RecyclerView} needs a new {@link ArticleViewHolder} to represent an
     * {@link Article} object.
     *
     * @param parent   {@link ViewGroup} into which the new {@link View} will be added after it is
     *                 bound to an adapter position.
     * @param viewType The view type of the new {@link View}.
     * @return A new {@link ArticleViewHolder}.
     */
    @NonNull
    @Override
    public ArticleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_article, parent, false);
        return new ArticleViewHolder(itemView);
    }

    /**
     * Called when the {@link RecyclerView} to display the data at the specified position. It
     * updates the contents of the given {@link ArticleViewHolder} to reflect the {@link Article}
     * object at the given position.
     *
     * @param holder   {@link ArticleViewHolder} to be updated.
     * @param position int representing the position of the {@link Article} object within the
     *                 adapter's {@link List} of {@link Article} objects.
     */
    @Override
    public void onBindViewHolder(@NonNull ArticleViewHolder holder, int position) {
        Article article = articles.get(position);
        holder.getTitleTextView().setText(article.getTitle());
        holder.getSectionNameTextView().setText(article.getSectionName());
        holder.getDatePublishedTextView().setText(article.getDatePublished());
    }

    /**
     * Returns the total number of {@link Article} objects stored in the {@link List} held by the
     * adapter.
     *
     * @return The total number of {@link Article} objects in this adapter.
     */
    @Override
    public int getItemCount() {
        return articles.size();
    }

    /**
     * Returns the {@link Article} object at the specified position in the {@link List} held by the
     * adapter.
     *
     * @param position int representing the position of the item to get.
     * @return {@link Article} object at the specified position in the {@link List} held by the
     * adapter.
     */
    public Article getItem(int position) {
        return articles.get(position);
    }

    /**
     * Adds a {@link List} of {@link Article} objects to the {@link List} held by the adapter.
     *
     * @param newArticles {@link List} of {@link Article} objects to add.
     */
    public void addAll(List<Article> newArticles) {
        articles.addAll(newArticles);
        notifyItemRangeInserted(articles.size(), newArticles.size());
    }

    /**
     * Resets the {@link List} held by the adapter.
     */
    public void reset() {
        this.articles = new ArrayList<>();
        notifyItemRangeRemoved(0, articles.size());
    }
}
