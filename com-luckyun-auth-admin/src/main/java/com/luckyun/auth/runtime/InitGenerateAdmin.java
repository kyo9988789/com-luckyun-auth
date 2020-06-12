package com.luckyun.auth.runtime;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.luckyun.auth.config.AuthBaseConfig;
import com.luckyun.auth.mapper.SysAccountMapper;
import com.luckyun.auth.mapper.SysCompanyMapper;
import com.luckyun.auth.mapper.SysModuleMapper;
import com.luckyun.auth.mapper.SysModuleOperateMapper;
import com.luckyun.auth.mapper.SysOperateGroupMapper;
import com.luckyun.auth.mapper.SysOperateMapper;
import com.luckyun.auth.mapper.SysRoleMapper;
import com.luckyun.auth.mapper.SysRoleOperateMapper;
import com.luckyun.auth.mapper.SysRoleTypeMapper;
import com.luckyun.auth.mapper.SysUserRoleMapper;
import com.luckyun.auth.service.SysAccountService;
import com.luckyun.core.km.config.LuckCoreLuttucePools;
import com.luckyun.model.company.SysCompany;
import com.luckyun.model.module.SysModule;
import com.luckyun.model.module.SysModuleOperate;
import com.luckyun.model.operate.SysOperate;
import com.luckyun.model.operate.SysOperateGroup;
import com.luckyun.model.role.SysRole;
import com.luckyun.model.role.SysRoleOperate;
import com.luckyun.model.role.SysRoleType;
import com.luckyun.model.user.SysAccount;
import com.luckyun.model.user.SysUserRole;

import lombok.extern.slf4j.Slf4j;

/**
 * 生成超级管理员
 * @author yangj080
 *
 */
@Component
@Slf4j
public class InitGenerateAdmin {
	
	@Autowired
	private AuthBaseConfig authBaseConfig;
	
	@Autowired
	private SysAccountMapper sysAccountMapper;
	
	@Autowired
	private SysModuleMapper sysModuleMapper;
	
	@Autowired
	private SysAccountService sysAccountService;
	
	@Autowired
	private SysUserRoleMapper sysUserRoleMapper;
	
	@Order(9)
	@EventListener
	public void initAdminAndModule(ContextRefreshedEvent event) throws Exception {
		if(!isExistsAdmin() && !moduleIsExist()) {
			//生成公司
			generateCompany();
			//生成模块
			List<SysModule> sysModules = generateBaseModule();
			if(sysModules == null) {
				throw new Exception ("redis数据与数据库数据不一致,请检查数据库与redis数据");
			}
			SysAccount account = generateAccountData(authBaseConfig.getReleaseUser());
			SysRoleType roleType = generateRoleType();
			SysRole role = generateAdminRole(roleType);
			generateAccountRole(account, role);
			SysOperateGroup group = generateGroup();
			List<SysOperate> sysOperates = generateOperate(group.getIndocno());
			for(SysModule module : sysModules) {
				if(!module.getIndocno().equals(1L)) {
					for(SysOperate operate : sysOperates) {
						SysModuleOperate moduleOperate = generateModuleOperate(module, operate);
						generateRoleOperate(role, moduleOperate);
					}
				}
			}
			
		}else {
			log.info("表数据存在,无需初始化");
		}
	}
	
	private boolean isExistsAdmin() {
		String admin = authBaseConfig.getReleaseUser();
		//不需要添加超级管理员,算作存在超级管理员
		if(StringUtils.isEmpty(admin)) {
			return true;
		}
		SysAccount account = sysAccountMapper.findBySloginid(admin);
		if(account != null) {
			return true;
		}
		return false;
	}
	
	private boolean moduleIsExist() {
		
		Long moduleCount = sysModuleMapper.findModuleCount();
		
		if(!moduleCount.equals(0L)) {
			return true; 
		}
		return false;
	}
	
