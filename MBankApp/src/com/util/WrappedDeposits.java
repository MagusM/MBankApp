package com.util;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import wrapClasses.DepositBeanWrapper;
import beans.DepositBean;

@XmlRootElement
public class WrappedDeposits {
	private List<DepositBeanWrapper> wrappedDeposits;
	
	public WrappedDeposits() {
		super();
	}

	public WrappedDeposits(List<DepositBeanWrapper> wrappedDeposits) {
		super();
		this.wrappedDeposits = wrappedDeposits;;
	}
	
	public List<DepositBeanWrapper> getWrappedDeposits() {
		return wrappedDeposits;
	}
	
	public void setWrappedDepositsFromDepositsBeans(List<DepositBean> deposits) {
		this.wrappedDeposits = new DepositBeanWrapper().getDepositsAsList(deposits);
	}

	public void setWrappedDeposits(List<DepositBeanWrapper> wrappedDeposits) {
		this.wrappedDeposits = wrappedDeposits;
	}
	

}
