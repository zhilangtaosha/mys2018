package com.jing.attendance.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jing.attendance.controller.vo.AttendanceBo;
import com.jing.attendance.model.entity.Attendance;
import com.jing.attendance.model.entity.AttendanceTime;
import com.jing.attendance.service.AttendanceService;
import com.jing.attendance.service.AttendanceTimeService;
import com.jing.config.validation.BeanValidator;
import com.jing.config.web.exception.NotFoundException;
import com.jing.config.web.exception.ParameterException;
import com.jing.system.login.session.Config;
import com.jing.system.login.session.SessionAttr;
import com.jing.system.user.entity.User;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * @ClassName: AttendanceController
 * @Description: 考勤HTTP接口
 * @author: Jinlong He
 * @email: mailto:jinlong_he@126.com
 * @date: 2018年01月11日 15时03分
 */
@RestController
@Api(description="考勤规则", tags={"AttendanceTime"})
public class AttendanceController{

	@Autowired
	BeanValidator beanValidator;
	
	@Autowired
	private AttendanceService attendanceService;
		
	@Autowired
	private AttendanceTimeService attendanceTimeService;	

	private static final Integer MAX_TIME_PER_ATT = 3;
	
	
	@ApiOperation(value = "新增 添加考勤信息", notes = "添加考勤信息 暂时后台固定为type=2详情")
	@RequestMapping(value = "/attendance", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
	public Object addAttendance(HttpServletResponse response,
			@ApiParam(value = "attendance") @RequestBody AttendanceBo attendance, @SessionAttr(Config.USER_INFO) User user) throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException {
		attendance.setTypes(2); //暂时只支持2     考勤方案方案 0休天数 1考勤天数 2详情
		attendance.setStatus(0);
		List<Map<String, String>> errors = beanValidator.validateClassAuto(attendance, true);		
		if(!errors.isEmpty()){
			throw new ParameterException(errors);
		}
		if(attendance.getAttTime()!=null && attendance.getAttTime().size()>MAX_TIME_PER_ATT.intValue()){
			Map<String, String> e = new HashMap<String, String>();
			e.put("field", "attTime");
			e.put("message", "每考勤规则限定最多"+MAX_TIME_PER_ATT+"个时间段。");
			errors.add(e);
		}
		errors.addAll(beanValidator.validateClassAuto(attendance.getAttTime(), true));
		if(!errors.isEmpty()){
			throw new ParameterException(errors);
		}			
		if(attendance.getTypes().intValue()<2 && (attendance.getAttendance()==null || attendance.getAttendance().intValue()==0)){
			throw new ParameterException("attendance","类型为0休天数或1考勤天数时，对应天数必传且不能为零。");
		}		
		attendance.setAttendanceId(null);
		attendance.setOutCt(null);//允许考勤提前时间
		attendance.setSignCt(null);//允许考勤推迟时间
		attendance.setCreatedBy(user.getUserId());
		attendance.setUpdatedBy(attendance.getCreatedBy());
		attendanceService.addAttendance(attendance);
		return attendance;
	}
	
	
	@ApiOperation(value = "更新 根据考勤标识更新考勤信息", notes = "请注意，时段修订时必须传入时段标识，否则认作新增")
	@RequestMapping(value = "/attendance/{attendanceId:.+}", method = RequestMethod.PUT)
	public Object modifyAttendanceById(HttpServletResponse response,
			@PathVariable Integer attendanceId,
			@ApiParam(value = "attendance", required = true) @RequestBody AttendanceBo attendance, @SessionAttr(Config.USER_INFO) User user
			) throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException {
		List<Map<String, String>> errors = beanValidator.validateClassAuto(attendance, false);				
		if(attendance.getAttTime()!=null && attendance.getAttTime().size()>MAX_TIME_PER_ATT.intValue()){
			Map<String, String> e = new HashMap<String, String>();
			e.put("field", "attTime");
			e.put("message", "每考勤规则限定最多"+MAX_TIME_PER_ATT+"个时间段。");
			errors.add(e);			
		}
		AttendanceBo tempAttendance = attendanceService.queryAttendanceByAttendanceId(attendanceId);	
		if(null == tempAttendance){
			throw new NotFoundException("考勤");
		}
		if(attendance.getAttTime()!=null && attendance.getAttTime().size()!=0) {
			errors.addAll(beanValidator.validateClassAuto(attendance.getAttTime(), true));
			if(tempAttendance.getAttTime()!=null ) {
				int i = tempAttendance.getAttTime().size();
				for(AttendanceTime atttt: attendance.getAttTime()) {
					if(atttt.getId()==null) {i++;} //有新增考勤时段
				}
				if(i>MAX_TIME_PER_ATT.intValue()) {
					Map<String, String> e = new HashMap<String, String>();
					e.put("field", "attTime");
					e.put("message", "每考勤规则限定最多"+MAX_TIME_PER_ATT+"个时间段。");
					errors.add(e);	
				}
			}
		}
		if(!errors.isEmpty()){
			throw new ParameterException(errors);
		}	
		if(attendance.getTypes()!=null && attendance.getTypes().intValue()!=tempAttendance.getTypes().intValue()){
			throw new ParameterException("types","考勤类型不允许变更。");
		}
		attendance.setAttendanceId(attendanceId);
		attendance.setOutCt(null);//允许考勤提前时间
		attendance.setSignCt(null);//允许考勤推迟时间
		attendance.setUpdatedBy(attendance.getCreatedBy());
		return attendanceService.modifyAttendance(attendance);
	}

	@ApiOperation(value = "删除 根据考勤标识删除考勤信息", notes = "根据考勤标识删除考勤信息")
	@RequestMapping(value = "/attendance/{attendanceId:.+}", method = RequestMethod.DELETE)
	public Object dropAttendanceByAttendanceId(HttpServletResponse response, @PathVariable Integer attendanceId, @SessionAttr(Config.USER_INFO) User user) {
		Attendance attendance = attendanceService.queryAttendanceByAttendanceId(attendanceId);
		if(null == attendance){
			throw new NotFoundException("考勤");
		}
//		if(attendanceId.intValue()==1) {
//			throw new ParameterException("attendanceId", "全局考勤规则不允许删除。");
//		}
		attendance.setUpdatedBy(attendance.getCreatedBy());
		return attendanceService.dropAttendanceByAttendanceId(attendanceId);
	}
	
	@ApiOperation(value = "查询 根据考勤标识查询考勤信息", notes = "根据考勤标识查询考勤信息")
	@RequestMapping(value = "/attendance/{attendanceId:.+}", method = RequestMethod.GET)
	public Object queryAttendanceById(HttpServletResponse response,
			@PathVariable Integer attendanceId) {
		AttendanceBo attendance = attendanceService.queryAttendanceByAttendanceId(attendanceId);
		if(null == attendance){
			throw new NotFoundException("考勤");
		}
		return attendance;
	}	
	
	@ApiOperation(value = "查询分页 根据考勤属性分页查询考勤信息列表", notes = "根据考勤属性分页查询考勤信息列表")
	@RequestMapping(value = "/attendances", method = RequestMethod.GET)
	public Object queryAttendancePage(HttpServletResponse response,
			@RequestParam(value = "pageNo", required = false) Integer pagenum,
			@RequestParam(value = "pageSize", required = false) Integer pagesize,
			@RequestParam(value = "sort", required = false) String sort, Attendance attendance) {	
		if(attendance==null) {
			attendance = new Attendance();
		}
		if(null==attendance.getStatus()) {
			attendance.setStatus(0);
		}
		if(sort==null) {
			sort = "{\\\"attendanceName\\\":\\\"asc\\\"}";
		}
		return attendanceService.queryAttendanceForPage(pagenum, pagesize, sort, attendance);
	}	
	
	
	@ApiOperation(value = "新增 添加考勤时段 一个考勤方案至多三个时段信息", notes = "添加考勤时段 一个考勤方案至多三个时段信息")
	@RequestMapping(value = "/attendance/{attendanceId:.+}/time", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
	public Object addAttendanceTime(HttpServletResponse response,
			@PathVariable Integer attendanceId,
			@ApiParam(value = "attendanceTime") @RequestBody AttendanceTime attendanceTime) throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException {
		Attendance tempAttendance = attendanceService.queryAttendanceByAttendanceId(attendanceId);		
		if(null == tempAttendance){
			throw new NotFoundException("考勤规则");
		}
		List<Map<String, String>> errors = beanValidator.validateClassAuto(attendanceTime, true);
		Map<String, Object> query = new HashMap<String, Object>();
		query.put("attendanceId", attendanceId);
		List<AttendanceTime> atList = attendanceTimeService.queryAttendanceTimeByProperty(query);
		if(atList.size()>=MAX_TIME_PER_ATT.intValue()){
			Map<String, String> e = new HashMap<String, String>();
			e.put("field", "attTime");
			e.put("message", "每考勤规则限定最多"+MAX_TIME_PER_ATT+"个时间段。");
			errors.add(e);
		}
		if(!errors.isEmpty()){
			throw new ParameterException(errors);
		}
		attendanceTime.setId(null);
		attendanceTime.setAttendanceId(attendanceId);
		attendanceTimeService.addAttendanceTime(attendanceTime);
		return attendanceTime;
	}
	
	
	@ApiOperation(value = "更新 根据考勤时段信息", notes = "根据考勤时段信息")
	@RequestMapping(value = "/attendance/{attendanceId:.+}/time/{id:.+}", method = RequestMethod.PUT)
	public Object modifyAttendanceTimeById(HttpServletResponse response,
			@PathVariable Integer attendanceId,
			@PathVariable Integer id,
			@ApiParam(value = "attendanceTime", required = true) @RequestBody AttendanceTime attendanceTime
			) throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException {		
		AttendanceTime tempAttendanceTime = attendanceTimeService.queryAttendanceTimeById(id);		
		if(null == tempAttendanceTime){
			throw new NotFoundException("考勤时段");
		}
		if(tempAttendanceTime.getAttendanceId().intValue()!=attendanceId.intValue()){
			throw new ParameterException("attendanceId", "时段所属考勤规则不匹配。");
		}
		List<Map<String, String>> errors = beanValidator.validateClassAuto(attendanceTime, false);
		if(!errors.isEmpty()){
			throw new ParameterException(errors);
		}
		tempAttendanceTime.setAttendanceId(null);
		tempAttendanceTime.setName(attendanceTime.getName());
		tempAttendanceTime.setOutTime(attendanceTime.getOutTime());
		tempAttendanceTime.setSignTime(attendanceTime.getSignTime());
		return attendanceTimeService.modifyAttendanceTime(tempAttendanceTime);
	}

	@ApiOperation(value = "删除 根据考勤时段标识删除时段信息", notes = "根据考勤时段标识删除时段信息")
	@RequestMapping(value = "/attendance/{attendanceId:.+}/time/{id:.+}", method = RequestMethod.DELETE)
	public Object dropAttendanceTimeById(HttpServletResponse response, 
			@PathVariable Integer attendanceId,
			@PathVariable Integer id) {		
		AttendanceTime attendanceTime = attendanceTimeService.queryAttendanceTimeById(id);
		if(null == attendanceTime){
			throw new NotFoundException("考勤时段");
		}
		if(attendanceTime.getAttendanceId().intValue()!=attendanceId.intValue()){
			throw new ParameterException("attendanceId", "时段所属考勤规则不匹配。");
		}
		Map<String, Object> query = new HashMap<String, Object>();
		query.put("attendanceId", attendanceId);
		List<AttendanceTime> list = attendanceTimeService.queryAttendanceTimeByProperty(query);
		if(list==null || list.size()==1){
			throw new ParameterException("id", "每考勤规则必须保留一个时间段。");
		}
		return attendanceTimeService.dropAttendanceTimeById(id);
	}
	
	@ApiOperation(value = "查询 根据考勤标识查询时段信息", notes = "根据考勤标识查询时段信息")
	@RequestMapping(value = "/attendance/{attendanceId:.+}/times", method = RequestMethod.GET)
	public Object queryAttendanceTimeById(HttpServletResponse response,
			@PathVariable Integer attendanceId) {
		Attendance tempAttendance = attendanceService.queryAttendanceByAttendanceId(attendanceId);		
		if(null == tempAttendance){
			throw new NotFoundException("考勤规则");
		}
		Map<String, Object> query = new HashMap<String, Object>();
		query.put("attendanceId", attendanceId);
		return attendanceTimeService.queryAttendanceTimeByProperty(query);
	}
	
}
