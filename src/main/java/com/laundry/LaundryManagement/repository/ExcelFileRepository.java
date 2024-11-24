package com.laundry.LaundryManagement.repository;

import com.laundry.LaundryManagement.model.ExcelFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExcelFileRepository extends JpaRepository<ExcelFile, String> {

}




