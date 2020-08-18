package com.example.api;

import com.example.bean.ModelInfo;
import com.example.domain.Pageable;
import com.example.utils.SqlUtils;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.springframework.jdbc.core.JdbcTemplate;

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

	public BaseCrudImpl(DataSource dataSource) {
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
	public <T> int update(T t) throws IllegalAccessException {
		ModelInfo<T> info = get(t);
		String sql = SqlUtils.getBaseUpdateSql(info);
		return this.jdbcTemplate.update(sql);
	}

	@Override
	public <T> int insert(T t) throws IllegalAccessException {
		ModelInfo<T> info = get(t);
		String sql = SqlUtils.getBaseInsertSql(info);
		return this.jdbcTemplate.update(sql);
	}

	@Override
	public <T> int deleteById(Object id) throws IllegalAccessException {
		ModelInfo<T> info = get(id);
		String sql = SqlUtils.getBaseDeleteSql(info);
		return this.jdbcTemplate.update(sql);
	}

	@Override
	protected BaseCrudImpl clone() throws CloneNotSupportedException {
		BaseCrudImpl crud = null;
		try {
			crud = (BaseCrudImpl) super.clone();
		} finally {
			if (Objects.isNull(crud)) {
				crud = new BaseCrudImpl(this.jdbcTemplate.getDataSource());
			}
		}
		return crud;
	}
}