	private SysAccount generateAccountData(String sloginid) {
		SysAccount account = new SysAccount();
		Long indocno =  LuckCoreLuttucePools.getHincrById(account.__getTableName());
		account.setIndocno(indocno);
		account.setSloginid(sloginid);
		account.setSpassword("123456");
		account.setSname("超级管理员");
		account.setSpinyinsingle("cjgly");
		account.setSpinyinfull("chaojiguanliyuan");
		account.setItype(0);
		account.setIstate(1);
		account.setDregt(new Date());
		sysAccountService.addSysUserInfo(account);
		return account;
	}
	
	private void generateAccountRole(SysAccount account,SysRole role) {
		SysUserRole sysUserRole = new SysUserRole();
		sysUserRole.setIroleid(role.getIndocno());
		sysUserRole.setIuserid(account.getIndocno());
		sysUserRoleMapper.insert(sysUserRole);
	}
	
	@Autowired
	private SysRoleMapper sysRoleMapper;
	
	private SysRole generateAdminRole(SysRoleType roleType) {
		SysRole role = new SysRole();
		Long indocno =  LuckCoreLuttucePools.getHincrById(role.__getTableName());
		role.setIndocno(indocno);
		role.setIlevel(0);
		role.setIsort(1);
		role.setSname("超级管理员");
		role.setItypeid(roleType.getIndocno());
		role.setDregt(new Date());
		role.setSregid(1L);
		sysRoleMapper.insert(role);
		return role;
	}
	
	@Autowired
	private SysRoleTypeMapper sysRoleTypeMapper;
	
	private SysRoleType generateRoleType() {
		SysRoleType sysRoleType = new SysRoleType();
		sysRoleType.setSname("超级管理员");
		sysRoleType.setDregt(new Date());
		sysRoleType.setSregid(1L);
		sysRoleTypeMapper.insert(sysRoleType);
		return sysRoleType;
	}
	/**
	 * 生成系统基础模块
	 * @return 基础模块列表
	 */
	private List<SysModule> generateBaseModule(){
		Object[][] modules = defaultModules();
		List<SysModule> modules2 = new ArrayList<SysModule>(modules.length);
		for(Object[] objects : modules) {
			SysModule module = new SysModule();
			Long indocno = LuckCoreLuttucePools.getHincrById(module.__getTableName());
			if((int)objects[0] == 0) {
				if(!indocno.equals(1L)) {
					log.error("redis主键生成数据异常");
					return null;
				}
			}
			module.setIndocno(indocno);
			String iparentid = objects[0] !=null ? objects[0].toString() : "0";
			module.setIparentid(Long.valueOf(iparentid));
			String sname = objects[1] !=null ? objects[1].toString() : "";
			module.setSname(sname);
			String spath = objects[2] != null ? objects[2].toString() : "";
			module.setSpath(spath);
			String spathalias = objects[3] != null ? objects[3].toString() : "";
			module.setSpathalias(spathalias);
			module.setIstate((int)objects[4]);
			module.setIsort((int)objects[5]);
			module.setImenu((int)objects[6]);
			String sicon = objects[7] != null ? objects[7].toString() : "";
			module.setSicon(sicon);
			module.setDregt(new Date());
			module.setSregid(1L);
			if(indocno.equals(1L)) {
				module.setSidcc("/1/");
			}else {
				module.setSidcc("/1/"+indocno+"/");
			}
			sysModuleMapper.insert(module);
			modules2.add(module);
		}
		return modules2;
	}
	
