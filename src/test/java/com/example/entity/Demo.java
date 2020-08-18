package com.example.entity;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 测试demo
 *
 * @author cliod
 * @since 8/17/20 3:24 PM
 */
@Table
public class Demo {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
}
