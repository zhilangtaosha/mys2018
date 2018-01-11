package com.jing.core.service.impl;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jing.utils.Constant;
import com.jing.utils.paginator.domain.PageBounds;
import com.jing.utils.paginator.domain.PageList;
import com.jing.utils.paginator.domain.PageService;
import java.util.UUID;


import com.jing.core.model.entity.Employee;
import com.jing.core.model.dao.EmployeeMapper;
import com.jing.core.service.EmployeeService;

/**
 * @ClassName: Employee
 * @Description: 员工服务实现类
 * @author: Jinlong He
 * @email: mailto:jinlong_he@126.com
 * @date: 2018年01月11日 15时02分
 */
@Service("employeeService")
@Transactional(readOnly=true)
public class  EmployeeServiceImpl implements EmployeeService {	
	private static final Logger logger = LoggerFactory.getLogger(EmployeeServiceImpl.class);
	
	@Autowired
    private EmployeeMapper employeeMapper;   
    
	@Autowired
	private PageService pageService; // 分页器
	
	
	/**
	 * @Title: addEmployee
	 * @Description:添加员工
	 * @param employee 实体
	 * @return Integer
	 */
	@Override
	@Transactional(readOnly = false)
	public Employee addEmployee(Employee employee){
		employee.setEmpId(UUID.randomUUID().toString().replaceAll("-", "").toUpperCase());
		int ret = employeeMapper.addEmployee(employee);
		if(ret>0){
			return employee;
		}
		return null;
	}
	
	/**
	 * @Title modifyEmployee
	 * @Description:修改员工
	 * @param employee 实体
	 * @return Integer
	 */
	@Override
	@Transactional(readOnly = false)
	public Integer modifyEmployee(Employee employee){
		return employeeMapper.modifyEmployee(employee);
	}
	
	/**
	 * @Title: dropEmployeeByEmpId
	 * @Description:删除员工
	 * @param empId 实体标识
	 * @return Integer
	 */
	@Override
	@Transactional(readOnly = false)
	public Integer dropEmployeeByEmpId(String empId){
		return employeeMapper.dropEmployeeByEmpId(empId);
	}
	
	/**
	 * @Title: queryEmployeeByEmpId
	 * @Description:根据实体标识查询员工
	 * @param empId 实体标识
	 * @return Employee
	 */
	@Override
	public Employee queryEmployeeByEmpId(String empId){
		return employeeMapper.queryEmployeeByEmpId(empId);
	}                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                   
	 
	/**
	 * @Title: queryEmployeeForPage
	 * @Description: 根据员工属性与分页信息分页查询员工信息
	 * @param pagenum 页 
	 * @param pagesize 页大小 
	 * @param sort 排序
	 * @param employee 实体
	 * @return List<Employee>
	 */
	@Override
	public Map<String, Object> queryEmployeeForPage(Integer pagenum, Integer pagesize, String sort, Employee employee){
		HashMap<String, Object> returnMap = new HashMap<String, Object>();
		PageBounds pageBounds = pageService.getPageBounds(pagenum, pagesize, null, true, false);
		pageBounds.setOrdersByJson(sort, Employee.class);
		List<Employee> entityList = employeeMapper.queryEmployeeForPage(pageBounds, employee);
		if(null!=sort && sort.length()>0){
			pageBounds.setOrdersByJson(sort, Employee.class);
		}
		if (!entityList.isEmpty()) {
			PageList<Employee> pagelist = (PageList<Employee>) entityList;
			returnMap.put(Constant.PAGELIST, entityList);
			returnMap.put(Constant.PAGINATOR, pagelist.getPaginator());
		}
		return returnMap;
	}
	 
	/**
	 * @Title: queryEmployeeByProperty
	 * @Description:根据属性查询员工
	 * @return List<Employee>
	 */
	@Override
	public List<Employee> queryEmployeeByProperty(Map<String, Object> map){
		return employeeMapper.queryEmployeeByProperty(map);
	}


}
