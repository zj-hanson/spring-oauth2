package com.lightshell.oauth2.common;

import java.util.List;

public interface EntityService<T> {

    T save(T t);

    List<T> saveAll(List<T> data);

}
