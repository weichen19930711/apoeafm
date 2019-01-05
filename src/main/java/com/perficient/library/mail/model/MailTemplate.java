package com.perficient.library.mail.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.hibernate.validator.constraints.NotBlank;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.perficient.library.common.converter.StringListAttributeConverter;

@Entity
@EntityListeners(AuditingEntityListener.class)
public class MailTemplate implements Serializable {

    private static final long serialVersionUID = -8047694129148493269L;

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    @NotBlank(message = "send from's value cannot be null or blank")
    private String sendFrom;

    @Convert(converter = StringListAttributeConverter.class)
    @Column(columnDefinition = "longtext")
    private List<String> sendTo;

    @Convert(converter = StringListAttributeConverter.class)
    @Column(columnDefinition = "longtext")
    private List<String> copyTo;

    @Column(columnDefinition = "longtext")
    @NotBlank(message = "subject cannot be null or blank")
    private String subject;

    @Column(columnDefinition = "longtext")
    @NotBlank(message = "content cannot be null or blank")
    private String content;

    @Convert(converter = StringListAttributeConverter.class)
    private List<String> variables;

    @CreatedDate
    private Date createDate;

    @LastModifiedDate
    private Date lastModifiedDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSendFrom() {
        return sendFrom;
    }

    public void setSendFrom(String sendFrom) {
        this.sendFrom = sendFrom;
    }

    public List<String> getSendTo() {
        return sendTo;
    }

    public void setSendTo(List<String> sendTo) {
        this.sendTo = sendTo;
    }

    public List<String> getCopyTo() {
        return copyTo;
    }

    public void setCopyTo(List<String> copyTo) {
        this.copyTo = copyTo;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<String> getVariables() {
        return variables;
    }

    public void setVariables(List<String> variables) {
        this.variables = variables;
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
