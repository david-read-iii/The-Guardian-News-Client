package com.davidread.newsfeed;

/**
 * {@link Article} is a model class for an article listing.
 */
public class Article {

    /**
     * {@link String} representing the title of the article.
     */
    private final String title;

    /**
     * {@link String} representing the name of the section the article is from.
     */
    private final String sectionName;

    /**
     * {@link String} representing the date the article was published.
     */
    private final String datePublished;

    /**
     * {@link String} representing a URL that points to the article online.
     */
    private final String url;

    /**
     * Constructs a new {@link Article} object.
     *
     * @param title         {@link String} representing the title of the article.
     * @param sectionName   {@link String} representing the name of the section the article is from.
     * @param datePublished {@link String} representing the date the article was published.
     * @param url           {@link String} representing a URL that points to the article online.
     */
    public Article(String title, String sectionName, String datePublished, String url) {
        this.title = title;
        this.sectionName = sectionName;
        this.datePublished = datePublished;
        this.url = url;
    }

    /**
     * Returns a {@link String} representing the title of the article.
     *
     * @return {@link String} representing the title of the article.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Returns a {@link String} representing the name of the section the article is from.
     *
     * @return {@link String} representing the name of the section the article is from.
     */
    public String getSectionName() {
        return sectionName;
    }

    /**
     * Returns a {@link String} representing the date the article was published.
     *
     * @return {@link String} representing the date the article was published.
     */
    public String getDatePublished() {
        return datePublished;
    }

    /**
     * Returns a {@link String} representing a URL that points to the article online.
     *
     * @return {@link String} representing a URL that points to the article online.
     */
    public String getUrl() {
        return url;
    }
}
