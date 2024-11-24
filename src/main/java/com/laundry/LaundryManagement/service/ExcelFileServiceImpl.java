package com.laundry.LaundryManagement.service;






import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.laundry.LaundryManagement.repository.ExcelFileRepository;

@Service
public class ExcelFileServiceImpl {


    @Autowired
    private ExcelFileRepository excelFileRepository;

//    public ExcelFile storeFile(MultipartFile file) {
//        // Normalize file name
//        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
//
//        try {
//            // Check if the file's name contains invalid characters
//            if(fileName.contains("..")) {
//                throw new FileStorageException("Sorry! Filename contains invalid path sequence " + fileName);
//            }
//
//            DBFile dbFile = new DBFile(fileName, file.getContentType(), file.getBytes());
//
//            return dbFileRepository.save(dbFile);
//        } catch (IOException ex) {
//            throw new FileStorageException("Could not store file " + fileName + ". Please try again!", ex);
//        }
//    }

//    public ExcelFile getFile(String fileId) {
//        return excelFileRepository.findById(fileId)
//                .orElseThrow(() -> new MyFileNotFoundException("File not found with id " + fileId));
//    }
}