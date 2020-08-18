package com.example.domain;

import lombok.Data;
import org.jetbrains.annotations.NotNull;

/**
 * 分页参数
 *
 * @author cliod
 * @since 8/18/20 3:05 PM
 */
@Data
public class Pageable {
	private String order;
	private Integer page;
	private Integer size;

	@NotNull
	public static Pageable of(String order, final int page, final int size) {
		Pageable pageable = new Pageable();
		pageable.setOrder(order);
		pageable.setPage(page);
		pageable.setSize(size);
		return pageable;
	}

	public Integer getOffset() {
		return size * (page - 1);
	}

	public Integer getLimit() {
		return size;
	}
}
