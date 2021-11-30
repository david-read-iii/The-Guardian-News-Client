package com.davidread.newsfeed;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    /**
     * {@link String} log tag name for {@link MainActivity}.
     */
    public static final String LOG_TAG_NAME = MainActivity.class.getSimpleName();

    /**
     * {@link String} key constants for identifying data put in instance state {@link Bundle}
     * objects.
     */
    private static final String NEXT_ARTICLE_LOADER_ID_KEY = "next_article_loader_id";
    private static final String NEXT_PAGE_INDEX_KEY = "next_page_index_key";
    private static final String EMPTY_LIST_TEXT_VIEW_VISIBILITY_KEY = "empty_list_text_view_visibility";
    private static final String RECYCLER_VIEW_CONTENT_KEY = "recycler_view_content_key";
    private static final String RECYCLER_VIEW_FOOTER_VIEW_TYPE_KEY = "recycler_view_footer_view_type";
    private static final String RECYCLER_VIEW_POSITION_KEY = "recycler_view_position";

    /**
     * {@link com.davidread.newsfeed.RecyclerViewOnItemClickListener.OnItemClickListener} defines
     * how the {@link RecyclerView} handles its itemClick event.
     */
    private final RecyclerViewOnItemClickListener.OnItemClickListener onItemClickListener = new RecyclerViewOnItemClickListener.OnItemClickListener() {

        /**
         * Handles itemClick event. On this event, the view type of the clicked item view will be
         * evaluated. If the view type is {@link ArticleAdapter#VIEW_TYPE_ARTICLE}, then an intent
         * to open the URL associated with the corresponding {@link Article} object will be started.
         * If the view type is {@link ArticleAdapter#VIEW_TYPE_ERROR}, then the error view will be
         * hidden and a new {@link ArticleLoader} will be initialized.
         *
         * @param view     {@link View} within the {@link RecyclerView} that was clicked.
         * @param position int representing the position of the view within the adapter.
         */
        @Override
        public void onItemClick(View view, int position, int viewType) {
            if (viewType == ArticleAdapter.VIEW_TYPE_ARTICLE) {

                Article article = (Article) articleAdapter.getArticle(position);
                String url = article.getUrl();

                // Do nothing if the provided URL is invalid.
                if (url == null || !URLUtil.isValidUrl(url)) {
                    return;
                }

                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivity(intent);
            } else if (viewType == ArticleAdapter.VIEW_TYPE_ERROR) {
                articleAdapter.hideFooterView();
                recyclerView.addOnScrollListener(onScrollListener);
                LoaderManager.getInstance(MainActivity.this).initLoader(nextArticleLoaderId, null, loaderCallbacks);
            }
        }
    };

    /**
     * {@link androidx.recyclerview.widget.RecyclerView.OnScrollListener} defines how the
     * {@link RecyclerView} handles its scrolled event.
     */
    private final RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {

        /**
         * Handles scrolled event. On this event, check if the last item of the {@link RecyclerView}
         * is visible. If so, initialize a new {@link ArticleLoader} to fetch more {@link Article}
         * objects to display.
         *
         * @param recyclerView  {@link RecyclerView} object being scrolled.
         * @param dx            The amount of horizontal scroll.
         * @param dy            The amount of vertical scroll.
         */
        @Override
        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            int totalItemCount = layoutManager.getItemCount();
            int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();
            if (lastVisibleItemPosition == totalItemCount - 1) {
                LoaderManager.getInstance(MainActivity.this).initLoader(nextArticleLoaderId, null, loaderCallbacks);
            }
        }
    };

    /**
     * {@link LoaderManager.LoaderCallbacks} object that defines how the {@link ArticleLoader}
     * handles its createLoader, loadFinished, and loaderReset events.
     */
    private final LoaderManager.LoaderCallbacks<List<Article>> loaderCallbacks = new LoaderManager.LoaderCallbacks<List<Article>>() {

        /**
         * Handles createLoader event. On this event, update the UI to a loading state, get the user
         * preferences for order by and search term, and return a new {@link ArticleLoader} object.
         *
         * @param id    int id of the {@link ArticleLoader} to be created.
         * @param args  {@link Bundle} object containing optional arguments for the
         *              {@link ArticleLoader}.
         * @return A new {@link ArticleLoader} object.
         */
        @NonNull
        @Override
        public Loader<List<Article>> onCreateLoader(int id, @Nullable Bundle args) {

            // Update UI to loading.
            articleAdapter.showFooterView(ArticleAdapter.VIEW_TYPE_LOADING);
            layoutManager.scrollToPosition(articleAdapter.getItemCount() - 1);

            // Get user preferences for order by and search term from SharedPreferences.
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
            String orderByPreferenceValue = sharedPreferences.getString(getString(R.string.order_by_key), getString(R.string.order_by_default_value));
            String searchTermPreferenceValue = sharedPreferences.getString(getString(R.string.search_term_key), getString(R.string.search_term_default_value));

            // Return a new ArticleLoader object.
            ArticleLoader articleLoader = new ArticleLoader(MainActivity.this, orderByPreferenceValue, nextPageIndex, searchTermPreferenceValue);
            articleLoader.registerOnLoadCanceledListener(onLoadCanceledListener);
            return articleLoader;
        }

        /**
         * Handles loadFinished event. On this event, hide the loading view in the
         * {@link RecyclerView} and add the {@link List} returned by the completed
         * {@link ArticleLoader} to the {@link ArticleAdapter}. Then, remove the scroll listener
         * if no data was returned by the {@link ArticleLoader}. Then, increment the
         * nextArticleLoaderId and nextPageIndex global variables for future {@link ArticleLoader}
         * objects. Finally, destroy the completed {@link ArticleLoader}.
         *
         * @param loader    {@link ArticleLoader} object that completed.
         * @param data      {@link List} of {@link Article} objects returned by the completed
         *                  {@link ArticleLoader} object.
         */
        @Override
        public void onLoadFinished(@NonNull Loader<List<Article>> loader, List<Article> data) {

            // Update UI.
            articleAdapter.hideFooterView();
            articleAdapter.addAllArticles(data);
            if (data.isEmpty()) {
                recyclerView.removeOnScrollListener(onScrollListener);
                if (articleAdapter.getItemCount() == 0) {
                    emptyListTextView.setVisibility(View.VISIBLE);
                } else {
                    articleAdapter.showFooterView(ArticleAdapter.VIEW_TYPE_END_OF_LIST);
                }
            }

            // Increment global variables.
            nextArticleLoaderId++;
            nextPageIndex++;

            // Destroy ArticleLoader.
            LoaderManager.getInstance(MainActivity.this).destroyLoader(loader.getId());
        }

        @Override
        public void onLoaderReset(@NonNull Loader<List<Article>> loader) {
        }
    };

    /**
     * {@link Loader.OnLoadCanceledListener} object that defines how an {@link ArticleLoader}
     * handles its loadCanceled event.
     */
    private final Loader.OnLoadCanceledListener<List<Article>> onLoadCanceledListener = new Loader.OnLoadCanceledListener<List<Article>>() {

        /**
         * Handles loadCanceled event. On this event, show an error view in the
         * {@link RecyclerView}, remove the scroll listener from the {@link RecyclerView}, and
         * destroy the canceled {@link ArticleLoader}.
         *
         * @param loader    {@link Loader} object that was canceled.
         */
        @Override
        public void onLoadCanceled(@NonNull Loader<List<Article>> loader) {
            articleAdapter.hideFooterView();
            articleAdapter.showFooterView(ArticleAdapter.VIEW_TYPE_ERROR);
            recyclerView.removeOnScrollListener(onScrollListener);
            LoaderManager.getInstance(MainActivity.this).destroyLoader(loader.getId());
        }
    };

    /**
     * {@link ArticleAdapter} for binding a {@link List} of {@link Article} objects to a
     * {@link RecyclerView}.
     */
    private ArticleAdapter articleAdapter;

    /**
     * {@link LinearLayoutManager} responsible for positioning views in the {@link RecyclerView}.
     */
    private LinearLayoutManager layoutManager;

    /**
     * {@link RecyclerView} for displaying a {@link List} of {@link Article} objects.
     */
    private RecyclerView recyclerView;

    /**
     * {@link TextView} set to visible when the {@link RecyclerView} is empty.
     */
    private TextView emptyListTextView;

    /**
     * int representing the next id that may be assigned to an {@link ArticleLoader} object.
     */
    private int nextArticleLoaderId;

    /**
     * int representing the next page index that needs to be fetched by an {@link ArticleLoader}
     * object.
     */
    private int nextPageIndex;

    /**
     * Callback method invoked exactly once when this activity is created. On this event, setup the
     * {@link RecyclerView} and its helper objects, setup the empty list {@link TextView},
     * initialize the global {@link ArticleLoader} variables, and initialize a new
     * {@link ArticleLoader} object.
     *
     * @param savedInstanceState {@link Bundle} object where instance state from a previous
     *                           configuration change is stored.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Setup article adapter.
        articleAdapter = new ArticleAdapter();

        // Setup linear layout manager.
        layoutManager = new LinearLayoutManager(this);

        // Setup recycler view.
        recyclerView = findViewById(R.id.article_recycler_view);
        recyclerView.setAdapter(articleAdapter);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        recyclerView.addOnItemTouchListener(new RecyclerViewOnItemClickListener(this, onItemClickListener));
        recyclerView.addOnScrollListener(onScrollListener);

        // Setup empty list text view.
        emptyListTextView = findViewById(R.id.empty_list_text_view);

        // Initialize id and page index for ArticleLoader objects.
        nextArticleLoaderId = 0;
        nextPageIndex = 1;

        // Update action bar title.
        updateActionBarTitle();

        // Start new ArticleLoader if not restoring an instance state.
        if (savedInstanceState == null) {
            LoaderManager.getInstance(MainActivity.this).initLoader(nextArticleLoaderId, null, loaderCallbacks);
        }
    }

    /**
     * Callback method invoked when this activity needs a new options menu. On this event, inflate
     * the options menu defined at {@link R.menu#menu_main}.
     *
     * @param menu {@link Menu} object in which you place your items.
     * @return Whether the menu will be displayed.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     * Callback method invoked when an options menu item is clicked. On this event, start an intent
     * to start the {@link SettingsActivity}.
     *
     * @param item {@link MenuItem} that was clicked.
     * @return False to allow normal menu processing to proceed. True to consume it here.
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivityForResult(intent, 0);
        return true;
    }

    /**
     * Callback method invoked when this activity needs to save its instance state before a possible
     * configuration change. On this event, save the state of the global {@link ArticleLoader}
     * variables, the empty list {@link TextView}, and the {@link RecyclerView}.
     *
     * @param outState {@link Bundle} object where the instance state is saved.
     */
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(NEXT_ARTICLE_LOADER_ID_KEY, nextArticleLoaderId);
        outState.putInt(NEXT_PAGE_INDEX_KEY, nextPageIndex);
        outState.putInt(EMPTY_LIST_TEXT_VIEW_VISIBILITY_KEY, emptyListTextView.getVisibility());
        outState.putParcelableArrayList(RECYCLER_VIEW_CONTENT_KEY, (ArrayList<? extends Parcelable>) articleAdapter.getArticles());
        outState.putInt(RECYCLER_VIEW_FOOTER_VIEW_TYPE_KEY, articleAdapter.getItemViewType(articleAdapter.getItemCount() - 1));
        outState.putInt(RECYCLER_VIEW_POSITION_KEY, layoutManager.findFirstVisibleItemPosition());
    }

    /**
     * Callback method invoked after a configuration change. On this event, restore the state of
     * the global {@link ArticleLoader} variables, the empty list {@link TextView}, and the
     * {@link RecyclerView}.
     *
     * @param savedInstanceState {@link Bundle} object where instance state is restored from.
     */
    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        nextArticleLoaderId = savedInstanceState.getInt(NEXT_ARTICLE_LOADER_ID_KEY);
        nextPageIndex = savedInstanceState.getInt(NEXT_PAGE_INDEX_KEY);
        emptyListTextView.setVisibility(savedInstanceState.getInt(EMPTY_LIST_TEXT_VIEW_VISIBILITY_KEY));
        articleAdapter.addAllArticles(savedInstanceState.getParcelableArrayList(RECYCLER_VIEW_CONTENT_KEY));
        layoutManager.scrollToPosition(savedInstanceState.getInt(RECYCLER_VIEW_POSITION_KEY));

        int recyclerViewFooterViewType = savedInstanceState.getInt(RECYCLER_VIEW_FOOTER_VIEW_TYPE_KEY);
        if (recyclerViewFooterViewType == ArticleAdapter.VIEW_TYPE_LOADING) {
            LoaderManager.getInstance(MainActivity.this).restartLoader(nextArticleLoaderId, null, loaderCallbacks);
        } else if (recyclerViewFooterViewType == ArticleAdapter.VIEW_TYPE_ERROR) {
            articleAdapter.showFooterView(ArticleAdapter.VIEW_TYPE_ERROR);
            recyclerView.removeOnScrollListener(onScrollListener);
        } else if (recyclerViewFooterViewType == ArticleAdapter.VIEW_TYPE_END_OF_LIST) {
            articleAdapter.showFooterView(ArticleAdapter.VIEW_TYPE_END_OF_LIST);
            recyclerView.removeOnScrollListener(onScrollListener);
        }
    }

    /**
     * Callback method invoked after a child activity finishes. On this event, reset the UI, reset
     * the nextPageIndex global variable, and initialize a new {@link ArticleLoader} object.
     *
     * @param requestCode The integer request code originally supplied to startActivityForResult(),
     *                    allowing you to identify who this result came from.
     * @param resultCode  The integer result code returned by the child activity through its
     *                    setResult().
     * @param data        An Intent, which can return result data to the caller (various data can be
     *                    attached to Intent "extras").
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        articleAdapter.resetArticles();
        articleAdapter.hideFooterView();
        emptyListTextView.setVisibility(View.INVISIBLE);
        updateActionBarTitle();
        nextPageIndex = 1;
        LoaderManager.getInstance(MainActivity.this).initLoader(nextArticleLoaderId, null, loaderCallbacks);
    }

    /**
     * Updates the action bar title of this activity to reflect what order by and search term
     * preferences the user has selected.
     */
    private void updateActionBarTitle() {

        // Get order by and search term preference values from SharedPreferences.
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        String orderByPreferenceValue = sharedPreferences.getString(getString(R.string.order_by_key), getString(R.string.order_by_default_value));
        String searchTermPreferenceValue = sharedPreferences.getString(getString(R.string.search_term_key), getString(R.string.search_term_default_value));

        // Get appropriate order by label that corresponds to its value.
        String orderByPreferenceLabel = "";
        if (orderByPreferenceValue.equals(getString(R.string.order_by_newest_value))) {
            orderByPreferenceLabel = getString(R.string.order_by_newest_label);
        } else if (orderByPreferenceValue.equals(getString(R.string.order_by_oldest_value))) {
            orderByPreferenceLabel = getString(R.string.order_by_oldest_label);
        } else if (orderByPreferenceValue.equals(getString(R.string.order_by_relevance_value))) {
            orderByPreferenceLabel = getString(R.string.order_by_relevance_label);
        }

        // Update action bar title.
        if (getSupportActionBar() != null) {
            if (searchTermPreferenceValue.equals(getString(R.string.search_term_default_value))) {
                getSupportActionBar().setTitle(getString(R.string.main_activity_no_search_label, orderByPreferenceLabel));
            } else {
                getSupportActionBar().setTitle(getString(R.string.main_activity_search_label, searchTermPreferenceValue, orderByPreferenceLabel));
            }
        }
    }
}