package com.venomvendor.dailyhunt.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "title",
        "source",
        "category",
        "image",
        "content",
        "url"
})
public class Article implements Parcelable {

    @JsonProperty("title")
    private String title;
    @JsonProperty("source")
    private String source;
    @JsonProperty("category")
    private String category;
    @JsonProperty("image")
    private String image;
    @JsonProperty("content")
    private String content;
    @JsonProperty("url")
    private String url;

    /**
     * No args constructor for use in serialization
     */
    public Article() {
    }

    /**
     * @param content
     * @param category
     * @param title
     * @param source
     * @param image
     * @param url
     */
    public Article(String title, String source, String category, String image, String content, String url) {
        this.title = title;
        this.source = source;
        this.category = category;
        this.image = image;
        this.content = content;
        this.url = url;
    }

    /**
     * @return The title
     */
    @JsonProperty("title")
    public String getTitle() {
        return title;
    }

    /**
     * @param title The title
     */
    @JsonProperty("title")
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return The source
     */
    @JsonProperty("source")
    public String getSource() {
        return source;
    }

    /**
     * @param source The source
     */
    @JsonProperty("source")
    public void setSource(String source) {
        this.source = source;
    }

    /**
     * @return The category
     */
    @JsonProperty("category")
    public String getCategory() {
        return category;
    }

    /**
     * @param category The category
     */
    @JsonProperty("category")
    public void setCategory(String category) {
        this.category = category;
    }

    /**
     * @return The image
     */
    @JsonProperty("image")
    public String getImage() {
        return image;
    }

    /**
     * @param image The image
     */
    @JsonProperty("image")
    public void setImage(String image) {
        this.image = image;
    }

    /**
     * @return The content
     */
    @JsonProperty("content")
    public String getContent() {
        return content;
    }

    /**
     * @param content The content
     */
    @JsonProperty("content")
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * @return The url
     */
    @JsonProperty("url")
    public String getUrl() {
        return url;
    }

    /**
     * @param url The url
     */
    @JsonProperty("url")
    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.title);
        dest.writeString(this.source);
        dest.writeString(this.category);
        dest.writeString(this.image);
        dest.writeString(this.content);
        dest.writeString(this.url);
    }

    protected Article(Parcel in) {
        this.title = in.readString();
        this.source = in.readString();
        this.category = in.readString();
        this.image = in.readString();
        this.content = in.readString();
        this.url = in.readString();
    }

    public static final Parcelable.Creator<Article> CREATOR = new Parcelable.Creator<Article>() {
        public Article createFromParcel(Parcel source) {
            return new Article(source);
        }

        public Article[] newArray(int size) {
            return new Article[size];
        }
    };
}
