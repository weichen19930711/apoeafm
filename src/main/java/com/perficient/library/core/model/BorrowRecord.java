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
import javax.validation.constraints.NotNull;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@EntityListeners(AuditingEntityListener.class)
public class BorrowRecord implements Serializable {

    private static final long serialVersionUID = 5424131908566673390L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "employee_id")
    @NotNull(message = "employee cannot be null")
    private Employee employee;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "book_id")
    @NotNull(message = "book cannot be null")
    private Book book;

    // borrow date
    private Date checkoutDate;

    // return date
    private Date checkinDate;

    //overdue date
    private Date dueDate;

    private boolean createdOverdueRecord;

    @CreatedDate
    private Date createDate;

    @LastModifiedDate
    private Date lastModifiedDate;

    private Integer renewTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public Date getCheckoutDate() {
        return checkoutDate;
    }

    public void setCheckoutDate(Date checkoutDate) {
        this.checkoutDate = checkoutDate;
    }

    public Date getCheckinDate() {
        return checkinDate;
    }

    public void setCheckinDate(Date checkinDate) {
        this.checkinDate = checkinDate;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public boolean isCreatedOverdueRecord() {
        return createdOverdueRecord;
    }

    public void setCreatedOverdueRecord(boolean createdOverdueRecord) {
        this.createdOverdueRecord = createdOverdueRecord;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Integer getRenewTime() {
        return renewTime;
    }

    public void setRenewTime(Integer renewTime) {
        this.renewTime = renewTime;
    }

    public Date getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(Date lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

}
