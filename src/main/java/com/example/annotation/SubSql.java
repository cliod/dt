package com.example.annotation;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * join的字段标注
 *
 * @author cliod
 * @since 7/30/20 4:23 PM
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface SubSql {

	String name() default "";

	/**
	 * 查询语句
	 *
	 * @return 语句
	 */
	String query();
}
