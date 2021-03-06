package com.jing.attendance.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jing.attendance.controller.vo.AttendanceBo;
import com.jing.attendance.model.dao.AttendanceDetailMapper;
import com.jing.attendance.model.dao.AttendanceEmployeeMapper;
import com.jing.attendance.model.dao.AttendanceMapper;
import com.jing.attendance.model.dao.AttendanceTimeMapper;
import com.jing.attendance.model.entity.Attendance;
import com.jing.attendance.model.entity.AttendanceTime;
import com.jing.attendance.service.AttendanceService;
import com.jing.utils.Constant;
import com.jing.utils.paginator.domain.PageBounds;
import com.jing.utils.paginator.domain.PageList;
import com.jing.utils.paginator.domain.PageService;

/**
 * @ClassName: Attendance
 * @Description: 门店考勤服务实现类
 * @author: Jinlong He
 * @email: mailto:jinlong_he@126.com
 * @date: 2018年01月11日 15时03分
 */
@Service("attendanceService")
@Transactional(readOnly=true)
public class  AttendanceServiceImpl implements AttendanceService {	
//	private static final Logger logger = LoggerFactory.getLogger(AttendanceServiceImpl.class);
	
	@Autowired
    private AttendanceMapper attendanceMapper;	

	@Autowired
	private AttendanceTimeMapper attendanceTimeMapper;
	
	@Autowired
	private AttendanceDetailMapper attendanceDetailMapper;	
	
	@Autowired
	private AttendanceEmployeeMapper employeeAttendanceMapper;
    
	@Autowired
	private PageService pageService; // 分页器
	
	
	/**
	 * @Title: addAttendance
	 * @Description:添加门店考勤-包含时段
	 * @param attendance 实体
	 * @return Integer
	 */
	@Override
	@Transactional(readOnly = false)
	public Attendance addAttendance(AttendanceBo attendance){
		int ret = attendanceMapper.addAttendance(attendance);
		if(ret>0){
			if(attendance.getAttTime()!=null && attendance.getAttTime().size()>0){
				for(AttendanceTime at : attendance.getAttTime()){					
					at.setAttendanceId(attendance.getAttendanceId()); //回填标识
					attendanceTimeMapper.addAttendanceTime(at);
				}
			}else {
				//保证每个规则有个时间段
				if(attendance.getAttTime()==null || attendance.getAttTime().size()==0){					
					AttendanceTime at = new AttendanceTime();
					at.setName("缺省");
					at.setSignTime("08:30:00");
					at.setOutTime("17:30:00");
					at.setAttendanceId(attendance.getAttendanceId());
					attendanceTimeMapper.addAttendanceTime(at);
					List<AttendanceTime> atlist = new ArrayList<AttendanceTime>();
					atlist.add(at);
					attendance.setAttTime(atlist);
				}
			}
			return attendance;
		}
		return null;
	}
	
	/**
	 * @Title modifyAttendance
	 * @Description:修改门店考勤
	 * @param attendance 实体
	 * @return Integer
	 */
	@Override
	@Transactional(readOnly = false)
	public Integer modifyAttendance(AttendanceBo attendance){
		if(attendance.getAttTime()!=null && attendance.getAttTime().size()>0){
			for(AttendanceTime at : attendance.getAttTime()){
				at.setAttendanceId(attendance.getAttendanceId()); //回填考勤标识
				if(at.getId()!=null) {
					attendanceTimeMapper.modifyAttendanceTime(at);
				}else {					
					attendanceTimeMapper.addAttendanceTime(at);
				}				
			}
		}
		return attendanceMapper.modifyAttendance(attendance);
	}
	
	/**
	 * @Title: dropAttendanceByAttendanceId
	 * @Description:删除门店考勤
	 * @param attendanceId 实体标识
	 * @return Integer
	 */
	@Override
	@Transactional(readOnly = false)
	public Integer dropAttendanceByAttendanceId(Integer attendanceId){
		attendanceDetailMapper.dropAttendanceDetailByAttendanceId(attendanceId); //清空考勤详情配置
		attendanceTimeMapper.dropAttendanceTimeByAttendanceId(attendanceId);//清空考勤时段
		employeeAttendanceMapper.dropAttendanceEmployeeByAttendanceId(attendanceId);//清空员工考勤关系		
		return attendanceMapper.dropAttendanceByAttendanceId(attendanceId);//删除考勤
	}
	
	/**
	 * @Title: queryAttendanceByAttendanceId
	 * @Description:根据实体标识查询考勤
	 * @param attendanceId 实体标识
	 * @return Attendance
	 */
	@Override
	public AttendanceBo queryAttendanceByAttendanceId(Integer attendanceId){
		Attendance a = attendanceMapper.queryAttendanceByAttendanceId(attendanceId);
		if(a==null){
			return null;
		}
		AttendanceBo ab = new AttendanceBo(a);
		Map<String, Object> query = new HashMap<String, Object>();
		query.put("attendanceId", attendanceId);
		ab.setAttTime(attendanceTimeMapper.queryAttendanceTimeByProperty(query));
		return ab;
	}
	 
	/**
	 * @Title: queryAttendanceForPage
	 * @Description: 根据门店考勤属性与分页信息分页查询门店考勤信息
	 * @param pagenum 页 
	 * @param pagesize 页大小 
	 * @param sort 排序
	 * @param attendance 实体
	 * @return List<Attendance>
	 */
	@Override
	public Map<String, Object> queryAttendanceForPage(Integer pagenum, Integer pagesize, String sort, Attendance attendance){
		HashMap<String, Object> returnMap = new HashMap<String, Object>();
		PageBounds pageBounds = pageService.getPageBounds(pagenum, pagesize, null, true, false);
		pageBounds.setOrdersByJson(sort, Attendance.class);
		List<Attendance> entityList = attendanceMapper.queryAttendanceForPage(pageBounds, attendance);		
		//if (!entityList.isEmpty()) {
			PageList<Attendance> pagelist = (PageList<Attendance>) entityList;
			returnMap.put(Constant.PAGELIST, entityList);
			returnMap.put(Constant.PAGINATOR, pagelist.getPaginator());
		//}
		return returnMap;
	}
	 
	/**
	 * @Title: queryAttendanceByProperty
	 * @Description:根据属性查询门店考勤
	 * @return List<Attendance>
	 */
	@Override
	public List<Attendance> queryAttendanceByProperty(Map<String, Object> map){
		List<Attendance> ret = attendanceMapper.queryAttendanceByProperty(map);		
		return ret;
	}



}
