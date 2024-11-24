package com.laundry.LaundryManagement.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpSession;

import com.laundry.LaundryManagement.constant.AppStatus;
import com.laundry.LaundryManagement.constant.AppStatusCode;
import com.laundry.LaundryManagement.dto.ReturnResponse;
import com.laundry.LaundryManagement.model.GlobalInfo;
import com.laundry.LaundryManagement.model.Unit;
import com.laundry.LaundryManagement.service.ServeUnitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.laundry.LaundryManagement.common.CommonUtills;
import com.laundry.LaundryManagement.constant.AppConstants;
import com.laundry.LaundryManagement.beans.ServeUnitResponse;

@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600)
@RestController
@RequestMapping("unit")
public class ServeUnitController {
	
	@Autowired
    ServeUnitService serveUnitService;
	
	@GetMapping("/fetchUnit")
	public ResponseEntity<Object> getUnit(HttpSession httpSession) {
			return ResponseEntity.ok().body(serveUnitService.fetchUnitDetails());
	}
	
	@PostMapping("/deActiveUnit")
	public ResponseEntity<Object> deactivateUnit(@RequestParam Integer[] ids, HttpSession httpSession) {
		try {
			List<ServeUnitResponse> theServeUnitResponse = new ArrayList<ServeUnitResponse>();
			for(Integer id : ids) {
				Unit aUnit = serveUnitService.findById(id);
				if(aUnit == null) {
					ServeUnitResponse serveUnitResponse = new ServeUnitResponse();
					serveUnitResponse.setErrors(AppStatus.ID_NOT_EXIST);
					serveUnitResponse.setId(id);
					theServeUnitResponse.add(serveUnitResponse);
				} else if(aUnit.getActiveStatus() == 0) {
					ServeUnitResponse serveUnitResponse = new ServeUnitResponse();
					serveUnitResponse.setId(id);
					serveUnitResponse.setUnit(aUnit.getUnit());
					serveUnitResponse.setErrors(AppStatus.ID_ALREADY_DEACTIVATED);
					serveUnitResponse.setStatus(AppConstants.DEACTIVATED);
					theServeUnitResponse.add(serveUnitResponse);
				} else {
					ServeUnitResponse serveUnitResponse = new ServeUnitResponse();
					//CommonUtills.addDefaultUpdateInfos(aUnit, aUnit.getClass(),(GlobalInfo)httpSession.getAttribute(AppConstants.SESSION_OBJ) );
					CommonUtills.addDefaultUpdateInfos(aUnit, aUnit.getClass(), (GlobalInfo)httpSession.getAttribute(AppConstants.SESSION_OBJ));
					aUnit.setActiveStatus(0);
					serveUnitService.createUnit(aUnit);
					serveUnitResponse.setActiveStatus(0);
					serveUnitResponse.setId(id);
					serveUnitResponse.setUnit(aUnit.getUnit());
					serveUnitResponse.setMessage(AppStatus.DEACTIVATED_SUCCESSFULLY);
					serveUnitResponse.setStatus(AppConstants.DEACTIVATED);
					theServeUnitResponse.add(serveUnitResponse);
				}
			}
			return ResponseEntity.ok().body(theServeUnitResponse);
		} catch(Exception e) {
			return ResponseEntity.status(500).body(new ReturnResponse(AppStatus.SOME_ERROR_OCCURRED, AppStatusCode.INTERNAL_ERROR));
		}
		
	}
	
