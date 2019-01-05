package com.perficient.library.douban.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.perficient.library.common.utils.PriceUtils;
import com.perficient.library.core.model.BookProperty;

public class DoubanBook {

    private String title;

    private String originTitle;

    private List<String> author;

    private String authorIntro;

    private String publisher;

    @JsonProperty("pubdate")
    private String publishDate;

    private List<DoubanTag> tags;

    private DoubanImages images;

    @JsonProperty("isbn13")
    private String isbn;

    private String summary;

    private String price;

    private String pages;

    public BookProperty generateBookProperty() {
        BookProperty property = new BookProperty();
        property.setTitle(title);
        property.setOriginTitle(originTitle);
        property.setAuthor(author);
        property.setAuthorIntro(authorIntro);
        property.setPublisher(publisher);
        property.setPublishDate(publishDate);
        List<String> tagList = new ArrayList<String>();
        if (tags != null) {
            tags.forEach(tag -> {
                tagList.add(tag.getName());
            });
        }
        property.setTags(tagList);
        property.setImage(images == null ? null : images.getLarge());
        property.setIsbn(isbn);
        property.setSummary(summary);
        property.setPrice(PriceUtils.getPrice(price));
        property.setPages(pages);
        return property;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOriginTitle() {
        return originTitle;
    }

    public void setOriginTitle(String originTitle) {
        this.originTitle = originTitle;
    }

    public List<String> getAuthor() {
        return author;
    }

    public void setAuthor(List<String> author) {
        this.author = author;
    }

    public String getAuthorIntro() {
        return authorIntro;
    }

    public void setAuthorIntro(String authorIntro) {
        this.authorIntro = authorIntro;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(String publishDate) {
        this.publishDate = publishDate;
    }

    public List<DoubanTag> getTags() {
        return tags;
    }

    public void setTags(List<DoubanTag> tags) {
        this.tags = tags;
    }

    public DoubanImages getImages() {
        return images;
    }

    public void setImages(DoubanImages images) {
        this.images = images;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getPages() {
        return pages;
    }

    public void setPages(String pages) {
        this.pages = pages;
    }

    @Override
    public String toString() {
        return "DoubanBook [title=" + title + ", originTitle=" + originTitle + ", author=" + author + ", authorIntro="
            + authorIntro + ", publisher=" + publisher + ", publishDate=" + publishDate + ", tags=" + tags + ", images="
            + images + ", isbn=" + isbn + ", summary=" + summary + ", price=" + price + ", pages=" + pages + "]";
    }

}
