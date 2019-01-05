package com.perficient.library.core.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.perficient.library.common.converter.RoleAttributeConverter;
import com.perficient.library.core.enums.Role;

@Entity
@EntityListeners(AuditingEntityListener.class)
public class Employee implements Serializable {

    private static final long serialVersionUID = -9188046656979613013L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String screenName;

    // employee id in TPT
    private String emid;

    @Convert(converter = RoleAttributeConverter.class)
    private Role role;

    private Boolean isFirstLogin;

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

    public String getScreenName() {
        return screenName;
    }

    public void setScreenName(String screenName) {
        this.screenName = screenName;
    }

    public String getEmid() {
        return emid;
    }

    public void setEmid(String emid) {
        this.emid = emid;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Boolean getIsFirstLogin() {
        return isFirstLogin;
    }

    public void setIsFirstLogin(Boolean isFirstLogin) {
        this.isFirstLogin = isFirstLogin;
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