	private Object[][] defaultModules(){
		Object [][] modules = new Object[][]
		{
			{0,"系统管理","/sys/",null,1,1,1,"iconxitongguanli"},
			{1,"登录日志","/Sys/LogLogin","/base/logger/",1,1,1,null},
			{1,"回收站","/Sys/Recycle","/base/recycle/",1,2,1,null},
			{1,"用户管理","/Sys/User/","/base/user/",1,3,1,null},
			{1,"操作管理","/Sys/Operate/","/base/operate/",1,4,1,null},
			{1,"模块管理","/Sys/Module/","/base/module/",1,5,1,null},
			{1,"数据字典","/Sys/Datadic","/base/datadic/",1,6,1,null},
			{1,"专业管理","/Sys/Major","/base/major/",1,7,1,null},
			{1,"部门管理","/Sys/Department","/base/dept/",1,8,1,null},
			{1,"岗位管理","/Sys/Post","/base/post/",1,9,1,"iconxitongguanli"},
			{1,"角色管理","/Sys/Role/","/base/role/",1,10,1,null},
			{1,"公司管理","/Sys/Company","/base/company/",1,11,1,null},
			{1,"导入导出管理","/report/imp/template","/report/",1,12,1,null},
		};
		return modules;
	}
	
	@Autowired
	private SysOperateGroupMapper sysOperateGroupMapper;
	
	private SysOperateGroup generateGroup() {
		SysOperateGroup group = new SysOperateGroup();
		Long indocno = LuckCoreLuttucePools.getHincrById(group.__getTableName());
		group.setIndocno(indocno);
		group.setSname("基础操作");
		group.setDregt(new Date());
		group.setSregid(1L);
		sysOperateGroupMapper.insert(group);
		return group;
	}
	
	@Autowired
	private SysOperateMapper sysOperateMapper;
	
	private List<SysOperate> generateOperate(Long igroupid){
		String[][] operates = new String[][]
		{
			{"查看","read","1"},
			{"修改","update","2"},
			{"添加","add","3"},
			{"删除","delete","4"},
		};
		List<SysOperate> sysOperates = new ArrayList<SysOperate>();
		for(String[] operStrings : operates) {
			SysOperate operate = new SysOperate();
			Long indocno  = LuckCoreLuttucePools.getHincrById(operate.__getTableName());
			operate.setIndocno(indocno);
			operate.setIgroupid(igroupid);
			operate.setSname(operStrings[0]);
			operate.setSpath(operStrings[1]);
			operate.setIsort(Integer.valueOf(operStrings[2]));
			operate.setSregid(1L);
			operate.setDregt(new Date());
			sysOperateMapper.insert(operate);
			sysOperates.add(operate);
		}
		return sysOperates;
	}
	
	@Autowired
	private SysModuleOperateMapper sysModuleOperateMapper;
	
	private SysModuleOperate generateModuleOperate(SysModule module,SysOperate operate) {
		SysModuleOperate sysModuleOperate = new SysModuleOperate();
		Long indocno = LuckCoreLuttucePools.getHincrById(sysModuleOperate.__getTableName());
		sysModuleOperate.setImoduleid(module.getIndocno());
		sysModuleOperate.setIoperateid(operate.getIndocno());
		sysModuleOperate.setIndocno(indocno);
		sysModuleOperateMapper.insert(sysModuleOperate);
		return sysModuleOperate;
	}
	
	@Autowired
	private SysRoleOperateMapper sysRoleOperateMapper;
	
	private SysRoleOperate generateRoleOperate(SysRole role,SysModuleOperate moduleOperate) {
		SysRoleOperate sysRoleOperate = new SysRoleOperate();
		sysRoleOperate.setIoperateid(moduleOperate.getIndocno());
		sysRoleOperate.setIroleid(role.getIndocno());
		sysRoleOperateMapper.insert(sysRoleOperate);
		return sysRoleOperate;
	}
	
	@Autowired
	private SysCompanyMapper sysCompanyMapper; 
	
	private void generateCompany() {
		SysCompany company = new SysCompany();
		Long indocno = LuckCoreLuttucePools.getHincrById(company.__getTableName());
		company.setIndocno(indocno);
		company.setSname("默认公司");
		company.setIstate(1);
		company.setIdel(0);
		company.setDregt(new Date());
		company.setSregid(1L);
		company.setIsort(1);
		sysCompanyMapper.insert(company);
	}
}
