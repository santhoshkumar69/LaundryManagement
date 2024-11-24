/**
 * 
 */
package com.laundry.LaundryManagement.model;

import java.io.Serializable;


/**
 * @author Administrator
 *
 */

public class GlobalInfo implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String userName;
	private String userRole;
	private Long userId;
	private String locationID;
	private int status;
	
	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public GlobalInfo(Builder builder) {
		this.userId = builder.userId;
		this.userName = builder.userName;
		this.userRole = builder.userRole;
		this.locationID = builder.locationID;
	}
	
	public String getUserName() {
		return userName;
	}
	public String getUserRole() {
		return userRole;
	}
	public Long getUserId() {
		return userId;
	}
	public String getLocationID() {
		return locationID;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public void setUserRole(String userRole) {
		this.userRole = userRole;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public void setLocationID(String locationID) {
		this.locationID = locationID;
	}
	
	public static class Builder{
		private String userName = "default";
		private String userRole = "defaultRole";
		private Long userId = new Long(1233);
		private String locationID = "QL1";
	
		public Builder setUserName(String userName) {
			this.userName = userName;
			return this;
		}
		
		public Builder setUserRole(String userRole) {
			this.userRole = userRole;
			return this;
		}
		
		public Builder setUserId(Long userId) {
			this.userId = userId;
			return this;
		}
		
		public Builder setLocationID(String locationID) {
			this.locationID = locationID;
			return this;
		}
		
		public GlobalInfo build() {
			return new GlobalInfo(this);
		}
	}
	
}