	@PostMapping("/activateUnit")
	public ResponseEntity<Object> activateUnit(@RequestParam Integer[] ids, HttpSession httpSession) {
		try {
			List<ServeUnitResponse> theServeUnitResponse = new ArrayList<ServeUnitResponse>();
			for(Integer id : ids) {
				Unit aUnit = serveUnitService.findById(id);
				if(aUnit == null) {
					ServeUnitResponse serveUnitResponse = new ServeUnitResponse();
					serveUnitResponse.setErrors(AppStatus.ID_NOT_EXIST);
					serveUnitResponse.setId(id);
					theServeUnitResponse.add(serveUnitResponse);	
				} else if(aUnit.getActiveStatus() == 1) {
					ServeUnitResponse serveUnitResponse = new ServeUnitResponse();
					serveUnitResponse.setErrors(AppStatus.ID_ALREADY_ACTIVATED);
					serveUnitResponse.setStatus(AppConstants.ACTIVATED);
					theServeUnitResponse.add(serveUnitResponse);
				} else {
					ServeUnitResponse serveUnitResponse = new ServeUnitResponse();
					CommonUtills.addDefaultUpdateInfos(aUnit, aUnit.getClass(), (GlobalInfo)httpSession.getAttribute(AppConstants.SESSION_OBJ));
					aUnit.setActiveStatus(1);
					serveUnitService.createUnit(aUnit);
					serveUnitResponse.setActiveStatus(1);
					serveUnitResponse.setId(id);
					serveUnitResponse.setUnit(aUnit.getUnit());
					serveUnitResponse.setMessage(AppStatus.ACTIVATED_SUCCESSFULLY);
					serveUnitResponse.setStatus(AppConstants.ACTIVATED);
				}
			}
			return ResponseEntity.ok().body(theServeUnitResponse);
		} catch (Exception e) {
			return ResponseEntity.status(500).body(new ReturnResponse(AppStatus.SOME_ERROR_OCCURRED,AppStatusCode.INTERNAL_ERROR));
		}
		
	}
	

	@GetMapping("/getDeactiveUnit")
	public ResponseEntity<Object> getDeActiveUnit(HttpSession httpSession)
	{
		return ResponseEntity.ok().body(serveUnitService.fetchAllDeActiveUnit());
			
      }

	@PostMapping("/createUnit")
	public ResponseEntity<Object> createUnit(@RequestBody Unit unit, HttpSession httpSession) {
		CommonUtills.addDefaultCreateInfos(unit, unit.getClass(),
				(GlobalInfo) httpSession.getAttribute(AppConstants.SESSION_OBJ));
		Unit aUnit = null;
		if(unit.getUnit()!= null) {
			String name = unit.getUnit();
			aUnit=serveUnitService.findByUnit(name);
		} if (aUnit != null) {
			return ResponseEntity.status(500)
					.body(new ReturnResponse(AppStatus.ID_ALREDY_EXIST, AppStatusCode.INTERNAL_ERROR));
		} else {
			unit.setActiveStatus(1);
			boolean isCreated = serveUnitService.createUnit(unit);
			if (isCreated) {
				return ResponseEntity.ok().body(serveUnitService.fetchUnitDetails());
			}
			return ResponseEntity.status(500)
					.body(new ReturnResponse(AppStatus.UNABLE_TO_CREATE, AppStatusCode.INTERNAL_ERROR));
		  }
		}
	
	
	@PostMapping("/updateUnit")
	public ResponseEntity<Object> updateUnit(@RequestBody Unit unitFromUI, HttpSession httpSession) {
		boolean unitIdChanged = false;
		Integer id = unitFromUI.getId();
		Optional<Unit>unitFromDb = serveUnitService.findOne(id);
		Unit unit = null;
		if(unitFromDb == null) {
			return ResponseEntity.notFound().build();
		}
		if(unitFromUI.getId() != null && !unitFromDb.get().getId().equals(unitFromUI.getId())) {
			unitIdChanged = true;
		}
		else {
			
			if (unitFromUI.getUnit() != null
					&& !unitFromDb.get().getUnit().equals(unitFromUI.getUnit())) {
				String unitName = unitFromUI.getUnit();
				unit  = serveUnitService.findByUnit(unitName);
			}
			if(unitIdChanged || unit != null) {
				return ResponseEntity.status(500).body(new ReturnResponse(AppStatus.UNIT_ALREDY_EXIST,AppStatusCode.INTERNAL_ERROR));
			}
		}
		Unit aUnit = unitFromDb.get();

		aUnit.setUnit(unitFromUI.getUnit());
		aUnit.setCreatedBy(aUnit.getCreatedBy());
		aUnit.setCreatedDate(aUnit.getCreatedDate());		
		CommonUtills.addDefaultUpdateInfos(aUnit,aUnit.getClass(),(GlobalInfo)httpSession.getAttribute(AppConstants.SESSION_OBJ));
		boolean isCreated = serveUnitService.createUnit(aUnit);
		if (isCreated) {
			return ResponseEntity.ok().body(serveUnitService.fetchUnitDetails());
		}
		return ResponseEntity.status(500)
				.body(new ReturnResponse(AppStatus.UNABLE_TO_CREATE, AppStatusCode.INTERNAL_ERROR));
	}
	
}
