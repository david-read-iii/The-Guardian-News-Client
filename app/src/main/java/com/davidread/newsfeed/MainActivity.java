package com.davidread.newsfeed;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Placeholder data source.
        ArrayList<Article> articles = new ArrayList<>();
        articles.add(new Article("Biden-Xi virtual summit: US president warns nations must not ‘veer into open conflict’ | Julian Borger", "US", "Mon 15 Nov 2021 20.37 EST", ""));
        articles.add(new Article("Kyle Rittenhouse trial: jury prepares to deliberate after closing arguments | Maya Yang", "US", "Mon 15 Nov 2021 20.27 EST", ""));
        articles.add(new Article("Amazon to pay $500,000 fine for failing to notify workers of Covid cases", "Tech", "Mon 15 Nov 2021 19.48 EST", ""));
        articles.add(new Article("Fast spreading bird flu puts Europe and Asia on alert", "Europe", "Mon 15 Nov 2021 14.43 EST", ""));

        // Setup adapter.
        ArticleAdapter adapter = new ArticleAdapter(articles);

        // Setup recycler view.
        RecyclerView recyclerView = findViewById(R.id.article_recycler_view);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
    }
}