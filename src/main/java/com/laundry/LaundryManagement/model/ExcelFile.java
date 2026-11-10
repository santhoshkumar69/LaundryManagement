package com.laundry.LaundryManagement.model;

import org.hibernate.annotations.Filter;
import jakarta.persistence.EntityListeners;
import com.laundry.LaundryManagement.auth.ScopeFilters;
import com.laundry.LaundryManagement.auth.ScopeStampingListener;
import org.hibernate.annotations.UuidGenerator;

import lombok.Data;

import jakarta.persistence.*;

@Entity
@Table(name = "files")
@Data
@Filter(name = ScopeFilters.TENANT)
@EntityListeners(ScopeStampingListener.class)
public class ExcelFile {

    @Id
    @UuidGenerator
    private String id;

    private String fileName;

    private String fileType;

    @Lob
    private byte[] data;
    
	@Column(name = "TENANT_ID")
	private int tenantId;

    public ExcelFile() {

    }

    public ExcelFile(String fileName, String fileType, byte[] data) {
        this.fileName = fileName;
        this.fileType = fileType;
        this.data = data;
    }

    // 
}