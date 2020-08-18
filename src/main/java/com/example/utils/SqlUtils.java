package com.example.utils;

import com.example.bean.ColumnInfo;
import com.example.bean.ModelInfo;
import com.google.common.base.CaseFormat;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.lang.reflect.Field;
import java.util.*;

/**
 * sql语句构建工具
 *
 * @author cliod
 * @since 8/17/20 3:56 PM
 */
public class SqlUtils {

	public static String getBaseSelectSql(ModelInfo<?> model) throws IllegalAccessException {
		return getBaseSelect(model.getColumns(), model.getTableName(), getWheres(model));
	}

	public static String getBaseUpdateSql(ModelInfo<?> model) throws IllegalAccessException {
		return getBaseUpdate(model.getPrimaryKeyInfo(), model.getTableName(), getWheres(model));
	}

	public static String getBaseSelect(String col, String table, String... wheres) {
		return String.format("select %s from %s %s", col, table, getWhere("where", wheres));
	}

	private static String getBaseUpdate(ColumnInfo primaryKey, String tableName, String... wheres) throws IllegalAccessException {
		StringBuilder baseSql = new StringBuilder(String.format("update %s  %s", tableName, getWhere("set", wheres)));
		Field field = primaryKey.getField();
		field.setAccessible(true);
		Object obj = field.get(primaryKey.getFieldName());
		baseSql.append(" where ").append(primaryKey.getColumnName()).append("=").append(obj);
		return baseSql.toString();
	}

	public static String getWhere(String pre, String... wheres) {
		StringBuilder baseSql = new StringBuilder();
		if (Objects.nonNull(wheres) && wheres.length > 0) {
			baseSql.append(pre).append(" ");
			Iterator<String> sql = Arrays.asList(wheres).iterator();
			String where;
			do {
				where = sql.next();
				if (StringUtils.isEmpty(where)) continue;
				baseSql.append(where);
				if (sql.hasNext())
					baseSql.append(" and ");
			} while (sql.hasNext());
		}
		String sql = baseSql.toString().trim();
		if (!sql.endsWith(pre)) {
			return sql;
		}
		return sql.replace("where", "");
	}

	public static String[] getWheres(ModelInfo<?> model) throws IllegalAccessException {
		List<String> wheres = new ArrayList<>(model.getSize());
		Object obj;
		Field field;
		for (Map.Entry<String, ColumnInfo> entry : model.getFieldCache().entrySet()) {
			field = entry.getValue().getField();
			field.setAccessible(true);
			obj = field.get(model.getModel());
			if (Objects.nonNull(obj))
				wheres.add(entry.getKey() + "=" + obj);
		}
		return wheres.toArray(new String[0]);
	}

	public static String getTableName(Object model) {
		Table table = model.getClass().getAnnotation(Table.class);
		if (Objects.nonNull(table) && StringUtils.isNotEmpty(table.name())) {
			String schema = table.schema();
			if (StringUtils.isNotEmpty(schema)) {
				return schema + "." + table.name();
			}
			return table.name();
		}
		Entity entity = model.getClass().getAnnotation(Entity.class);
		if (Objects.nonNull(entity) && StringUtils.isNotEmpty(entity.name())) {
			return entity.name();
		}
		return convert(CaseFormat.LOWER_CAMEL, CaseFormat.LOWER_UNDERSCORE, model.getClass().getSimpleName());
	}

	/**
	 * 字符串转换工具
	 *
	 * @param text 字符串
	 * @return 结果
	 */
	public static String convert(CaseFormat from, CaseFormat to, String text) {
		return from.to(to, text);
	}
}
