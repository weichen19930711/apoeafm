package com.perficient.library.core.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@EntityListeners(AuditingEntityListener.class)
public class Configuration implements Serializable {

    private static final long serialVersionUID = 2497370670316981037L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * The amount of most popular books showed on home page
     */
    @NotNull(message = "most popular amount cannot be null")
    @Min(value = 0, message = "most popular amount cannot be less than 0")
    @Max(value = 100, message = "most popular amount cannot be greater than 100")
    private Integer mostPopularAmount;

    /**
     * The amount of recommended books showed on home page
     */
    @NotNull(message = "recommended amount cannot be null")
    @Min(value = 0, message = "recommended amount cannot be less than 0")
    @Max(value = 100, message = "recommended amount cannot be greater than 100")
    private Integer recommendedAmount;

    /**
     * The amount of books that can be borrowed at most for one employee
     */
    @NotNull(message = "max borrowing amount cannot be null")
    @Min(value = 0, message = "max borrowing amount cannot be less than 0")
    @Max(value = 50, message = "max borrowing amount cannot be greater than 50")
    private Integer maxBorrowingAmount;

    /**
     * The number of days a book can be borrowed
     */
    @NotNull(message = "available borrowing days cannot be null")
    @Min(value = 30, message = "available borrowing days cannot be less than 30")
    @Max(value = 120, message = "available borrowing days cannot be greater than 120")
    private Integer availableBorrowingDays;

    /**
     * The number of days before the due date to send overdue reminder mail
     */
    @NotNull(message = "reminder day before cannot be null")
    @Min(value = 0, message = "reminder day before cannot be less than 0")
    @Max(value = 30, message = "reminder day before cannot be greater than 30")
    private Integer reminderDaysBefore;

    /**
     * The number of days before the due date that you can renew a book
     */
    @NotNull(message = "renew days before cannot be null")
    @Min(value = 0, message = "renew days before cannot be less than 0")
    @Max(value = 30, message = "renew days before cannot be greater than 30")
    private Integer renewDaysBefore;

    /**
     * The number of times that you can renew a book
     */
    @NotNull(message = "max renew times cannot be null")
    @Min(value = 0, message = "max renew times cannot be less than 0")
    @Max(value = 10, message = "max renew times cannot be greater than 10")
    private Integer maxRenewTimes;

    /**
     * The number of days that due date added when you renew a book
     */
    @NotNull(message = "renew days cannot be null")
    @Min(value = 0, message = "renew added days cannot be less than 0")
    @Max(value = 120, message = "renew added days cannot be greater than 120")
    private Integer renewAddedDays;

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

    public Integer getMostPopularAmount() {
        return mostPopularAmount;
    }

    public void setMostPopularAmount(Integer mostPopularAmount) {
        this.mostPopularAmount = mostPopularAmount;
    }

    public Integer getRecommendedAmount() {
        return recommendedAmount;
    }

    public void setRecommendedAmount(Integer recommendedAmount) {
        this.recommendedAmount = recommendedAmount;
    }

    public Integer getMaxBorrowingAmount() {
        return maxBorrowingAmount;
    }

    public void setMaxBorrowingAmount(Integer maxBorrowingAmount) {
        this.maxBorrowingAmount = maxBorrowingAmount;
    }

    public Integer getAvailableBorrowingDays() {
        return availableBorrowingDays;
    }

    public void setAvailableBorrowingDays(Integer availableBorrowingDays) {
        this.availableBorrowingDays = availableBorrowingDays;
    }

    public Integer getReminderDaysBefore() {
        return reminderDaysBefore;
    }

    public void setReminderDaysBefore(Integer reminderDaysBefore) {
        this.reminderDaysBefore = reminderDaysBefore;
    }

    public Integer getRenewDaysBefore() {
        return renewDaysBefore;
    }

    public void setRenewDaysBefore(Integer renewDaysBefore) {
        this.renewDaysBefore = renewDaysBefore;
    }

    public Integer getMaxRenewTimes() {
        return maxRenewTimes;
    }

    public void setMaxRenewTimes(Integer maxRenewTimes) {
        this.maxRenewTimes = maxRenewTimes;
    }

    public Integer getRenewAddedDays() {
        return renewAddedDays;
    }

    public void setRenewAddedDays(Integer renewAddedDays) {
        this.renewAddedDays = renewAddedDays;
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
