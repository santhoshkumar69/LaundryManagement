/**
 * 
 */
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
import com.laundry.LaundryManagement.model.ItemMaster;
import com.laundry.LaundryManagement.service.MasterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.laundry.LaundryManagement.beans.ItemMasterBean;

/**
 * @author pandyarajan
 *
 *         26-Dec-2018
 */
@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600)
@RestController
@RequestMapping("master")
public class ItemMasterController {

	@Autowired
    MasterService masterService;
	
	@GetMapping("/testEnd")
	public ResponseEntity<String> testEndPoint(){
		return ResponseEntity.ok().body("Hello...! welcome to Launtry app");
	}
	
	@PostMapping("/createitem")
	public ResponseEntity<Object> createItemDetails(@RequestBody ItemMasterBean itemMasterBean,HttpSession httpSession){
		//CommonUtills.addDefaultCreateInfos(itemInfo,itemInfo.getClass(),(GlobalInfo)httpSession.getAttribute(AppConstants.SESSION_OBJ));
		
		ItemMaster aItemMaster = null;
		if(itemMasterBean.getItemName() != null) {
			String itemName = itemMasterBean.getItemName();
			aItemMaster = masterService.findByItem(itemName);
		} if(aItemMaster == null && itemMasterBean.getItemCode() != null && !itemMasterBean.getItemCode().isEmpty()) {
				String code = itemMasterBean.getItemCode();
				aItemMaster=masterService.findByItemCode(code);
		} if(aItemMaster != null) {
				return ResponseEntity.status(500).body(new ReturnResponse(AppStatus.ID_ALREDY_EXIST, AppStatusCode.INTERNAL_ERROR));
		} else {
			itemMasterBean.setActiveStatus(1);
			boolean isCreated = masterService.createItemMaster(itemMasterBean);
		if(isCreated) {
			return ResponseEntity.ok().body(masterService.fetchItemDetails());
		}
		return	ResponseEntity.status(500).body(new ReturnResponse(AppStatus.UNABLE_TO_CREATE,AppStatusCode.INTERNAL_ERROR));
		}
	}
	
	@PostMapping("/updateitem")
	public ResponseEntity<Object> updateItemDetails(@RequestBody ItemMasterBean itemMasterBean,HttpSession httpSession){	

//		boolean userItemNameChanged = false;
		Integer id=itemMasterBean.getId();
		Optional<ItemMaster> id1 = masterService.findById(id);
		if(id1 == null) {
			return ResponseEntity.notFound().build();
		}
//		if(itemMasterBean.getItemName() != null && !id1.get().getItemName().equals(itemMasterBean.getItemName())) {
//	    	String newItemName=itemMasterBean.getItemName();
//	    	ItemMaster aItemMaster=masterService.findByItem(newItemName);
//	    	if(aItemMaster != null) {
//	    		userItemNameChanged = true;
//	    	}
//	    } if(userItemNameChanged) {
//			return ResponseEntity.status(500).body(new ReturnResponse(AppStatus.ID_ALREDY_EXIST,AppStatusCode.INTERNAL_ERROR));
//		}
//		ItemMaster itemMaster=id1.get();
//		
//		itemMaster.setItemCode(itemMasterBean.getItemCode());
//		itemMaster.setItemCategory(itemInfo.getItemCategory());
//		itemMaster.setItemName(itemMasterBean.getItemName());
//		itemMaster.setItemAltName(itemInfo.getItemAltName());
//		itemMaster.setIroningRate(itemInfo.getIroningRate());
//		itemMaster.setLaundryRate(itemInfo.getLaundryRate());
//		itemMaster.setUnit(itemInfo.getUnit());
//		itemMaster.setCreatedBy(itemMaster.getCreatedBy());
//		itemMaster.setCreatedDate(itemMaster.getCreatedDate());
//		CommonUtills.addDefaultUpdateInfos(itemMaster,itemMaster.getClass(),(GlobalInfo)httpSession.getAttribute(AppConstants.SESSION_OBJ));
		boolean isCreated = masterService.createItemMaster(itemMasterBean);
		if(isCreated){
			return ResponseEntity.ok().body(masterService.fetchItemDetails());
		}
		return	ResponseEntity.status(500).body(new ReturnResponse(AppStatus.UNABLE_TO_CREATE,AppStatusCode.INTERNAL_ERROR));
	}
	
