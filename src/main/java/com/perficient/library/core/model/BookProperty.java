package com.perficient.library.core.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotBlank;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.perficient.library.common.converter.IntegerListAttributeConverter;
import com.perficient.library.common.converter.StringListAttributeConverter;

/**
 * all the book's properties are in this class, one BookProperty can map many Books
 * 
 * @author bin.zhou
 *
 */
@Entity
@EntityListeners(AuditingEntityListener.class)
public class BookProperty implements Serializable {

    private static final long serialVersionUID = -7433995058438492748L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Size(max = 20, message = "isbn cannot more than 20 words")
    @Column(unique = true, updatable = false)
    @NotBlank(message = "isbn cannot be null or blank")
    private String isbn;

    @NotBlank(message = "title cannot be null or blank")
    @Size(max = 500, message = "title cannot more than 500 words")
    private String title;

    @Size(max = 500, message = "origin title cannot more than 500 words")
    private String originTitle;

    @Size(max = 50, message = "author cannot more than 50")
    @Convert(converter = StringListAttributeConverter.class)
    private List<String> author;

    @Column(columnDefinition = "longtext")
    private String authorIntro;

    @Size(max = 50, message = "category cannot more than 50")
    @Convert(converter = IntegerListAttributeConverter.class)
    private List<Integer> category;

    @Size(max = 1000, message = "publisher cannot more than 100 words")
    private String publisher;

    private String publishDate;

    @Size(max = 255, message = "image url cannot more than 255 words")
    private String image;

    @Convert(converter = StringListAttributeConverter.class)
    @Size(max = 50, message = "tag cannot more than 50")
    private List<String> tags;

    @Column(columnDefinition = "longtext")
    private String summary;

    @Size(max = 20, message = "pages cannot more than 20 words")
    private String pages;

    @Size(max = 20, message = "price cannot more than 20 words")
    private String price;

    @CreatedDate
    private Date createDate;

    @LastModifiedDate
    private Date lastModifiedDate;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
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

    public List<Integer> getCategory() {
        return category;
    }

    public void setCategory(List<Integer> category) {
        this.category = category;
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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getPages() {
        return pages;
    }

    public void setPages(String pages) {
        this.pages = pages;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(Date lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

}
