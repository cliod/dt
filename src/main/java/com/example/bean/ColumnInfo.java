package com.example.bean;

import lombok.Data;

import java.lang.reflect.Field;

/**
 * 字段与数据库属性映射
 *
 * @author cliod
 * @since 8/18/20 9:34 AM
 */
@Data
public class ColumnInfo {
	private Field field;
	private String fieldName;
	private String columnName;
	private Class<?> fieldType;

	static ColumnInfo of(Field field, String fieldName, String columnName, Class<?> fieldType) {
		ColumnInfo info = new ColumnInfo();
		info.setField(field);
		info.setFieldType(fieldType);
		info.setFieldName(fieldName);
		info.setColumnName(columnName);
		return info;
	}
}
