package com.lightshell.oauth2.service;

import java.util.List;

public interface SuperService<T> {

    T save(T t);

    List<T> saveAll(List<T> data);

}
