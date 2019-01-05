package com.perficient.library.core.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotBlank;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@EntityListeners(AuditingEntityListener.class)
public class Comment implements Serializable {

    private static final long serialVersionUID = 5550084948077788194L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "property_id")
    @NotNull(message = "book property cannot be null")
    private BookProperty property;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "employee_id")
    @NotNull(message = "employee cannot be null")
    private Employee employee;

    @NotBlank(message = "comment content cannot be blank")
    @Size(max = 300, message = "comment content cannot more than 300 characters")
    private String content;

    @NotNull(message = "rating cannot be null")
    @Max(value = 5, message = "rating cannot more than 5")
    @Min(value = 1, message = "rating cannot less than 1")
    private Double rating;

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

    public BookProperty getProperty() {
        return property;
    }

    public void setProperty(BookProperty property) {
        this.property = property;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
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
