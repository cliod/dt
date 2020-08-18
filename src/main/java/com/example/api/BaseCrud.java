package com.example.api;

import com.example.domain.Pageable;

import java.util.List;

/**
 * crud封装接口
 *
 * @author cliod
 * @since 7/25/20 5:26 PM
 */
public interface BaseCrud {

	static BaseCrud getInstance() {
		return null;
	}

	<T> List<T> selectAll(T t, Pageable pageable) throws IllegalAccessException;

	<T> T selectOne(T t) throws IllegalAccessException;

	<T> int update(T t) throws IllegalAccessException;

	<T> int insert(T t);

	<T> int deleteById(Object id);
}
