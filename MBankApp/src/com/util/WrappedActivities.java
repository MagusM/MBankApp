package com.util;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import wrapClasses.ActivitiesBeanWrapper;
import beans.ActivityBean;

@XmlRootElement
public class WrappedActivities {
	
	private List<ActivitiesBeanWrapper> wrappedActvities;
	
	public WrappedActivities() {
		super();
	}

	public List<ActivitiesBeanWrapper> getActvities() {
		return wrappedActvities;
	}

	public void setActvities(List<ActivitiesBeanWrapper> activities) {
		this.wrappedActvities = activities;
	}
	
	public void setWrappedActvitiesFromActvityBeans(List<ActivityBean> activities) {
		wrappedActvities = new ArrayList<ActivitiesBeanWrapper>();
		for (ActivityBean activityBean : activities) {
			this.wrappedActvities.add(new ActivitiesBeanWrapper(activityBean));
		}
	}

	@Override
	public String toString() {
		return "WrappedActivities [wrappedActvities=" + wrappedActvities + "]";
	}
	
	

}
