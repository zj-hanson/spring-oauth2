package com.lightshell.oauth2.common;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.lang.Nullable;

import javax.persistence.criteria.*;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @param <T>
 * @param <ID>
 * @author KevinDong
 */
public interface SuperService<T, ID> extends Serializable {

    final static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    JpaRepository<T, ID> getJpaRepository();

    JpaSpecificationExecutor<T> getJpaSpecificationExecutor();

    /**
     * 根据条件组成Specification
     *
     * @param filters 过滤条件
     * @param equalString TRUE 完全匹配 FALSE 近似匹配
     * @return Specification
     */
    default Specification<T> getSpecification(Map<String, Object> filters, boolean equalString) {
        return new Specification<T>() {
            @Override
            public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicateList = new ArrayList<Predicate>();
                if (filters != null && !filters.isEmpty()) {
                    for (Map.Entry<String, Object> entry : filters.entrySet()) {
                        Predicate p = null;
                        if (entry.getKey().contains("@")) {
                            Path<Date> path = root.get(entry.getKey().replace("@Start", "").replace("@End", ""));
                            try {
                                if (entry.getKey().contains("@Start")) {
                                    p = criteriaBuilder.greaterThanOrEqualTo(path,
                                        DATE_FORMAT.parse(entry.getValue().toString()));
                                } else if (entry.getKey().contains("@End")) {
                                    p = criteriaBuilder.lessThanOrEqualTo(path,
                                        DATE_FORMAT.parse(entry.getValue().toString()));
                                }
                            } catch (ParseException e) {
                                throw new RuntimeException(e);
                            }
                        } else {
                            Path<Object> path = root.get(entry.getKey());
                            switch (path.getJavaType().getSimpleName()) {
                                case "String":
                                    if (equalString) {
                                        p = criteriaBuilder.equal(path, entry.getValue());
                                    } else {
                                        p = criteriaBuilder.like(path.as(String.class),
                                            "%" + entry.getValue().toString() + "%");
                                    }
                                    break;
                                case "Date":
                                    try {
                                        p = criteriaBuilder.equal(path, DATE_FORMAT.parse(entry.getValue().toString()));
                                    } catch (ParseException e) {
                                        throw new RuntimeException(e);
                                    }
                                    break;
                                default:
                                    p = criteriaBuilder.equal(path, entry.getValue());
                            }
                        }
                        predicateList.add(p);
                    }
                }
                // 新建一个数组，这个对象代表一个查询条件
                Predicate[] p = new Predicate[predicateList.size()];
                // 将list对象转化为p类型数组，加载criteriaBuilder
                return criteriaBuilder.and(predicateList.toArray(p));
            }
        };
    }

    default String getFormId(Date day, String code) {
        return getFormId(day, code, "yyMM", 4);
    }

    default String getFormId(Date day, String code, String format) {
        return getFormId(day, code, format, 4);
    }

    default String getFormId(Date day, String code, String format, int len) {
        return UUID.randomUUID().toString().replace("-", "");
    }

    default List<T> findAll(@Nullable Map<String, String> sorts) {
        if (sorts != null && !sorts.isEmpty()) {
            Sort sort = null;
            for (Map.Entry<String, String> entry : sorts.entrySet()) {
                if (sort == null) {
                    sort = Sort.by(Sort.Direction.fromString(entry.getValue()), entry.getKey());
                } else {
                    sort = sort.and(Sort.by(Sort.Direction.fromString(entry.getValue()), entry.getKey()));
                }
            }
            return getJpaRepository().findAll(sort);
        }
        return getJpaRepository().findAll();
    }

    default List<T> findAll(@Nullable Map<String, Object> filters, boolean equalString) {
        return getJpaSpecificationExecutor().findAll(getSpecification(filters, equalString));
    }

    default List<T> findAll(@Nullable Map<String, Object> filters, Integer offset, Integer pageSize,
        @Nullable Map<String, String> sorts, boolean equalString) {
        Pageable pageable;
        if (sorts != null && !sorts.isEmpty()) {
            Sort sort = null;
            for (Map.Entry<String, String> entry : sorts.entrySet()) {
                if (sort == null) {
                    sort = Sort.by(Sort.Direction.fromString(entry.getValue()), entry.getKey());
                } else {
                    sort = sort.and(Sort.by(Sort.Direction.fromString(entry.getValue()), entry.getKey()));
                }
            }
            pageable = PageRequest.of(offset, pageSize, sort);
        } else {
            pageable = PageRequest.of(offset, pageSize);
        }
        if (filters != null && !filters.isEmpty()) {
            return getJpaSpecificationExecutor().findAll(getSpecification(filters, equalString), pageable).toList();
        } else {
            return getJpaRepository().findAll(pageable).toList();
        }
    }

    default T findById(ID id) {
        return getJpaRepository().findById(id).get();
    }

    default long getCount() {
        return getJpaRepository().count();
    }

    default long getCount(Map<String, Object> filters, boolean equalString) {
        return getJpaSpecificationExecutor().count(getSpecification(filters, equalString));
    }

    default void deleteById(ID id) {
        getJpaRepository().deleteById(id);
    }

    default void deleteByUID(String uid) {
        T entity = findByUID(uid);
        if (entity != null) {
            getJpaRepository().delete(entity);
        } else {
            throw new NullPointerException(uid);
        }
    }

    default void deleteAll(List<T> data) {
        getJpaRepository().deleteAll(data);
    }

    default boolean existsById(ID id) {
        return getJpaRepository().existsById(id);
    }

    default boolean existsByUID(String uid) {
        return null != findByUID(uid);
    }

    default T save(T t) {
        return getJpaRepository().save(t);
    }

    default List<T> saveAll(List<T> data) {
        return getJpaRepository().saveAll(data);
    }

    T findByUID(String uid);

}
