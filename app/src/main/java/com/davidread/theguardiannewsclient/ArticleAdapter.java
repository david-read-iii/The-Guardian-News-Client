package com.davidread.theguardiannewsclient;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * {@link ArticleAdapter} is an adapter class that provides a binding from a {@link List} of
 * {@link Article} objects to views that are displayed within a {@link RecyclerView}. It also
 * allows a single footer view to be shown below the adapted {@link Article} objects.
 */
public class ArticleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    /**
     * {@link String} log tag name for {@link ArticleAdapter}.
     */
    public static final String LOG_TAG_NAME = ArticleAdapter.class.getSimpleName();

    /**
     * Constants representing the view types that the adapter returns.
     */
    public static final int VIEW_TYPE_UNDEFINED = -1;
    public static final int VIEW_TYPE_ARTICLE = 0;
    public static final int VIEW_TYPE_LOADING = 1;
    public static final int VIEW_TYPE_ERROR = 2;
    public static final int VIEW_TYPE_END_OF_LIST = 3;

    /**
     * {@link Context} for accessing string resources.
     */
    private final Context context;

    /**
     * {@link List} of {@link Article} objects being adapted.
     */
    private List<Article> articles;

    /**
     * Boolean representing whether a loading view is being adapted as the footer view.
     */
    private boolean loadingViewVisible;

    /**
     * Boolean representing whether an error view is being adapted as the footer view.
     */
    private boolean errorViewVisible;

    /**
     * Boolean representing whether an end of list view is being adapted as the footer view.
     */
    private boolean endOfListViewVisible;

    /**
     * Constructs a new {@link ArticleAdapter} object.
     *
     * @param context {@link Context} for accessing string resources.
     */
    public ArticleAdapter(Context context) {
        this.context = context;
        this.articles = new ArrayList<>();
        this.loadingViewVisible = false;
        this.errorViewVisible = false;
        this.endOfListViewVisible = false;
    }

    /**
     * Called when the {@link RecyclerView} needs a new {@link RecyclerView.ViewHolder} to represent
     * either an {@link Article} object or a footer view.
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
        } else if (viewType == VIEW_TYPE_LOADING) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_loading, parent, false);
            return new FooterViewHolder(itemView);
        } else if (viewType == VIEW_TYPE_ERROR) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_error, parent, false);
            return new FooterViewHolder(itemView);
        } else if (viewType == VIEW_TYPE_END_OF_LIST) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_end_of_list, parent, false);
            return new FooterViewHolder(itemView);
        } else {
            return new FooterViewHolder(new View(parent.getContext()));
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
            ArticleViewHolder articleViewHolder = (ArticleViewHolder) holder;
            Article article = articles.get(position);
            articleViewHolder.getTitleTextView().setText(article.getTitle());
            articleViewHolder.getAuthorsTextView().setText(getFormattedAuthorsString(article.getAuthors()));
            articleViewHolder.getSectionNameTextView().setText(article.getSectionName());
            articleViewHolder.getDatePublishedTextView().setText(getFormattedDatePublishedString(article.getDatePublished()));
        }
    }

    /**
     * Returns the total number of items this adapter is adapting. Items include the {@link Article}
     * objects, the loading view, and the error view.
     *
     * @return The total number of items this adapter is adapting.
     */
    @Override
    public int getItemCount() {
        int itemCount = articles.size();
        if (loadingViewVisible || errorViewVisible || endOfListViewVisible) {
            itemCount++;
        }
        return itemCount;
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
        } else if (loadingViewVisible) {
            return VIEW_TYPE_LOADING;
        } else if (errorViewVisible) {
            return VIEW_TYPE_ERROR;
        } else if (endOfListViewVisible) {
            return VIEW_TYPE_END_OF_LIST;
        } else {
            return VIEW_TYPE_UNDEFINED;
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
     * Returns the {@link List} of {@link Article} objects stored in the adapter.
     *
     * @return {@link List} of {@link Article} objects stored in the adapter.
     */
    public List<Article> getArticles() {
        return articles;
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
        int articlesItemCount = articles.size();
        articles = new ArrayList<>();
        notifyItemRangeRemoved(0, articlesItemCount);
    }

    /**
     * Adds a footer view of the specified view type to the adapter. Only one footer view may be
     * shown at once.
     *
     * @param viewType Int representing the type of view to be shown.
     */
    public void showFooterView(int viewType) {

        if (loadingViewVisible || errorViewVisible || endOfListViewVisible) {
            hideFooterView();
        }

        if (viewType == VIEW_TYPE_LOADING) {
            loadingViewVisible = true;
        } else if (viewType == VIEW_TYPE_ERROR) {
            errorViewVisible = true;
        } else if (viewType == VIEW_TYPE_END_OF_LIST) {
            endOfListViewVisible = true;
        } else {
            return;
        }
        notifyItemInserted(getItemCount() - 1);
    }

    /**
     * Removes the footer view from the end of the adapter.
     */
    public void hideFooterView() {
        if (loadingViewVisible) {
            loadingViewVisible = false;
            notifyItemRemoved(getItemCount());
        } else if (errorViewVisible) {
            errorViewVisible = false;
            notifyItemRemoved(getItemCount());
        } else if (endOfListViewVisible) {
            endOfListViewVisible = false;
            notifyItemRemoved(getItemCount());
        }
    }

    /**
     * Returns a {@link String} containing a formatted version of an authors {@link String} array.
     * This format simply presents the items of the array in a comma delimited list. If no
     * authors are in the array, an unknown author string is returned.
     *
     * @param authors {@link String} array from a {@link Article} object.
     * @return {@link String} containing a formatted version of an authors {@link String} array.
     */
    private String getFormattedAuthorsString(String[] authors) {

        if (authors.length == 0) {
            return context.getString(R.string.unknown_author_label);
        }

        StringBuilder formattedStringBuilder = new StringBuilder();
        for (int index = 0; index < authors.length; index++) {
            if (index > 0) {
                formattedStringBuilder.append(", ");
            }
            formattedStringBuilder.append(authors[index]);
        }
        return formattedStringBuilder.toString();
    }

    /**
     * Returns a {@link String} containing a formatted version of a date published string. This
     * takes timezone offset and device date format preferences into account.
     *
     * @param unformattedString {@link String} containing the date following the
     *                          "yyyy-MM-dd'T'HH:mm:ss'Z" format pattern and given from the UTC
     *                          time zone.
     * @return {@link String} containing the date following the device's preferred date format and
     * in the device's time zone.
     */
    private String getFormattedDatePublishedString(String unformattedString) {

        // Parse a Date object from the unformatted string.
        Date date = null;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        try {
            date = simpleDateFormat.parse(unformattedString);
        } catch (ParseException e) {
            Log.e(LOG_TAG_NAME, "Error parsing \"" + unformattedString + "\" string");
        }

        // If parse is successful, return a formatted version of the Date object in the user's locale.
        if (date != null) {
            return DateFormat.getDateTimeInstance().format(date);
        }

        // If parse is unsuccessful, just return the unformatted string.
        else {
            return unformattedString;
        }
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
         * {@link TextView} to hold the author of an article.
         */
        private final TextView authorsTextView;

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
            authorsTextView = itemView.findViewById(R.id.authors_text_view);
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
         * Returns the {@link TextView} holding the author of an article.
         */
        public TextView getAuthorsTextView() {
            return authorsTextView;
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
     * {@link FooterViewHolder} is a model class that describes a single footer item view and
     * metadata about its place within a {@link RecyclerView}.
     */
    private static class FooterViewHolder extends RecyclerView.ViewHolder {

        /**
         * Constructs a new {@link FooterViewHolder}.
         *
         * @param itemView {@link View} to be held in the {@link FooterViewHolder}.
         */
        public FooterViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
