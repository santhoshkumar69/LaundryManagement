package com.laundry.LaundryManagement.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpSession;

import com.laundry.LaundryManagement.common.CommonUtills;
import com.laundry.LaundryManagement.constant.AppConstants;
import com.laundry.LaundryManagement.constant.AppStatus;
import com.laundry.LaundryManagement.constant.AppStatusCode;
import com.laundry.LaundryManagement.dto.ReturnResponse;
import com.laundry.LaundryManagement.model.GlobalInfo;
import com.laundry.LaundryManagement.model.WashingType;
import com.laundry.LaundryManagement.service.WashingTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.laundry.LaundryManagement.beans.WashingTypeResponse;

@CrossOrigin(origins = "http://localhost:4200",maxAge = 3600)
@RestController
@RequestMapping("WashingType")
public class WashingTypeController {

	@Autowired
    WashingTypeService washingTypeService;
	
	@PostMapping("/createWashingType")
	public ResponseEntity<Object> createWashingType(@RequestBody WashingType washingType, HttpSession httpSession)
	{
		CommonUtills.addDefaultCreateInfos(washingType, washingType.getClass(), (GlobalInfo)httpSession.getAttribute(AppConstants.SESSION_OBJ));
		WashingType aWashingType = null;
         
         if(washingType.getProcessTypeName() == null)
         {
        	 return ResponseEntity.status(500).body(new ReturnResponse(AppStatus.WASHNG_TYPE_ENTER, AppStatusCode.INTERNAL_ERROR));
         }
         if (washingType.getProcessTypeName() != null) {
 			String name = washingType.getProcessTypeName();
 			aWashingType = washingTypeService.findByProcessTypeName(name);
 		}
         
         if (aWashingType != null) {
 			return ResponseEntity.status(500)
 					.body(new ReturnResponse(AppStatus.ID_ALREDY_EXIST, AppStatusCode.INTERNAL_ERROR));
 		}
         else
         {
        	 washingType.setActiveStatus(1);
        	 boolean isCreated=washingTypeService.createWashingType(washingType);
        	 if(isCreated)
        	 {
        		 return ResponseEntity.ok(washingTypeService.findAllWashingTYpe());
        	 }
        	 return	ResponseEntity.status(500).body(new ReturnResponse(AppStatus.UNABLE_TO_CREATE,AppStatusCode.INTERNAL_ERROR));
         }
				
	}
	
	@GetMapping("/getWashingType")
	public ResponseEntity<Object>  getAllWashingType(HttpSession httpSession)
	{
		return ResponseEntity.ok().body(washingTypeService.findAllWashingTYpe());
		
	}

	@GetMapping("/getDeActiveWashingType")
	public ResponseEntity<Object> getDeactiveWashingType(HttpSession httpSession)
	{
		return ResponseEntity.ok().body(washingTypeService.findDeActiveWashingType());
	}
	
