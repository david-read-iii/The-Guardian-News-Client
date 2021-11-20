package com.davidread.newsfeed;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
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
    private static final String RECYCLER_VIEW_CONTENT_KEY = "recycler_view_content_key";
    private static final String RECYCLER_VIEW_POSITION_KEY = "recycler_view_position";
    private static final String NEXT_ARTICLE_LOADER_ID_KEY = "next_article_loader_id";
    private static final String NEXT_PAGE_INDEX_KEY = "next_page_index_key";

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
         * Handles createLoader event. On this event, show a loading view in the
         * {@link RecyclerView} and return a new {@link ArticleLoader} object with the appropriate
         * parameters and listeners set onto it.
         *
         * @param id    int id of the {@link ArticleLoader} to be created.
         * @param args  {@link Bundle} object containing optional arguments for the
         *              {@link ArticleLoader}.
         * @return A new {@link ArticleLoader} object.
         */
        @NonNull
        @Override
        public Loader<List<Article>> onCreateLoader(int id, @Nullable Bundle args) {
            articleAdapter.showFooterView(ArticleAdapter.VIEW_TYPE_LOADING);
            ArticleLoader articleLoader = new ArticleLoader(MainActivity.this, "newest", nextPageIndex, null);
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
            nextArticleLoaderId++;
            nextPageIndex++;
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

        // Start new ArticleLoader if not restoring an instance state.
        if (savedInstanceState == null) {
            LoaderManager.getInstance(MainActivity.this).initLoader(nextArticleLoaderId, null, loaderCallbacks);
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(RECYCLER_VIEW_CONTENT_KEY, (ArrayList<? extends Parcelable>) articleAdapter.getArticles());
        outState.putInt(RECYCLER_VIEW_POSITION_KEY, layoutManager.findFirstVisibleItemPosition());
        outState.putInt(NEXT_ARTICLE_LOADER_ID_KEY, nextArticleLoaderId);
        outState.putInt(NEXT_PAGE_INDEX_KEY, nextPageIndex);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        articleAdapter.addAllArticles(savedInstanceState.getParcelableArrayList(RECYCLER_VIEW_CONTENT_KEY));
        layoutManager.scrollToPosition(savedInstanceState.getInt(RECYCLER_VIEW_POSITION_KEY));
        nextArticleLoaderId = savedInstanceState.getInt(NEXT_ARTICLE_LOADER_ID_KEY);
        nextPageIndex = savedInstanceState.getInt(NEXT_PAGE_INDEX_KEY);
    }
}