package com.davidread.newsfeed;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link ArticleAdapter} is an adapter class that provides a binding from a {@link List} of
 * {@link Article} objects to views that are displayed within a {@link RecyclerView}.
 */
public class ArticleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    /**
     * Constants representing the view types that the adapter returns.
     */
    public static final int VIEW_TYPE_ARTICLE = 0;
    public static final int VIEW_TYPE_LOADING = 1;

    /**
     * {@link List} of {@link Article} objects being adapted.
     */
    private List<Article> articles;

    /**
     * Boolean representing whether a loading view is being adapted at the end of the
     * {@link RecyclerView}.
     */
    private boolean loadingViewVisible;

    /**
     * Constructs a new {@link ArticleAdapter} object.
     */
    public ArticleAdapter() {
        this.articles = new ArrayList<>();
        this.loadingViewVisible = false;
    }

    /**
     * Called when the {@link RecyclerView} needs a new {@link RecyclerView.ViewHolder} to represent
     * either an {@link Article} object or a loading view.
     *
     * @param parent   {@link ViewGroup} into which the new {@link View} will be added after it is
     *                 bound to an adapter position.
     * @param viewType The view type of the new {@link View}.
     * @return A new {@link RecyclerView.ViewHolder}.
     */
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ARTICLE) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_article, parent, false);
            return new ArticleViewHolder(itemView);
        } else {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_loading, parent, false);
            return new LoadingViewHolder(itemView);
        }
    }

    /**
     * Called when the {@link RecyclerView} to bind data to a {@link RecyclerView.ViewHolder} object
     * at a certain position index in the adapter. Only {@link ArticleViewHolder} objects need
     * to be bound with data about their corresponding {@link Article} object.
     *
     * @param holder   {@link RecyclerView.ViewHolder} to be bound.
     * @param position The {@link RecyclerView.ViewHolder} object's position index in the adapter.
     */
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ArticleViewHolder) {
            Article article = articles.get(position);
            ArticleViewHolder articleViewHolder = (ArticleViewHolder) holder;
            articleViewHolder.getTitleTextView().setText(article.getTitle());
            articleViewHolder.getSectionNameTextView().setText(article.getSectionName());
            articleViewHolder.getDatePublishedTextView().setText(article.getDatePublished());
        }
    }

    /**
     * Returns the total number of items this adapter is adapting. Items include the {@link Article}
     * objects and the loading view.
     *
     * @return The total number of items this adapter is adapting.
     */
    @Override
    public int getItemCount() {
        if (loadingViewVisible) {
            return articles.size() + 1;
        } else {
            return articles.size();
        }
    }

    /**
     * Returns an int representing the view type of the item given its position in the adapter.
     *
     * @param position The items position index in the adapter.
     * @return The view type of the item.
     */
    @Override
    public int getItemViewType(int position) {
        if (position < articles.size()) {
            return VIEW_TYPE_ARTICLE;
        } else {
            return VIEW_TYPE_LOADING;
        }
    }

    /**
     * Returns the {@link Article} object given its position index in the adapter.
     *
     * @param position The {@link Article} object's position index in the adapter.
     * @return {@link Article} object given its adapter position index.
     */
    public Article getArticle(int position) {
        return articles.get(position);
    }

    /**
     * Adds a {@link List} of {@link Article} objects to the end of the {@link List} of objects
     * being adapted.
     *
     * @param newArticles {@link List} of new {@link Article} objects to be adapted.
     */
    public void addAllArticles(List<Article> newArticles) {
        articles.addAll(newArticles);
        notifyItemRangeInserted(getItemCount(), newArticles.size());
    }

    /**
     * Resets the {@link List} of objects being adapted.
     */
    public void resetArticles() {
        this.articles = new ArrayList<>();
        notifyItemRangeRemoved(0, getItemCount());
    }

    /**
     * Adds a loading view to the end of the adapter.
     */
    public void showLoadingView() {
        loadingViewVisible = true;
        notifyItemInserted(getItemCount() - 1);
    }

    /**
     * Removes the loading view from the end of the adapter.
     */
    public void hideLoadingView() {
        loadingViewVisible = false;
        notifyItemRemoved(getItemCount());
    }

    /**
     * {@link ArticleViewHolder} is a model class that describes a single article item view and
     * metadata about its place within a {@link RecyclerView}.
     */
    private static class ArticleViewHolder extends RecyclerView.ViewHolder {

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
         * Constructs a new {@link ArticleViewHolder}.
         *
         * @param itemView {@link View} to be held in the {@link ArticleViewHolder}.
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

    /**
     * {@link LoadingViewHolder} is a model class that describes a single loading item view and
     * metadata about its place within a {@link RecyclerView}.
     */
    private static class LoadingViewHolder extends RecyclerView.ViewHolder {

        /**
         * Constructs a new {@link LoadingViewHolder}.
         *
         * @param itemView {@link View} to be held in the {@link LoadingViewHolder}.
         */
        public LoadingViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
