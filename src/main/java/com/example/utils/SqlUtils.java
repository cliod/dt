package com.example.utils;

import com.example.bean.ColumnInfo;
import com.example.bean.ModelInfo;
import com.google.common.base.CaseFormat;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.lang.reflect.Field;
import java.sql.SQLSyntaxErrorException;
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
		return getBaseUpdate(model.getModel(), model.getPrimaryKeyInfo(), model.getTableName(), getWheres(model));
	}

	public static String getBaseInsertSql(ModelInfo<?> model) throws SQLSyntaxErrorException, NoSuchFieldException, IllegalAccessException {
		return getBaseInsert(model.getTableName(), model.getColumns(), getValues(model), model.getIsAutoGenerate() == 1);
	}

	public static String getBaseDeleteSql(ModelInfo<?> model) {
		return getBaseDelete(model.getTableName());
	}

	public static String getBaseSelect(String col, String table, String... wheres) {
		return String.format("select %s from %s %s", col, table, getWhere("where", "and", wheres));
	}

	private static String getBaseUpdate(Object model, ColumnInfo primaryKey, String tableName, String... wheres) throws IllegalAccessException {
		StringBuilder baseSql = new StringBuilder(String.format("update %s  %s", tableName, getWhere("set", ",", wheres)));
		Field field = primaryKey.getField();
		field.setAccessible(true);
		Object obj = field.get(model);
		baseSql.append(" where ").append(primaryKey.getColumnName()).append("=").append(obj);
		return baseSql.toString();
	}

	private static String getBaseInsert(String tableName, String columns, Object[] values, boolean isAutoGenerate) throws SQLSyntaxErrorException {
		if (isAutoGenerate) {
			if (columns.split(",").length - 1 != values.length) {
				throw new SQLSyntaxErrorException("插入的values的的数量不正确");
			}
		} else {
			if (columns.split(",").length != values.length) {
				throw new SQLSyntaxErrorException("插入的values的的数量不正确");
			}
		}
		StringBuilder baseSql = new StringBuilder(String.format("insert into %s(%s) values", tableName, columns));
		baseSql.append("(");
		for (Object value : values) {
			if (value instanceof CharSequence)
				baseSql.append("'").append(value).append("'");
			else if (value instanceof Number)
				baseSql.append(value);
			else
				baseSql.append("'").append(value).append("'");
		}
		baseSql.append(")");
		return baseSql.toString();
	}

	private static String getBaseDelete(String tableName, String... wheres) {
		return String.format("delete from %s", tableName) + getWhere("where", "and", wheres);
	}

	private static Object[] getValues(ModelInfo<?> model) throws NoSuchFieldException, IllegalAccessException {
		Object obj = model.getModel();
		Collection<String> fields = model.getFieldCache().keySet();
		Object[] values = new Object[fields.size()];
		int i = 0;
		Field field;
		for (String fieldName : fields) {
			field = obj.getClass().getDeclaredField(fieldName);
			field.setAccessible(true);
			values[i] = field.get(obj);
			i++;
		}
		return values;
	}

	public static String getWhere(String pre, String spl, String... wheres) {
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
					baseSql.append(" ").append(spl).append(" ");
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
		for (Map.Entry<String, ColumnInfo> entry : model.getColumnCache().entrySet()) {
			field = entry.getValue().getField();
			field.setAccessible(true);
			obj = field.get(model.getModel());
			if (Objects.nonNull(obj)) {
				if (obj instanceof Number)
					wheres.add(entry.getKey() + "=" + obj);
				else
					wheres.add(entry.getKey() + "='" + obj + "'");
			}
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
