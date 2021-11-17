package com.davidread.newsfeed;

import android.os.Bundle;

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

        // Start new ArticleLoader.
        LoaderManager.getInstance(MainActivity.this).initLoader(0, null, loaderCallbacks);
    }
}