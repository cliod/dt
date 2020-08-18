package com.example.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wobangkj.api.Session;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 附件信息
 *
 * @author cliod
 * @since 8/12/20 1:53 PM
 */
@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Attach implements Session {
	private static final long serialVersionUID = 1119027944575626065L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	/**
	 * 时间
	 */
	@Column(columnDefinition = "datetime")
	@JsonFormat(pattern = "yyyy-MM-dd hh:mm:ss")
	private LocalDateTime createTime;
	/**
	 * 文件名
	 */
	private String name;
	/**
	 * 文件地址
	 */
	private String url;
	/**
	 * 语言
	 */
	@JsonIgnore
	@Column(columnDefinition = "varchar(8) default 'zh_cn'", nullable = false)
	private String language;
	/**
	 * 文件路径
	 */
	@JsonIgnore
	private String path;
	/**
	 * 三方id
	 */
	@JsonIgnore
	@Column(columnDefinition = "bigint(1) default 0")
	private Long tid;
	/**
	 * 三方类型, 0文章, 1轮播图
	 */
	@JsonIgnore
	@Column(columnDefinition = "tinyint(1) default 0")
	private Integer type;
}
