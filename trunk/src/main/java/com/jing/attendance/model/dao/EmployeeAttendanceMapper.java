package com.jing.attendance.model.dao;


import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.jing.utils.paginator.domain.PageBounds;
import com.jing.attendance.model.entity.EmployeeAttendance;

/**
 * @ClassName: EmployeeAttendanceMapper
 * @Description: 员工考勤关系映射
 * @author: Jinlong He
 * @email: mailto:jinlong_he@126.com
 * @date: 2018年01月11日 15时03分
 */
@Mapper
public interface EmployeeAttendanceMapper {

	/**
	 * @Title: addEmployeeAttendance
	 * @Description:添加员工考勤关系
	 * @param employeeAttendance 实体
	 * @return Integer
	 */
	Integer addEmployeeAttendance(EmployeeAttendance employeeAttendance);
	
	/**
	 * @Title modifyEmployeeAttendance
	 * @Description:修改员工考勤关系
	 * @param employeeAttendance 实体
	 * @return Integer
	 */
	Integer modifyEmployeeAttendance(EmployeeAttendance employeeAttendance);
	
	/**
	 * @Title: dropEmployeeAttendanceByLinkId
	 * @Description:删除员工考勤关系
	 * @param linkId 实体标识
	 * @return Integer
	 */
	Integer dropEmployeeAttendanceByLinkId(Integer linkId);
	
	/**
	 * @Title: queryEmployeeAttendanceByLinkId
	 * @Description:根据实体标识查询员工考勤关系
	 * @param linkId 实体标识
	 * @return EmployeeAttendance
	 */
	EmployeeAttendance queryEmployeeAttendanceByLinkId(Integer linkId);
	 
	/**
	 * @Title: queryEmployeeAttendanceForPage
	 * @Description: 根据员工考勤关系属性与分页信息分页查询员工考勤关系信息
	 * @param pageBounds 分页信息
	 * @param employeeAttendance 实体
	 * @return List<EmployeeAttendance>
	 */
	List<EmployeeAttendance> queryEmployeeAttendanceForPage(PageBounds pageBounds, @Param("employeeAttendance") EmployeeAttendance employeeAttendance);
	 
	 /**
	  * @Title: queryEmployeeAttendanceByProperty
	  * @Description:根据属性查询员工考勤关系
	  * @return List<EmployeeAttendance>
	  */
	 List<EmployeeAttendance> queryEmployeeAttendanceByProperty(@Param("employeeAttendance") Map<String, Object> map);
	 
	 
	 
}
