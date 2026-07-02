package com.utp.myapp.sales.domain.model.repository;

import java.util.List;

public interface ICRUD <T>{
    T insert(T t);
    T update(T t);
    void delete(Integer id);
    T listById(Integer id);
    List<T> listAll();
}
