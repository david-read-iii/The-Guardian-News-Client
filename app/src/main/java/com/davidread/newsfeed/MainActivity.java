package com.davidread.newsfeed;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.URLUtil;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    /**
     * {@link String} log tag name for {@link MainActivity}.
     */
    public static final String LOG_TAG_NAME = MainActivity.class.getSimpleName();

    /**
     * {@link com.davidread.newsfeed.RecyclerViewOnItemClickListener.OnItemClickListener} defines
     * how the {@link RecyclerView} handles its itemClick event.
     */
    private final RecyclerViewOnItemClickListener.OnItemClickListener onItemClickListener = new RecyclerViewOnItemClickListener.OnItemClickListener() {

        /**
         * Handles itemClick event. On this event, start an intent to open the browser. The URL of
         * the site will be determined by the {@link Article} object associated with the clicked
         * item.
         *
         * @param view     {@link View} within the {@link RecyclerView} that was clicked.
         * @param position int representing the position of the view within the adapter.
         */
        @Override
        public void onItemClick(View view, int position, int viewType) {

            // Do nothing if the view type is not an article view.
            if (viewType != ArticleAdapter.VIEW_TYPE_ARTICLE) {
                return;
            }

            Article article = (Article) articleAdapter.getArticle(position);
            String url = article.getUrl();

            // Do nothing if the provided URL is invalid.
            if (url == null || !URLUtil.isValidUrl(url)) {
                return;
            }

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            startActivity(intent);
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
            LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
            int totalItemCount = linearLayoutManager.getItemCount();
            int lastVisibleItemPosition = linearLayoutManager.findLastVisibleItemPosition();
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
         * Handles createLoader event. On this event, return a new {@link ArticleLoader} object with
         * the appropriate parameters.
         *
         * @param id    int id of the {@link ArticleLoader} to be created.
         * @param args  {@link Bundle} object containing optional arguments for the
         *              {@link ArticleLoader}.
         * @return A new {@link ArticleLoader} object.
         */
        @NonNull
        @Override
        public Loader<List<Article>> onCreateLoader(int id, @Nullable Bundle args) {
            articleAdapter.showLoadingView();
            return new ArticleLoader(MainActivity.this, "newest", nextPageIndex, null);
        }

        /**
         * Handles loadFinished event. On this event, add the {@link List} returned by the completed
         * {@link ArticleLoader} to the {@link ArticleAdapter}. Then, increment the
         * nextArticleLoaderId and nextPageIndex global variables for future {@link ArticleLoader}
         * objects. Finally, destroy the completed {@link ArticleLoader}.
         *
         * @param loader    {@link ArticleLoader} object that completed.
         * @param data      {@link List} of {@link Article} objects returned by the completed
         *                  {@link ArticleLoader} object.
         */
        @Override
        public void onLoadFinished(@NonNull Loader<List<Article>> loader, List<Article> data) {
            articleAdapter.hideLoadingView();
            articleAdapter.addAllArticles(data);
            nextArticleLoaderId++;
            nextPageIndex++;
            LoaderManager.getInstance(MainActivity.this).destroyLoader(loader.getId());
        }

        @Override
        public void onLoaderReset(@NonNull Loader<List<Article>> loader) {
        }
    };

    /**
     * {@link ArticleAdapter} for binding a {@link List} of {@link Article} objects to a
     * {@link RecyclerView}.
     */
    private ArticleAdapter articleAdapter;

    /**
     * {@link RecyclerView} for displaying a {@link List} of {@link Article} objects.
     */
    private RecyclerView recyclerView;

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

        // Setup recycler view.
        recyclerView = findViewById(R.id.article_recycler_view);
        recyclerView.setAdapter(articleAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        recyclerView.addOnItemTouchListener(new RecyclerViewOnItemClickListener(this, onItemClickListener));
        recyclerView.addOnScrollListener(onScrollListener);

        // Initialize id and page index for ArticleLoader objects.
        nextArticleLoaderId = 0;
        nextPageIndex = 1;

        // Start new ArticleLoader.
        LoaderManager.getInstance(MainActivity.this).initLoader(nextArticleLoaderId, null, loaderCallbacks);
    }
}