package com.laundry.LaundryManagement.model;
import org.hibernate.annotations.GenericGenerator;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "files")
@Data
public class ExcelFile {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
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