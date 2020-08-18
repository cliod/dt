package com.example.api;

import com.example.bean.ModelInfo;
import com.example.domain.Pageable;
import com.example.utils.SqlUtils;
import lombok.Getter;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeanUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * crud默认实现, 用jdbctemplate
 *
 * @author cliod
 * @since 7/27/20 10:25 AM
 */
@Getter
public class BaseCrudImpl implements BaseCrud, Cloneable {

	private static final Map<Object, ModelInfo<?>> modelCache = new HashMap<>();
	private final JdbcTemplate jdbcTemplate;

	public BaseCrudImpl(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	@SneakyThrows
	public BaseCrudImpl() {
		//2.获得数据库的连接
		String url = "jdbc:mysql://127.0.0.1:3306/gjjy?characterEncoding=UTF-8&allowPublicKeyRetrieval=true&serverTimezone=Asia/Shanghai";
		String username = "root";
		String password = "wobangkj2019";
		DataSource dataSource = new SingleConnectionDataSource(url, username, password, true);
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	@SuppressWarnings("unchecked")
	public static <T> @NotNull ModelInfo<T> get(Object model) {
		ModelInfo<T> info = (ModelInfo<T>) modelCache.get(model);
		if (Objects.nonNull(info)) {
			info.setModel(model);
			return info;
		}
		info = (ModelInfo<T>) ModelInfo.of(model);
		modelCache.put(model, info);
		modelCache.put(model.getClass(), info);
		return info;
	}

	@Override
	public <T> List<T> selectAll(T t, Pageable pageable) throws IllegalAccessException {
		ModelInfo<T> info = get(t);
		String baseSql = SqlUtils.getBaseSelectSql(info);
		String sql = baseSql + String.format(" order by %s limit %s,%s", pageable.getOrder(), pageable.getOffset(), pageable.getLimit());
		return this.jdbcTemplate.query(sql, info.beanRowMapper());
	}

	@Override
	public <T> T selectOne(T t) throws IllegalAccessException {
		ModelInfo<T> info = get(t);
		String baseSql = SqlUtils.getBaseSelectSql(info);
		String sql = baseSql + " limit 1";
		return this.jdbcTemplate.queryForObject(sql, info.beanRowMapper());
	}

	@Override
	public <T> int update(T t) {
		String sql = "";
		return this.jdbcTemplate.update(sql);
	}

	@Override
	public <T> int insert(T t) {
		return 0;
	}

	@Override
	public <T> int deleteById(Object id) {
		return 0;
	}

	@Override
	protected BaseCrudImpl clone() {
		BaseCrudImpl crud = new BaseCrudImpl();
		BeanUtils.copyProperties(this, crud);
		return crud;
	}
}