	@PostMapping("/deactivateitem")
	public ResponseEntity<Object> deactivateItemDetails(@RequestParam("theItemIDs") Integer[] theItemIDs, HttpSession httpSession) {
		List<ItemMasterBean> theItemMasterBean = new ArrayList<ItemMasterBean>();
		try {
			for(int itemID : theItemIDs)  {
				ItemMaster aItemMaster = masterService.findByIdforDeactive(itemID);
				if(aItemMaster == null) {
					ItemMasterBean itemMasterBean = new ItemMasterBean();
					itemMasterBean.setId(itemID);
					itemMasterBean.setErrors(AppStatus.ID_NOT_EXIST);
					theItemMasterBean.add(itemMasterBean);
				} else if(aItemMaster.getActiveStatus() == 0) {
					ItemMasterBean itemMasterBean = new ItemMasterBean();
					itemMasterBean.setId(itemID);
					itemMasterBean.setErrors(AppStatus.ID_ALREADY_DEACTIVATED);
					theItemMasterBean.add(itemMasterBean);
				} else {
					ItemMasterBean itemMasterBean = new ItemMasterBean();
					CommonUtills.addDefaultUpdateInfos(aItemMaster, aItemMaster.getClass(),(GlobalInfo)httpSession.getAttribute(AppConstants.SESSION_OBJ) );
					aItemMaster.setActiveStatus(0);
					masterService.saveItemMaster(aItemMaster);
					itemMasterBean.setActiveStatus(0);
					itemMasterBean.setStatus(AppConstants.DEACTIVATED);
					itemMasterBean.setMessage(AppStatus.DEACTIVATED_SUCCESSFULLY);
					theItemMasterBean.add(itemMasterBean);
				}
			}
			return ResponseEntity.ok().body(theItemMasterBean);
		} catch (Exception e) {
			return ResponseEntity.status(500).body(new ReturnResponse(AppStatus.SOME_ERROR_OCCURRED,AppStatusCode.INTERNAL_ERROR));
		}
		
	}
	
	
	@GetMapping("/getItemDetails")
	public ResponseEntity<Object> getItemDetails(HttpSession httpSession)
	{
		return ResponseEntity.ok().body(masterService.fetchItemDetails());
		
	}
	
	@PostMapping("/getOneItemDetails")
	public ResponseEntity<Object> getOneItemDetails(@RequestParam("itemId") Integer itemId, HttpSession httpSession) {
		
		
		return ResponseEntity.ok().body(masterService.findOne(itemId));
	}
	
	@GetMapping("/getDeActiveItemDetails")
	public ResponseEntity<Object> deActiveItemsDetails()
	{
		return ResponseEntity.ok().body(masterService.getDeactiveItems());
		
	}
	
	@PostMapping("/activateItem")
	public ResponseEntity<Object> activeStatausItem(@RequestParam("theItemIDs") Integer[] theItemIDs, HttpSession httpSession) {
		List<ItemMasterBean> theItemMasterBean = new ArrayList<ItemMasterBean>();
		try {
			for(int itemID : theItemIDs)  {
				ItemMaster aItemMaster = masterService.findByIdforDeactive(itemID);
				if(aItemMaster == null) {
					ItemMasterBean itemMasterBean = new ItemMasterBean();
					itemMasterBean.setId(itemID);
					itemMasterBean.setErrors(AppStatus.ID_NOT_EXIST);
					theItemMasterBean.add(itemMasterBean);
				} else if(aItemMaster.getActiveStatus() == 1) {
					ItemMasterBean itemMasterBean = new ItemMasterBean();
					itemMasterBean.setId(itemID);
					itemMasterBean.setErrors(AppStatus.ID_ALREADY_ACTIVATED);
					theItemMasterBean.add(itemMasterBean);
				} else {
					ItemMasterBean itemMasterBean = new ItemMasterBean();
					CommonUtills.addDefaultUpdateInfos(aItemMaster, aItemMaster.getClass(),(GlobalInfo)httpSession.getAttribute(AppConstants.SESSION_OBJ) );
					aItemMaster.setActiveStatus(1);
					masterService.saveItemMaster(aItemMaster);
					itemMasterBean.setActiveStatus(1);
					itemMasterBean.setStatus(AppConstants.ACTIVATED);
					itemMasterBean.setMessage(AppStatus.ACTIVATED_SUCCESSFULLY);
					theItemMasterBean.add(itemMasterBean);
				}
			}
			return ResponseEntity.ok().body(theItemMasterBean);
		} catch (Exception e) {
			return ResponseEntity.status(500).body(new ReturnResponse(AppStatus.SOME_ERROR_OCCURRED,AppStatusCode.INTERNAL_ERROR));
		}
		
	}
	
}
