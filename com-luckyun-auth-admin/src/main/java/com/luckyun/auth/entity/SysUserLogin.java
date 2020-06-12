package com.luckyun.auth.entity;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.luckyun.model.data.BaseEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 登录日志记录表
 * @author yangj080
 *
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class SysUserLogin extends BaseEntity implements Serializable {

	private static final long serialVersionUID = -8716722981868050033L;

	/** 主键 */
	private Long indocno;
	
	/**
	 * 登录人编号
	 */
	private Long iuserid;
	
	/**
	 * 登录人
	 */
	private String susernm;
	/**标识（0失败/1成功）*/
	private Integer ilogintype;
	/**登录帐号*/
	private String sloginid;
	/**登录IP*/
	private String sloginip;
	/**登录MAC*/
	private String sloginmac;
	/**登录时间*/
	@JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
	private Date dlogintime = new Date();
	/**登录详细信息*/
	private String sloginmsg;
	@Override
	public String __getTableName() {
		return "sys_user_login";
	}
	@Override
	public boolean __isTrueDel() {
		return false;
	}
}
