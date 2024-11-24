/**
 * 
 */
package com.laundry.LaundryManagement.model;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

/**
 * @author pandyarajan
 *
 * 11-Mar-2019
 */
@Entity
@Table(name = "TB_ESTIMATION_DETAILS")
@Data
public class EstimationDetails implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "EST_ID")
	private int estID;
	
	@Column(name = "CUS_ID")
	private int cusID;
	
	@Column(name = "BILLING_STATUS")
	private int billStatus;
	
	@Column(name = "SPCL_COMMENTS")
	private String comments;
	
	@Column(name = "LAUNDRY_STS")
	private String laundryReq;
	
	@Column(name = "IRON_STS")
	private String ironReq;
	
	@Column(name = "DELIVERY_DATE")
	private Date deliveryDate;
	
	@Column(name = "CREATED_BY")
	private String createdBy;

	@Column(name = "CREATED_DATE")
	private Date createdDate;

	@Column(name = "UPDATED_BY")
	private String updatedBy;

	@Column(name = "UPDATED_DATE")
	private Date updatedDate;

	@Column(name = "LOCATION_ID")
	private String locationID;

	@Column(name = "USER_ID")
	private String userID;
	
	@Column(name = "TENANT_ID")
	private int tenantId;

	/**
	 * @return the estID
	 */
	public int getEstID() {
		return estID;
	}

	/**
	 * @param estID the estID to set
	 */
	public void setEstID(int estID) {
		this.estID = estID;
	}

	/**
	 * @return the cusID
	 */
	public int getCusID() {
		return cusID;
	}

	/**
	 * @param cusID the cusID to set
	 */
	public void setCusID(int cusID) {
		this.cusID = cusID;
	}

	/**
	 * @return the billStatus
	 */
	public int getBillStatus() {
		return billStatus;
	}

	/**
	 * @param billStatus the billStatus to set
	 */
	public void setBillStatus(int billStatus) {
		this.billStatus = billStatus;
	}

	/**
	 * @return the createdBy
	 */
	public String getCreatedBy() {
		return createdBy;
	}

	/**
	 * @param createdBy the createdBy to set
	 */
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	/**
	 * @return the createdDate
	 */
	public Date getCreatedDate() {
		return createdDate;
	}

	/**
	 * @param createdDate the createdDate to set
	 */
	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	/**
	 * @return the updatedBy
	 */
	public String getUpdatedBy() {
		return updatedBy;
	}

	/**
	 * @param updatedBy the updatedBy to set
	 */
	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}

	/**
	 * @return the updatedDate
	 */
	public Date getUpdatedDate() {
		return updatedDate;
	}

	/**
	 * @param updatedDate the updatedDate to set
	 */
	public void setUpdatedDate(Date updatedDate) {
		this.updatedDate = updatedDate;
	}

	/**
	 * @return the locationID
	 */
	public String getLocationID() {
		return locationID;
	}

	/**
	 * @param locationID the locationID to set
	 */
	public void setLocationID(String locationID) {
		this.locationID = locationID;
	}

	/**
	 * @return the userID
	 */
	public String getUserID() {
		return userID;
	}

	/**
	 * @param userID the userID to set
	 */
	public void setUserID(String userID) {
		this.userID = userID;
	}

	/**
	 * @return the laundryReq
	 */
	public String getLaundryReq() {
		return laundryReq;
	}

	/**
	 * @param laundryReq the laundryReq to set
	 */
	public void setLaundryReq(String laundryReq) {
		this.laundryReq = laundryReq;
	}

	/**
	 * @return the ironReq
	 */
	public String getIronReq() {
		return ironReq;
	}

	/**
	 * @param ironReq the ironReq to set
	 */
	public void setIronReq(String ironReq) {
		this.ironReq = ironReq;
	}

	/**
	 * @return the comments
	 */
	public String getComments() {
		return comments;
	}

	/**
	 * @param comments the comments to set
	 */
	public void setComments(String comments) {
		this.comments = comments;
	}

	/**
	 * @return the deliveryDate
	 */
	public Date getDeliveryDate() {
		return deliveryDate;
	}

	/**
	 * @param deliveryDate the deliveryDate to set
	 */
	public void setDeliveryDate(Date deliveryDate) {
		this.deliveryDate = deliveryDate;
	}

	public EstimationDetails get() {
		// TODO Auto-generated method stub
		return null;
	}
	
}



