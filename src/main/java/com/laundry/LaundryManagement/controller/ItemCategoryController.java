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
import com.laundry.LaundryManagement.model.ItemCategory;
import com.laundry.LaundryManagement.service.ItemCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.laundry.LaundryManagement.beans.ItemCategoryResponse;


@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600)
@RestController
@RequestMapping("itemCategory")
public class ItemCategoryController {
	
	@Autowired
    ItemCategoryService itemCategoryService;
	
	

	@GetMapping("/fetchCategory")
	public ResponseEntity<Object> getItemCategory(HttpSession httpSession) {
			return ResponseEntity.ok().body(itemCategoryService.fetchItemCategoryDetails());
	}
	
	@PostMapping("/createItemCategory")
	public ResponseEntity<Object> createItemCategory(@RequestBody ItemCategory itemCategory, HttpSession httpSession) {
		CommonUtills.addDefaultCreateInfos(itemCategory, itemCategory.getClass(),
				(GlobalInfo) httpSession.getAttribute(AppConstants.SESSION_OBJ));
		ItemCategory aItemCategory = null;
		if (itemCategory.getCategoryName() != null) {
			String name = itemCategory.getCategoryName();
			aItemCategory = itemCategoryService.findByCategoryName(name);
		}
		if (aItemCategory == null && !itemCategory.getCategoryCode().isEmpty()) {
			String code = itemCategory.getCategoryCode();
			aItemCategory = itemCategoryService.findByCategoryCode(code);
		}
		if (aItemCategory != null) {
			return ResponseEntity.status(500)
					.body(new ReturnResponse(AppStatus.ID_ALREDY_EXIST, AppStatusCode.INTERNAL_ERROR));
		} else {
			itemCategory.setActiveStatus(1);
			boolean isCreated = itemCategoryService.createItemCategory(itemCategory);

			if (isCreated) {
				return ResponseEntity.ok().body(itemCategoryService.fetchItemCategoryDetails());
			}
			return ResponseEntity.status(500)
					.body(new ReturnResponse(AppStatus.UNABLE_TO_CREATE, AppStatusCode.INTERNAL_ERROR));
		  }
		}
	
	@PostMapping("/updateItemsCategory")
	public ResponseEntity<Object> updateItemCategoryDetails(@RequestBody ItemCategory itemCategoryFromUI, HttpSession httpSession) {
		boolean categoryIdChanged = false;
		Integer id = itemCategoryFromUI.getId();
		Optional<ItemCategory>itemcategoryFromDb = itemCategoryService.findOne(id);
		ItemCategory itemCategory = null;
		if(itemcategoryFromDb == null) {
			return ResponseEntity.notFound().build();
		}
		if(itemCategoryFromUI.getId() != null && !itemcategoryFromDb.get().getId().equals(itemCategoryFromUI.getId())) {
			categoryIdChanged = true;
		}
		else {
			if (itemCategoryFromUI.getCategoryCode() != null
					&& !itemcategoryFromDb.get().getCategoryCode().equals(itemCategoryFromUI.getCategoryCode())) {
				String categoryCode = itemCategoryFromUI.getCategoryCode();
				itemCategory  = itemCategoryService.findByCategoryCode(categoryCode);
				if(itemCategory != null) {
					return ResponseEntity.status(500).body(new ReturnResponse(AppStatus.CATEGORY_CODE_ALREDY_EXIST,AppStatusCode.INTERNAL_ERROR));
				}
			}
			if (itemCategoryFromUI.getCategoryName() != null
					&& !itemcategoryFromDb.get().getCategoryName().equals(itemCategoryFromUI.getCategoryName())) {
				String categoryName = itemCategoryFromUI.getCategoryName();
				itemCategory  = itemCategoryService.findByCategoryName(categoryName);
			}
			if(categoryIdChanged || itemCategory != null) {
				return ResponseEntity.status(500).body(new ReturnResponse(AppStatus.CATEGORY_NAME_ALREDY_EXIST,AppStatusCode.INTERNAL_ERROR));
			}
		}
		ItemCategory aItemCategory = itemcategoryFromDb.get();
		aItemCategory.setCategoryCode(itemCategoryFromUI.getCategoryCode());
		aItemCategory.setCategoryName(itemCategoryFromUI.getCategoryName());
		aItemCategory.setCreatedBy(aItemCategory.getCreatedBy());
		aItemCategory.setCreatedDate(aItemCategory.getCreatedDate());		
		CommonUtills.addDefaultUpdateInfos(aItemCategory,aItemCategory.getClass(),(GlobalInfo)httpSession.getAttribute(AppConstants.SESSION_OBJ));
		boolean isCreated = itemCategoryService.createItemCategory(aItemCategory);
		if (isCreated) {
			return ResponseEntity.ok().body(itemCategoryService.fetchItemCategoryDetails());
		}
		return ResponseEntity.status(500)
				.body(new ReturnResponse(AppStatus.UNABLE_TO_CREATE, AppStatusCode.INTERNAL_ERROR));
	}

