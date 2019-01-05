package com.perficient.library.core.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.perficient.library.common.converter.BookStatusAttributeConverter;
import com.perficient.library.common.converter.PurchaserAttributeConverter;
import com.perficient.library.core.enums.BookStatus;
import com.perficient.library.core.enums.Purchaser;

@Entity
@EntityListeners(AuditingEntityListener.class)
public class Book implements Serializable {

    private static final long serialVersionUID = 4227321046160722545L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // auto generated in BookService's save method
    @Column(unique = true, updatable = false)
    private String tagNumber;

    @Valid
    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "property_id")
    @NotNull(message = "book property cannot be null")
    private BookProperty property;

    @Convert(converter = BookStatusAttributeConverter.class)
    private BookStatus status;

    private Date purchaseDate;

    @CreatedDate
    private Date createDate;

    @LastModifiedDate
    private Date lastModifiedDate;

    @Size(max = 500, message = "description cannot more than 500 words")
    private String description;

    @NotNull(message = "purchaser cannot be null")
    @Convert(converter = PurchaserAttributeConverter.class)
    private Purchaser purchaser;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTagNumber() {
        return tagNumber;
    }

    public void setTagNumber(String tagNumber) {
        this.tagNumber = tagNumber;
    }

    public BookProperty getProperty() {
        return property;
    }

    public void setProperty(BookProperty property) {
        this.property = property;
    }

    public BookStatus getStatus() {
        return status;
    }

    public void setStatus(BookStatus status) {
        this.status = status;
    }

    public Date getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(Date purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Purchaser getPurchaser() {
        return purchaser;
    }

    public void setPurchaser(Purchaser purchaser) {
        this.purchaser = purchaser;
    }

    public Date getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(Date lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

}