	 @PostMapping("/deActiveWashingType")
	  public ResponseEntity<Object> deActiveWashingType(@RequestParam Integer[] ids, HttpSession httpSession)
	  {
		 List<WashingTypeResponse> theWashingTypeResponse = new ArrayList<WashingTypeResponse>();
		 try {
			 //Integer id = washingType.getId();
			 for(int id : ids) {
				 WashingType aWashingType = washingTypeService.findByRow(id);
				  if(aWashingType== null)  {
					  WashingTypeResponse washingTypeResponse = new WashingTypeResponse();
					  washingTypeResponse.setErrors(AppStatus.ID_NOT_EXIST);
					  washingTypeResponse.setId(id);
					  theWashingTypeResponse.add(washingTypeResponse);
				  } else if(aWashingType.getActiveStatus() == 0) {
					  WashingTypeResponse washingTypeResponse = new WashingTypeResponse();
					  washingTypeResponse.setErrors(AppStatus.ID_ALREADY_DEACTIVATED);
					  washingTypeResponse.setStatus(AppConstants.DEACTIVATED);
					  washingTypeResponse.setId(id);
					  washingTypeResponse.setActiveStatus(0);
					  washingTypeResponse.setWashingType(aWashingType.getProcessTypeName());
					  theWashingTypeResponse.add(washingTypeResponse);
				  } else {
					  WashingTypeResponse washingTypeResponse = new WashingTypeResponse();
					  CommonUtills.addDefaultUpdateInfos(aWashingType, aWashingType.getClass(), (GlobalInfo)httpSession.getAttribute(AppConstants.SESSION_OBJ));
					  aWashingType.setActiveStatus(0);
					  washingTypeService.createWashingType(aWashingType);
					  
					  washingTypeResponse.setErrors(AppStatus.DEACTIVATED_SUCCESSFULLY);
					  washingTypeResponse.setStatus(AppConstants.DEACTIVATED);
					  washingTypeResponse.setId(id);
					  washingTypeResponse.setActiveStatus(0);
					  washingTypeResponse.setWashingType(aWashingType.getProcessTypeName());
					  theWashingTypeResponse.add(washingTypeResponse);
				  } 
			 }
			  return ResponseEntity.ok().body(theWashingTypeResponse);
		 } catch(Exception e) {
			 return ResponseEntity.status(500).body(new ReturnResponse(AppStatus.SOME_ERROR_OCCURRED,AppStatusCode.INTERNAL_ERROR));
		 }
	  }
	 
	 
	 @PostMapping("/activeWashingType")
	  public ResponseEntity<Object> activeWashingType(@RequestParam Integer[] ids, HttpSession httpSession) {
		 
		 List<WashingTypeResponse> theWashingTypeResponse = new ArrayList<WashingTypeResponse>();
		 try {
			 for(int id : ids) {
				 WashingType aWashingType = washingTypeService.findByRow(id);
				 if(aWashingType == null)  {
					  WashingTypeResponse washingTypeResponse = new WashingTypeResponse();
					  washingTypeResponse.setErrors(AppStatus.ID_NOT_EXIST);
					  washingTypeResponse.setId(id);
					  theWashingTypeResponse.add(washingTypeResponse);
				 } else if(aWashingType.getActiveStatus() == 1) {
					 WashingTypeResponse washingTypeResponse = new WashingTypeResponse();
					  washingTypeResponse.setErrors(AppStatus.ID_ALREADY_ACTIVATED);
					  washingTypeResponse.setStatus(AppConstants.ACTIVATED);
					  washingTypeResponse.setId(id);
					  washingTypeResponse.setActiveStatus(1);
					  washingTypeResponse.setWashingType(aWashingType.getProcessTypeName());
					  theWashingTypeResponse.add(washingTypeResponse);
				 } else {
					 WashingTypeResponse washingTypeResponse = new WashingTypeResponse();
					  CommonUtills.addDefaultUpdateInfos(aWashingType, aWashingType.getClass(), (GlobalInfo)httpSession.getAttribute(AppConstants.SESSION_OBJ));
					  aWashingType.setActiveStatus(1);
					  washingTypeService.createWashingType(aWashingType);
					  
					  washingTypeResponse.setErrors(AppStatus.ACTIVATED_SUCCESSFULLY);
					  washingTypeResponse.setStatus(AppConstants.ACTIVATED);
					  washingTypeResponse.setId(id);
					  washingTypeResponse.setActiveStatus(1);
					  washingTypeResponse.setWashingType(aWashingType.getProcessTypeName());
					  theWashingTypeResponse.add(washingTypeResponse);
				 }
			 }
			 return ResponseEntity.ok().body(theWashingTypeResponse);
		 } catch(Exception e) {
			 return ResponseEntity.status(500).body(new ReturnResponse(AppStatus.SOME_ERROR_OCCURRED,AppStatusCode.INTERNAL_ERROR)); 
		 }
		 
	  }
	 
		@PostMapping("/updateWashingType")
		public ResponseEntity<Object> updateWashingType(@RequestBody WashingType washingTypeFromUi,
				HttpSession httpSession) {
			boolean washingTypeIdChanged = false;
			Integer id = washingTypeFromUi.getProcessTypeId();
			WashingType washingtypes = null;
			Optional<WashingType> getwashingTypeFromDb = washingTypeService.findIdFromDb(id);
//			if (washingTypeFromUi == null) {
//				return ResponseEntity.notFound().build();
//			} else {
				if (washingTypeFromUi.getProcessTypeName() != null) {
					String washingTypeName = washingTypeFromUi.getProcessTypeName();
					washingtypes = washingTypeService.findByProcessTypeName(washingTypeName);
				} if (washingTypeIdChanged || washingtypes != null) {
					return ResponseEntity.status(500).body(
							new ReturnResponse(AppStatus.WASHINGTYPE_NAME_ALREDY_EXIST, AppStatusCode.INTERNAL_ERROR));
				}
	
				WashingType setWashingType = getwashingTypeFromDb.get();
	
				setWashingType.setProcessTypeName(washingTypeFromUi.getProcessTypeName());
				setWashingType.setPrice(washingTypeFromUi.getPrice());
				setWashingType.setUpdatedBy(setWashingType.getUpdatedBy());
				setWashingType.setUpdatedDate(setWashingType.getUpdatedDate());
	
				CommonUtills.addDefaultUpdateInfos(setWashingType, setWashingType.getClass(),
						(GlobalInfo) httpSession.getAttribute(AppConstants.SESSION_OBJ));
				boolean isCreated = washingTypeService.createWashingType(setWashingType);
				if (isCreated) {
					return ResponseEntity.ok().body(washingTypeService.findAllWashingTYpe());
				}
				return ResponseEntity.status(500)
						.body(new ReturnResponse(AppStatus.UNABLE_TO_CREATE, AppStatusCode.INTERNAL_ERROR));
	
			//}
		}
	}