	@PostMapping("/deActiveItemCategory")
	public ResponseEntity<Object> deactivateItemCategory(@RequestParam Integer[] theCategoryIds ,HttpSession httpSession) {
		try {
			List<ItemCategoryResponse> theCategoryResponses = new ArrayList<ItemCategoryResponse>();
			for(Integer categoryId : theCategoryIds) {
				ItemCategory aItemCategory = itemCategoryService.findById(categoryId);
				if(aItemCategory == null) {
					ItemCategoryResponse itemCategoryResponse = new ItemCategoryResponse();
					itemCategoryResponse.setErrors(AppStatus.ID_NOT_EXIST);
					itemCategoryResponse.setId(categoryId);
					theCategoryResponses.add(itemCategoryResponse);
					
				} else if(aItemCategory.getActiveStatus() == 0 ) {
					ItemCategoryResponse itemCategoryResponse = new ItemCategoryResponse();
					itemCategoryResponse.setErrors(AppStatus.ID_ALREADY_DEACTIVATED);
					itemCategoryResponse.setStatus(AppConstants.DEACTIVATED);
					itemCategoryResponse.setId(categoryId);
					itemCategoryResponse.setCategoryName(aItemCategory.getCategoryName());
					itemCategoryResponse.setCategoryCode(aItemCategory.getCategoryCode());
					theCategoryResponses.add(itemCategoryResponse);
				} else {
					ItemCategoryResponse itemCategoryResponse = new ItemCategoryResponse();
					CommonUtills.addDefaultUpdateInfos(aItemCategory, aItemCategory.getClass(),(GlobalInfo)httpSession.getAttribute(AppConstants.SESSION_OBJ) );
					aItemCategory.setActiveStatus(0);
					itemCategoryService.createItemCategory(aItemCategory);
					itemCategoryResponse.setStatus(AppConstants.DEACTIVATED);
					itemCategoryResponse.setActiveStatus(0);
					itemCategoryResponse.setCategoryCode(aItemCategory.getCategoryCode());
					itemCategoryResponse.setCategoryName(aItemCategory.getCategoryName());
					itemCategoryResponse.setId(aItemCategory.getId());
					itemCategoryResponse.setMessage(AppStatus.DEACTIVATED_SUCCESSFULLY);
					theCategoryResponses.add(itemCategoryResponse);
				}
			}
			return ResponseEntity.ok().body(theCategoryResponses);
		} catch(Exception e) {
			return ResponseEntity.status(500).body(new ReturnResponse(AppStatus.SOME_ERROR_OCCURRED,AppStatusCode.INTERNAL_ERROR));
		}
	}
	
	@PostMapping("/activateItemCategory")
	public ResponseEntity<Object> activateItemCategory(@RequestParam Integer[] theCategoryIds ,HttpSession httpSession) {
		try {
			List<ItemCategoryResponse> theCategoryResponses = new ArrayList<ItemCategoryResponse>();
			for(Integer categoryId : theCategoryIds) {
				ItemCategory aItemCategory = itemCategoryService.findById(categoryId);
				if(aItemCategory == null) {
					ItemCategoryResponse itemCategoryResponse = new ItemCategoryResponse();
					
					itemCategoryResponse.setErrors(AppStatus.ID_NOT_EXIST);
					itemCategoryResponse.setId(categoryId);
					theCategoryResponses.add(itemCategoryResponse);	
				} else if(aItemCategory.getActiveStatus() == 1) {
					ItemCategoryResponse itemCategoryResponse = new ItemCategoryResponse();
					
					itemCategoryResponse.setStatus(AppConstants.ACTIVATED);
					itemCategoryResponse.setMessage(AppStatus.ID_ALREADY_ACTIVATED);
					itemCategoryResponse.setId(categoryId);
					itemCategoryResponse.setActiveStatus(1);
					itemCategoryResponse.setCategoryCode(aItemCategory.getCategoryCode());
					itemCategoryResponse.setCategoryName(aItemCategory.getCategoryName());
					theCategoryResponses.add(itemCategoryResponse);	
				} else {
					ItemCategoryResponse itemCategoryResponse = new ItemCategoryResponse();
					
					CommonUtills.addDefaultUpdateInfos(aItemCategory, aItemCategory.getClass(),(GlobalInfo)httpSession.getAttribute(AppConstants.SESSION_OBJ) );
					itemCategoryResponse.setStatus(AppConstants.ACTIVATED);
					itemCategoryResponse.setMessage(AppStatus.ACTIVATED_SUCCESSFULLY);
					aItemCategory.setActiveStatus(1);
					itemCategoryResponse.setId(categoryId);
					itemCategoryResponse.setActiveStatus(1);
					itemCategoryResponse.setCategoryCode(aItemCategory.getCategoryCode());
					itemCategoryResponse.setCategoryName(aItemCategory.getCategoryName());
					itemCategoryService.createItemCategory(aItemCategory);
					theCategoryResponses.add(itemCategoryResponse);
				}
			}
			return ResponseEntity.ok().body(theCategoryResponses);
		} catch (Exception e) {
			return ResponseEntity.status(500).body(new ReturnResponse(AppStatus.SOME_ERROR_OCCURRED,AppStatusCode.INTERNAL_ERROR));
		}
	}
	
	@GetMapping("/getDeactiveCategoryDetails")
	public ResponseEntity<Object> getDeActiveCategoryInfoDetails(HttpSession httpSession)
	{
		return ResponseEntity.ok().body(itemCategoryService.fetchAllDeActiveItemCategory());
			
      }

	
}
