package com.jing.core.model.entity;

import com.jing.utils.BaseEntity;

/**
 * 会员金额流水 实体类
 * @author codeing gen
 */
public class MemberMoneyRecord extends BaseEntity{
	private static final long serialVersionUID = 1L;

	private Integer recordId;   // 记录标识
	private String memberId;   // 会员标识
	private Integer rtype;   // 收支
	private java.math.BigDecimal money;   // 金额
	private java.math.BigDecimal balance;   // 余额
	private String remark;   // 备注













