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

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.perficient.library.common.converter.StringListAttributeConverter;

@Entity
@EntityListeners(AuditingEntityListener.class)
public class MailQueue implements Serializable {

    private static final long serialVersionUID = -3264347323661801400L;

    @Id
    @GeneratedValue
    private Integer id;

    private String sendFrom;

    @Convert(converter = StringListAttributeConverter.class)
    private List<String> sendTo;

    @Convert(converter = StringListAttributeConverter.class)
    private List<String> copyTo;

    @Convert(converter = StringListAttributeConverter.class)
    private List<String> blindCopyTo;

    private String subject;

    @Column(columnDefinition = "longtext")
    private String content;

    @Convert(converter = StringListAttributeConverter.class)
    @Column(columnDefinition = "longtext")
    private List<String> attachments;

    private boolean sent;

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

    public List<String> getBlindCopyTo() {
        return blindCopyTo;
    }

    public void setBlindCopyTo(List<String> blindCopyTo) {
        this.blindCopyTo = blindCopyTo;
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

    public List<String> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<String> attachments) {
        this.attachments = attachments;
    }

    public boolean isSent() {
        return sent;
    }

    public void setSent(boolean sent) {
        this.sent = sent;
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
