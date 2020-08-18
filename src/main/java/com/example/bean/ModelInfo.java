package com.example.bean;

import com.google.common.base.CaseFormat;
import com.example.utils.SqlUtils;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.RowMapper;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 对象信息
 *
 * @author cliod
 * @since 8/17/20 5:38 PM
 */
@Getter
@EqualsAndHashCode
public class ModelInfo<T> {

	private final Class<T> type;
	private final Map<String, ColumnInfo> columnCache = new HashMap<>();
	private final Map<String, ColumnInfo> fieldCache = new HashMap<>();
	private String tableName;
	/**
	 * 主键的名称
	 */
	private transient ColumnInfo primaryKeyInfo;
	/**
	 * 模型
	 */
	private transient Object model;
	/**
	 * 数据库字段
	 */
	private transient String columns = "";
	/**
	 * 用户自增id
	 */
	private transient byte isAutoGenerate = 0;
	/**
	 * 是否程序自增, 默认是
	 */
	@Setter
	private transient boolean isProAutoGenerate = true;

	@SuppressWarnings("unchecked")
	protected ModelInfo(T model) {
		this.model = model;
		this.type = (Class<T>) model.getClass();
	}

	public static <T> ModelInfo<T> of(T model) {
		ModelInfo<T> info = new ModelInfo<>(model);
		info.init();
		return info;
	}

	public void setModel(Object model) {
		this.model = model;
	}

	private void init() {
		Field[] fields = this.type.getDeclaredFields();
		String fieldName;
		String columnName;
		Class<?> fieldType;
		for (Field field : fields) {
			Transient t = field.getAnnotation(Transient.class);
			// 指定注解不存储
			if (Objects.nonNull(t)) continue;
			// 静态和最终修饰的字段不需要
			if (exclude(field.getModifiers())) continue;
			// 构建成column info
			fieldType = field.getType();
			fieldName = field.getName();
			columnName = SqlUtils.convert(CaseFormat.LOWER_CAMEL, CaseFormat.LOWER_UNDERSCORE, fieldName);
			ColumnInfo info = ColumnInfo.of(field, fieldName, columnName, fieldType);
			// 放入缓存
			columnCache.put(columnName, info);
			fieldCache.put(fieldName, info);
			// 主键相关
			if (field.isAnnotationPresent(Id.class)) {
				primaryKeyInfo = info;
				if (fieldType.equals(Long.class) && field.isAnnotationPresent(GeneratedValue.class)) {
					GeneratedValue generatedValue = field.getAnnotation(GeneratedValue.class);
					if (Objects.nonNull(generatedValue) && GenerationType.IDENTITY.equals(generatedValue.strategy())) {
						isAutoGenerate = 1;
					}
				}
			}
		}
		columns = String.join(",", columnCache.keySet());
		tableName = SqlUtils.getTableName(this.model);
	}

	public int getSize() {
		return this.columnCache.size();
	}

	private boolean exclude(int mod) {
		return Modifier.isStatic(mod) || Modifier.isFinal(mod);
	}

	public RowMapper<T> beanRowMapper() throws IllegalArgumentException {
		return new BeanPropertyRowMapper<>(type);
	}

	public RowMapper<Map<String, Object>> mapRowMapper() {
		return new ColumnMapRowMapper();
	}
}
