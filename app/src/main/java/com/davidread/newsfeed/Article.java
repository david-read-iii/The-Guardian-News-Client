package com.davidread.newsfeed;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * {@link Article} is a model class for an article listing. It implements the {@link Parcelable}
 * interface so that {@link java.util.ArrayList} of {@link Article} objects may be passed inside
 * {@link android.os.Bundle} objects.
 */
public class Article implements Parcelable {

    /**
     * {@link android.os.Parcelable.Creator} object that generates instances of this class from
     * a {@link Parcelable} object.
     */
    public static final Creator<Article> CREATOR = new Creator<Article>() {
        @Override
        public Article createFromParcel(Parcel in) {
            return new Article(in);
        }

        @Override
        public Article[] newArray(int size) {
            return new Article[size];
        }
    };

    /**
     * {@link String} representing the title of the article.
     */
    private final String title;

    /**
     * {@link String} array representing the author(s) of the article.
     */
    private final String[] authors;

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
     * @param authors        {@link String} array representing the author(s) of the article.
     * @param sectionName   {@link String} representing the name of the section the article is from.
     * @param datePublished {@link String} representing the date the article was published.
     * @param url           {@link String} representing a URL that points to the article online.
     */
    public Article(String title, String[] authors, String sectionName, String datePublished, String url) {
        this.title = title;
        this.authors = authors;
        this.sectionName = sectionName;
        this.datePublished = datePublished;
        this.url = url;
    }

    /**
     * Constructs a new {@link Article} object.
     *
     * @param in {@link Parcelable} object that contains the member variables of the {@link Article}
     *           object to be constructed.
     */
    protected Article(Parcel in) {
        title = in.readString();
        authors = in.createStringArray();
        sectionName = in.readString();
        datePublished = in.readString();
        url = in.readString();
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
     * Returns a {@link String} array representing the author(s) of the article.
     *
     * @return {@link String} array representing the author(s) of the article.
     */
    public String[] getAuthors() {
        return authors;
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

    /**
     * Returns an int that describes the type of objects contained in this {@link Parcelable}
     * instance.
     *
     * @return An int that describes the type of objects contained in this {@link Parcelable}
     * instance.
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Converts the member variables of this {@link Article} object into a {@link Parcel} object.
     *
     * @param dest  {@link Parcel} object where the member variables will be stored.
     * @param flags Additional flags.
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeStringArray(authors);
        dest.writeString(sectionName);
        dest.writeString(datePublished);
        dest.writeString(url);
    }
}
