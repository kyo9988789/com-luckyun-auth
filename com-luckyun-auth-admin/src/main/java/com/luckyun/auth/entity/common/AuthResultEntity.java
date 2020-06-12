package com.luckyun.auth.entity.common;

import java.io.Serializable;

/**
 * 接口请求返回实体类
 * 
 * @author Jonny
 * @version 1.0.0
 */
public class AuthResultEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 状态（1:成功,0:失败）
	 */
	private Integer code; 
	/**
	 * 返回信息码
	 */
	private String msgno; 
	/**
	 * 返回信息
	 */
	private String msg; 
	/**
	 * 返回导入错误文件名称
	 */
	private String spaths;
	private String key;
	 /**
	  * 返回内容
	  */
	private Object content;
	 /**
	  * 总数
	  */
	private Long totalcount;

	private Long msgid;

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public String getMsgno() {
		return msgno;
	}

	public void setMsgno(String msgno) {
		this.msgno = msgno;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public Long getTotalcount() {
		return totalcount;
	}

	public void setTotalcount(Long totalcount) {
		this.totalcount = totalcount;
	}

	public String getSpaths() {
		return spaths;
	}

	public void setSpaths(String spaths) {
		this.spaths = spaths;
	}

	public Object getContent() {
		return content;
	}

	public void setContent(Object content) {
		this.content = content;
	}

	public Long getMsgid() {
		return msgid;
	}

	public void setMsgid(Long msgid) {
		this.msgid = msgid;
	}

	public enum StatusCode {
		/**
		 * 失败
		 */
		FAILURE(0),
		/**
		 * 成功
		 */
		SUCCESS(1);
		private Integer code;

		private StatusCode(Integer code) {
			this.code = code;
		}

		public Integer getCode() {
			return code;
		}

		public void setCode(Integer code) {
			this.code = code;
		}
	}
}
