package com.laundry.LaundryManagement.model;

public class ExcelUploadFileResponse {


    private String fileName;
    private String fileDownloadUri;
    private String fileType;
    private long size;

    public ExcelUploadFileResponse(String fileName, String fileDownloadUri, String fileType, long size) {
        this.fileName = fileName;
        this.fileDownloadUri = fileDownloadUri;
        this.fileType = fileType;
        this.size = size;
    }

	// Getters and Setters (Omitted for brevity)
}