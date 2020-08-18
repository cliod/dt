package com.example;

import com.example.api.BaseCrud;
import com.example.domain.Pageable;
import com.example.entity.Attach;
import com.wobangkj.utils.JsonUtils;
import org.junit.Test;

import java.util.List;

public class AppTest {
	@Test
	public void run() {
		try {
			BaseCrud baseCrud = BaseCrud.getInstance();
			Attach attach = baseCrud.selectOne(new Attach());
			System.out.println(JsonUtils.toJson(attach));
			List<Attach> attaches = baseCrud.selectAll(new Attach(), Pageable.of("id desc",1, 10));
			System.out.println(JsonUtils.toJson(attaches));
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}
}
