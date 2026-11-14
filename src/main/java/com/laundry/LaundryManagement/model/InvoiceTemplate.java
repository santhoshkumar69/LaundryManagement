package com.laundry.LaundryManagement.model;

import org.hibernate.annotations.Filter;
import jakarta.persistence.EntityListeners;
import com.laundry.LaundryManagement.auth.ScopeFilters;
import com.laundry.LaundryManagement.auth.ScopeStampingListener;

import java.io.Serializable;
import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "TB_LA_INVOICE_TEMPLATE")
@Filter(name = ScopeFilters.TENANT)
@Filter(name = ScopeFilters.OUTLET)
@EntityListeners(ScopeStampingListener.class)
public class InvoiceTemplate implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "TEMPLATE_ID")
	private Integer templateId;

	@Column(name = "LOCATION_ID")
	private String locationID;

	@Column(name = "TEMPLATE_NAME", length = 100, nullable = false)
	private String templateName;

	@Column(name = "PAPER_WIDTH_MM")
	private Integer paperWidthMm;

	@Column(name = "FONT_FAMILY", length = 50)
	private String fontFamily;

	@Column(name = "FONT_SIZE")
	private Double fontSize;

	@Column(name = "LAYOUT_JSON", columnDefinition = "LONGTEXT")
	private String layoutJson;

	@Column(name = "LOGO_DATA", columnDefinition = "MEDIUMTEXT")
	private String logoData;

	@Column(name = "IS_ACTIVE")
	private Integer isActive;

	@Column(name = "ACTIVE_STATUS")
	private Integer activeStatus;

	@Column(name = "TENANT_ID")
	private Integer tenantId;

	@Column(name = "CREATED_BY")
	private String createdBy;

	@Column(name = "CREATED_DATE")
	private Date createdDate;

	@Column(name = "UPDATED_BY")
	private String updatedBy;

	@Column(name = "UPDATED_DATE")
	private Date updatedDate;

	public Integer getTemplateId() {
		return templateId;
	}

	/** ID of the outlet that owns the template. */
	public String getLocationID() {
		return locationID;
	}

	public void setTemplateId(Integer templateId) {
		this.templateId = templateId;
	}

	public String getTemplateName() {
		return templateName;
	}

	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}

	public Integer getPaperWidthMm() {
		return paperWidthMm;
	}

	public void setPaperWidthMm(Integer paperWidthMm) {
		this.paperWidthMm = paperWidthMm;
	}

	public String getFontFamily() {
		return fontFamily;
	}

	public void setFontFamily(String fontFamily) {
		this.fontFamily = fontFamily;
	}

	public Double getFontSize() {
		return fontSize;
	}

	public void setFontSize(Double fontSize) {
		this.fontSize = fontSize;
	}

	public String getLayoutJson() {
		return layoutJson;
	}

	public void setLayoutJson(String layoutJson) {
		this.layoutJson = layoutJson;
	}

	public String getLogoData() {
		return logoData;
	}

	public void setLogoData(String logoData) {
		this.logoData = logoData;
	}

	public Integer getIsActive() {
		return isActive;
	}

	public void setIsActive(Integer isActive) {
		this.isActive = isActive;
	}

	public Integer getActiveStatus() {
		return activeStatus;
	}

	public void setActiveStatus(Integer activeStatus) {
		this.activeStatus = activeStatus;
	}

	public Integer getTenantId() {
		return tenantId;
	}

	public void setTenantId(Integer tenantId) {
		this.tenantId = tenantId;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public String getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}

	public Date getUpdatedDate() {
		return updatedDate;
	}

	public void setUpdatedDate(Date updatedDate) {
		this.updatedDate = updatedDate;
	}
}
