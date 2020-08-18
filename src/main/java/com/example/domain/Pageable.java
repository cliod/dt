package com.example.domain;

/**
 * 分页参数
 *
 * @author cliod
 * @since 8/18/20 3:05 PM
 */
public class Pageable extends com.wobangkj.bean.Pageable {
	private String order;

	public static Pageable of(String order, final int page, final int size) {
		Pageable pageable = new Pageable();
		pageable.setOrder(order);
		pageable.setPage(page);
		pageable.setSize(size);
		return pageable;
	}

	public static Pageable of(com.wobangkj.bean.Pageable pageable) {
		Pageable pageable0 = new Pageable();
		pageable0.setPage(pageable.getPage());
		pageable0.setSize(pageable0.getSize());
		return pageable0;
	}

	public String getOrder() {
		return order;
	}

	public void setOrder(String order) {
		this.order = order;
	}
}
