package com.jing.attendance.controller;

import java.util.Map;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jing.config.web.exception.NotFoundException;
import com.jing.config.web.exception.ParameterException;
import java.beans.IntrospectionException;
import java.lang.reflect.InvocationTargetException;
import com.jing.config.validation.BeanValidator;
import com.jing.attendance.model.entity.AttendanceDiary;
import com.jing.attendance.service.AttendanceDiaryService;
import com.jing.utils.ClassUtil;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * @ClassName: AttendanceDiaryController
 * @Description: 打卡记录HTTP接口
 * @author: Jinlong He
 * @email: mailto:jinlong_he@126.com
 * @date: 2018年02月05日 22时39分
 */
@RestController
@Api(description="打卡记录")
public class AttendanceDiaryController{

	@Autowired
	BeanValidator beanValidator;
	
	@Autowired
	private AttendanceDiaryService attendanceDiaryService;

	///syswhite
	
	@ApiOperation(value = "新增 添加打卡记录信息", notes = "添加打卡记录信息")
	@RequestMapping(value = "/attendancediary", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
	public Object addAttendanceDiary(HttpServletResponse response,
			@ApiParam(value = "attendanceDiary") @RequestBody AttendanceDiary attendanceDiary) throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException {
		attendanceDiary.setAttId(null);
		attendanceDiaryService.addAttendanceDiary(attendanceDiary);
		return attendanceDiary;
	}
	
	
	@ApiOperation(value = "更新 根据打卡记录标识更新打卡记录信息", notes = "根据打卡记录标识更新打卡记录信息")
	@RequestMapping(value = "/attendancediary/{attId:.+}", method = RequestMethod.PUT)
	public Object modifyAttendanceDiaryById(HttpServletResponse response,
			@PathVariable Integer attId,
			@ApiParam(value = "attendanceDiary", required = true) @RequestBody AttendanceDiary attendanceDiary
			) throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException {
		AttendanceDiary tempAttendanceDiary = attendanceDiaryService.queryAttendanceDiaryByAttId(attId);
		attendanceDiary.setAttId(attId);
		if(null == tempAttendanceDiary){
			throw new NotFoundException("打卡记录");
		}
		return attendanceDiaryService.modifyAttendanceDiary(attendanceDiary);
	}

	@ApiOperation(value = "删除 根据打卡记录标识删除打卡记录信息", notes = "根据打卡记录标识删除打卡记录信息")
	@RequestMapping(value = "/attendancediary/{attId:.+}", method = RequestMethod.DELETE)
	public Object dropAttendanceDiaryByAttId(HttpServletResponse response, @PathVariable Integer attId) {
		AttendanceDiary attendanceDiary = attendanceDiaryService.queryAttendanceDiaryByAttId(attId);
		if(null == attendanceDiary){
			throw new NotFoundException("打卡记录");
		}
		return attendanceDiaryService.dropAttendanceDiaryByAttId(attId);
	}
	
	@ApiOperation(value = "查询 根据打卡记录标识查询打卡记录信息", notes = "根据打卡记录标识查询打卡记录信息")
	@RequestMapping(value = "/attendancediary/{attId:.+}", method = RequestMethod.GET)
	public Object queryAttendanceDiaryById(HttpServletResponse response,
			@PathVariable Integer attId) {
		AttendanceDiary attendanceDiary = attendanceDiaryService.queryAttendanceDiaryByAttId(attId);
		if(null == attendanceDiary){
			throw new NotFoundException("打卡记录");
		}
		return attendanceDiary;
	}
	
	@ApiOperation(value = "查询 根据打卡记录属性查询打卡记录信息列表", notes = "根据打卡记录属性查询打卡记录信息列表")
	@RequestMapping(value = "/attendancediary", method = RequestMethod.GET)
	public Object queryAttendanceDiaryList(HttpServletResponse response,
			AttendanceDiary attendanceDiary) throws IntrospectionException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {		
		return attendanceDiaryService.queryAttendanceDiaryByProperty(ClassUtil.transBean2Map(attendanceDiary, false));
	}
	
	@ApiOperation(value = "查询分页 根据打卡记录属性分页查询打卡记录信息列表", notes = "根据打卡记录属性分页查询打卡记录信息列表")
	@RequestMapping(value = "/attendancediarys", method = RequestMethod.GET)
	public Object queryAttendanceDiaryPage(HttpServletResponse response,
			@RequestParam(value = "pageNo", required = false) Integer pagenum,
			@RequestParam(value = "pageSize", required = false) Integer pagesize, 
			@RequestParam(value = "sort", required = false) String sort, AttendanceDiary attendanceDiary) {				
		return attendanceDiaryService.queryAttendanceDiaryForPage(pagenum, pagesize, sort, attendanceDiary);
	}

}