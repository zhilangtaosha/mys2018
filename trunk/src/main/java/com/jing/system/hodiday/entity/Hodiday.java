package com.jing.system.hodiday.entity;

import java.util.Date;

import com.jing.utils.BaseEntity;
import com.jing.utils.DateUtils;

/**
 * 节假日 实体类
 * @author codeing gen
 */
public class Hodiday extends BaseEntity{
	private static final long serialVersionUID = 1L;

	private String hid;   // 主键
	private String htype;   // 字典：加班、休假
	private Date hdate;   // 日期
	private Integer hnumber;   // 数字日期





		if(hnumber==null && hdate !=null){
			String d = DateUtils.getDateFormat(hdate,"yyyyMMdd");
			return Integer.parseInt(d);
		}






}