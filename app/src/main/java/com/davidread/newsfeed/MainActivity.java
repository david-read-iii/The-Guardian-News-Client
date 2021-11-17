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
        public void onItemClick(View view, int position) {

            Article article = (Article) articleAdapter.getItem(position);
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
            return new ArticleLoader(MainActivity.this, "newest", 1, null);
        }

        /**
         * Handles loadFinished event. On this event, add the {@link List} returned by the completed
         * {@link ArticleLoader} to the {@link ArticleAdapter}.
         *
         * @param loader    {@link ArticleLoader} object that completed.
         * @param data      {@link List} of {@link Article} objects returned by the completed
         *                  {@link ArticleLoader} object.
         */
        @Override
        public void onLoadFinished(@NonNull Loader<List<Article>> loader, List<Article> data) {
            articleAdapter.addAll(data);
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

        // Start new ArticleLoader.
        LoaderManager.getInstance(MainActivity.this).initLoader(0, null, loaderCallbacks);
    }
}