package com.perficient.library.core.service;

import java.io.Serializable;
import java.util.List;

public interface BaseService<T, ID extends Serializable> {

    static final String MESSAGE_ID_NOT_EXIST = "the %s of this id [%s] is not exist.";

    static final String MESSAGE_ASSOCIATED_LIST_IS_NOT_EMPTY = "the associated %s list of this %s is not empty.";

    T save(T entity);

    List<T> findAll();

    T findOne(ID id);

    void delete(ID id);

    boolean exists(ID id);

}
