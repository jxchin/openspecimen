
package com.krishagni.catissueplus.core.administrative.events;

import java.util.List;

import com.krishagni.catissueplus.core.common.events.AbstractListCriteria;

public class ListPvCriteria extends AbstractListCriteria<ListPvCriteria> {
	private static final String ARCHIVED = "Archived";

	private String attribute;

	private List<String> values;

	private String parentValue;
	
	private boolean includeParentValue;
	
	private String parentAttribute;
	
	private boolean includeOnlyLeafValue;

	private boolean includeOnlyRootValue;

	private String activityStatus;
	
	public String attribute() {
		return attribute;
	}

	public ListPvCriteria attribute(String attribute) {
		this.attribute = attribute;
		return self();
	}

	public List<String> values() {
		return values;
	}

	public ListPvCriteria values(List<String> values) {
		this.values = values;
		return self();
	}

	public String parentValue() {
		return parentValue;
	}

	public ListPvCriteria parentValue(String parentValue) {
		this.parentValue = parentValue;
		return self();
	}
	
	public boolean includeParentValue() {
		return includeParentValue;
	}
	
	public ListPvCriteria includeParentValue(boolean includeParentValue) {
		this.includeParentValue = includeParentValue;
		return self();
	}
	
	public String parentAttribute() {
		return parentAttribute;
	}
	
	public ListPvCriteria parentAttribute(String parentAttribute) {
		this.parentAttribute = parentAttribute;
		return self();
	}
	
	public boolean includeOnlyLeafValue() {
		return includeOnlyLeafValue;
	}
	
	public ListPvCriteria includeOnlyLeafValue(boolean includeOnlyLeafValue) {
		this.includeOnlyLeafValue = includeOnlyLeafValue;
		return self();
	}

	public boolean includeOnlyRootValue() {
		return includeOnlyRootValue;
	}

	public ListPvCriteria includeOnlyRootValue(boolean includeOnlyRootValue) {
		this.includeOnlyRootValue = includeOnlyRootValue;
		return self();
	}

	public String activityStatus() {
		return activityStatus;
	}

	public ListPvCriteria activityStatus(String activityStatus) {
		if (ARCHIVED.equalsIgnoreCase(activityStatus)) {
			activityStatus = "Closed";
		}

		this.activityStatus = activityStatus;
		return self();
	}

	@Override
	public ListPvCriteria self() {
		return this;
	}
}
