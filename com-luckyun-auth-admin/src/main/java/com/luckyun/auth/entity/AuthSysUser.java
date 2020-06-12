package com.luckyun.auth.entity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.luckyun.model.dept.SysDept;
import com.luckyun.model.role.SysRole;

/**
 * 
 * @author yj
 *
 */
public class AuthSysUser implements UserDetails {

	/**
	 * @Fields field:field:{todo}(用一句话描述这个变量表示什么)
	 */
	private static final long serialVersionUID = 771823300329256520L;

	private Long indocno;

	private String sloginid;

	private String spassword;
	
	private String sname;
	
	private Integer istate;
	
	/**
	 * 角色列表数据
	 */
	private List<SysRole> roleList;
	/**
	 * 管辖部门
	 */
	private List<SysDept> deptList;
	
	/**
	 * 所属公司
	 */
	private Long icompanyid;
	
	/**
	 * 所属部门
	 */
	private Long ideptid;
	
	/**
	 * 过期时间
	 */
	private Date dovertime;
	
	/**
	 * 操作列表
	 */
	private String operateLists;
	
	/**
	 * 当前访问地址所属模板的名称
	 */
	private String cmodulenm;

	public Integer getIstate() {
		return istate;
	}

	public void setIstate(Integer istate) {
		this.istate = istate;
	}

	public Long getIndocno() {
		return indocno;
	}

	public void setIndocno(Long indocno) {
		this.indocno = indocno;
	}

	public String getSloginid() {
		return sloginid;
	}

	public void setSloginid(String sloginid) {
		this.sloginid = sloginid;
	}

	public String getSpassword() {
		return spassword;
	}

	public void setSpassword(String spassword) {
		this.spassword = spassword;
	}

	public String getSname() {
		return sname;
	}

	public void setSname(String sname) {
		this.sname = sname;
	}
	
	public List<SysRole> getRoleList() {
		return roleList;
	}

	public void setRoleList(List<SysRole> roleList) {
		this.roleList = roleList;
	}
	
	
	public List<SysDept> getDeptList() {
		return deptList;
	}

	public void setDeptList(List<SysDept> deptList) {
		this.deptList = deptList;
	}

	public Long getIcompanyid() {
		return icompanyid;
	}

	public void setIcompanyid(Long icompanyid) {
		this.icompanyid = icompanyid;
	}
	
	public String getOperateLists() {
		return operateLists;
	}

	public void setOperateLists(String operateLists) {
		this.operateLists = operateLists;
	}

	public Long getIdeptid() {
		return ideptid;
	}

	public void setIdeptid(Long ideptid) {
		this.ideptid = ideptid;
	}

	public Date getDovertime() {
		return dovertime;
	}

	public void setDovertime(Date dovertime) {
		this.dovertime = dovertime;
	}

	@JsonIgnore
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		List<SimpleGrantedAuthority> authorities = new ArrayList<>();
		return authorities;
	}

	 //密码    
	@JsonIgnore
	@Override
	public String getPassword() {
		return spassword;
	}

	//用户名
	@JsonIgnore
	@Override
	public String getUsername() {
		return sloginid;
	}

	//用户名是否没有过期
	@JsonIgnore
	@Override
	public boolean isAccountNonExpired() {
		if(istate != null && istate==3) {
			return false;
		}
		return true;
	}

	//用户名是否没有锁定    
	@JsonIgnore
	@Override
	public boolean isAccountNonLocked() {
		if(istate != null && istate == 2) {
			return false;
		}
		return true;
	}

	//用户密码是否没有过期
	@JsonIgnore
	@Override
	public boolean isCredentialsNonExpired() {
		if(istate != null && istate==3) {
			return false;
		}
		return true;
	}

	//账号是否可用(可理解为是否删除)
	@JsonIgnore
	@Override
	public boolean isEnabled() {
		if(istate != null && istate == 0) {
			return false;
		}
		return true;
	}

	public String getCmodulenm() {
		return cmodulenm;
	}

	public void setCmodulenm(String cmodulenm) {
		this.cmodulenm = cmodulenm;
	}
}
