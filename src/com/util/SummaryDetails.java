package com.util;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class SummaryDetails {
	private double accountBalance, depositsBalance, creditLimit;
	private String AccountType;
	
	public SummaryDetails() {
		super();
	}

	public double getAccountBalance() {
		return accountBalance;
	}

	public void setAccountBalance(double accountBalance) {
		this.accountBalance = accountBalance;
	}

	public double getDepositsBalance() {
		return depositsBalance;
	}

	public void setDepositsBalance(double depositsBalance) {
		this.depositsBalance = depositsBalance;
	}

	public String getAccountType() {
		return AccountType;
	}

	public void setAccountType(String accountType) {
		AccountType = accountType;
	}

	public double getCreditLimit() {
		return creditLimit;
	}

	public void setCreditLimit(double creditLimit) {
		this.creditLimit = creditLimit;
	}

	@Override
	public String toString() {
		return "SummaryDetails [accountBalance=" + accountBalance
				+ ", depositsBalance=" + depositsBalance + ", AccountType="
				+ AccountType + ", creditLimit=" + creditLimit + "]";
	}
	
	

}
